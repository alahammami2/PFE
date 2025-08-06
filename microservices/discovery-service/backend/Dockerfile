# Multi-stage Dockerfile pour Discovery Service (Eureka Server)
# SprintBot - Service de découverte pour l'écosystème microservices

# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

# Métadonnées
LABEL maintainer="SprintBot Team <infrastructure@sprintbot.com>"
LABEL description="Discovery Service (Eureka Server) pour l'écosystème SprintBot"
LABEL version="1.0.0"

# Variables d'environnement pour le build
ENV MAVEN_VERSION=3.9.6
ENV MAVEN_HOME=/usr/share/maven
ENV MAVEN_CONFIG=/root/.m2

# Installation des dépendances système
RUN apk add --no-cache \
    curl \
    wget \
    bash \
    && rm -rf /var/cache/apk/*

# Installation de Maven
RUN wget -q https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /usr/share \
    && mv /usr/share/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} \
    && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Création du répertoire de travail
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .

# Téléchargement des dépendances (cache layer)
RUN mvn dependency:resolve -B

# Copie du code source
COPY src/ src/

# Build de l'application
RUN mvn clean package -DskipTests -B \
    && mv target/discovery-service.jar app.jar

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Métadonnées pour l'image finale
LABEL maintainer="SprintBot Team <infrastructure@sprintbot.com>"
LABEL description="Discovery Service (Eureka Server) - Runtime Image"
LABEL version="1.0.0"
LABEL service="discovery-service"
LABEL component="infrastructure"

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"
ENV SERVER_PORT=8761
ENV EUREKA_INSTANCE_HOSTNAME=discovery-service

# Installation des utilitaires nécessaires
RUN apk add --no-cache \
    curl \
    bash \
    tzdata \
    && rm -rf /var/cache/apk/*

# Configuration du timezone
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S sprintbot && \
    adduser -u 1001 -S sprintbot -G sprintbot

# Création des répertoires nécessaires
RUN mkdir -p /app/logs /app/config /app/tmp \
    && chown -R sprintbot:sprintbot /app

# Copie de l'application depuis le stage de build
COPY --from=builder --chown=sprintbot:sprintbot /app/app.jar /app/

# Copie du script d'entrée
COPY --chown=sprintbot:sprintbot docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Configuration des volumes
VOLUME ["/app/logs", "/app/config"]

# Changement vers l'utilisateur non-root
USER sprintbot

# Répertoire de travail
WORKDIR /app

# Exposition du port
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["./docker-entrypoint.sh"]

# Commande par défaut
CMD ["java", "-jar", "app.jar"]
