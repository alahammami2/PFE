# Dockerfile pour Gateway Service - SprintBot
# Point d'entrée unique pour l'écosystème microservices

# Stage 1: Build stage avec Maven
FROM eclipse-temurin:21-jdk-alpine AS builder

# Métadonnées du conteneur
LABEL maintainer="SprintBot Team <infrastructure@sprintbot.com>"
LABEL description="API Gateway pour l'écosystème microservices SprintBot"
LABEL version="1.0.0"

# Installation des outils nécessaires
RUN apk add --no-cache \
    maven \
    git \
    curl \
    && rm -rf /var/cache/apk/*

# Configuration de l'environnement de build
ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository -Xmx1024m -XX:MaxMetaspaceSize=256m"
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# Création du répertoire de travail
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml ./

# Téléchargement des dépendances (cache Docker)
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copie du code source
COPY src/ src/

# Build de l'application
RUN mvn clean package -DskipTests -B --no-transfer-progress && \
    mkdir -p target/dependency && \
    cd target/dependency && \
    jar -xf ../gateway-service.jar

# Vérification du build
RUN ls -la target/ && \
    echo "✅ Build terminé avec succès"

# Stage 2: Runtime stage avec JRE optimisé
FROM eclipse-temurin:21-jre-alpine AS runtime

# Métadonnées du conteneur runtime
LABEL maintainer="SprintBot Team <infrastructure@sprintbot.com>"
LABEL description="Gateway Service Runtime - SprintBot"
LABEL version="1.0.0"

# Installation des outils runtime nécessaires
RUN apk add --no-cache \
    curl \
    netcat-openbsd \
    bash \
    tzdata \
    && rm -rf /var/cache/apk/*

# Configuration du timezone
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 sprintbot && \
    adduser -D -s /bin/bash -u 1001 -G sprintbot sprintbot

# Configuration des répertoires
WORKDIR /app
RUN mkdir -p /app/logs /app/config /app/tmp && \
    chown -R sprintbot:sprintbot /app

# Copie des artefacts depuis le stage de build
COPY --from=builder --chown=sprintbot:sprintbot /app/target/dependency/BOOT-INF/lib /app/lib
COPY --from=builder --chown=sprintbot:sprintbot /app/target/dependency/META-INF /app/META-INF
COPY --from=builder --chown=sprintbot:sprintbot /app/target/dependency/BOOT-INF/classes /app

# Copie du script d'entrée
COPY --chown=sprintbot:sprintbot docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Configuration des variables d'environnement
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"
ENV GATEWAY_INSTANCE_HOSTNAME=gateway-service

# Variables d'environnement pour Eureka
ENV EUREKA_CLIENT_SERVICE_URL=http://discovery-service:8761/eureka/

# Variables d'environnement pour Redis
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV REDIS_DATABASE=0

# Variables d'environnement pour JWT
ENV JWT_SECRET_KEY=SprintBot-Gateway-Secret-Key-2024-Very-Long-And-Secure
ENV JWT_EXPIRATION_TIME=86400000
ENV JWT_REFRESH_EXPIRATION_TIME=604800000

# Variables d'environnement pour Rate Limiting
ENV RATE_LIMIT_REQUESTS_PER_SECOND=100
ENV RATE_LIMIT_BURST_CAPACITY=200

# Variables d'environnement pour les logs
ENV LOGGING_LEVEL_GATEWAY=INFO
ENV LOGGING_LEVEL_GATEWAY_FRAMEWORK=INFO
ENV LOGGING_LEVEL_SECURITY=WARN

# Exposition du port
EXPOSE 8080

# Configuration des volumes
VOLUME ["/app/logs", "/app/config"]

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Changement vers l'utilisateur non-root
USER sprintbot

# Point d'entrée
ENTRYPOINT ["/app/docker-entrypoint.sh"]

# Commande par défaut
CMD ["java", \
     "-cp", "/app:/app/lib/*", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
     "-Dserver.port=${SERVER_PORT}", \
     "-Deureka.client.service-url.defaultZone=${EUREKA_CLIENT_SERVICE_URL}", \
     "-Dspring.data.redis.host=${REDIS_HOST}", \
     "-Dspring.data.redis.port=${REDIS_PORT}", \
     "-Djwt.secret=${JWT_SECRET_KEY}", \
     "-Dlogging.level.com.sprintbot.gateway=${LOGGING_LEVEL_GATEWAY}", \
     "com.sprintbot.gateway.GatewayServiceApplication"]
