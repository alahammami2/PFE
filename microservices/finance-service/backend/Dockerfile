# Dockerfile pour le service finance
# Utilise une approche multi-stage pour optimiser la taille de l'image

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

# Métadonnées
LABEL maintainer="SprintBot Team <dev@sprintbot.com>"
LABEL description="Finance Service - Gestion financière pour SprintBot"
LABEL version="1.0.0"

# Variables d'environnement pour le build
ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -Dorg.slf4j.simpleLogger.showDateTime=true -Djava.awt.headless=true"

# Installation des dépendances système
RUN apk add --no-cache \
    maven \
    curl \
    && rm -rf /var/cache/apk/*

# Création du répertoire de travail
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .

# Téléchargement des dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# Copie du code source
COPY src/ src/

# Build de l'application
RUN mvn clean package -DskipTests -B && \
    mv target/*.jar app.jar

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

# Métadonnées pour l'image finale
LABEL maintainer="SprintBot Team <dev@sprintbot.com>"
LABEL description="Finance Service Runtime - Gestion financière pour SprintBot"
LABEL version="1.0.0"

# Installation des outils nécessaires
RUN apk add --no-cache \
    curl \
    dumb-init \
    tzdata \
    && rm -rf /var/cache/apk/*

# Configuration du timezone
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S sprintbot && \
    adduser -u 1001 -S sprintbot -G sprintbot

# Création des répertoires nécessaires
RUN mkdir -p /app/logs /app/uploads /app/reports && \
    chown -R sprintbot:sprintbot /app

# Définition du répertoire de travail
WORKDIR /app

# Copie de l'application depuis le stage builder
COPY --from=builder --chown=sprintbot:sprintbot /app/app.jar .

# Copie du script de démarrage
COPY --chown=sprintbot:sprintbot docker-entrypoint.sh .
RUN chmod +x docker-entrypoint.sh

# Changement vers l'utilisateur non-root
USER sprintbot

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8085

# Exposition du port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1

# Point d'entrée avec dumb-init pour une gestion correcte des signaux
ENTRYPOINT ["dumb-init", "--"]
CMD ["./docker-entrypoint.sh"]
