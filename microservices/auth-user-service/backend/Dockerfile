# Dockerfile pour le microservice auth-user-service
# Version simplifiée avec images Eclipse Temurin

FROM eclipse-temurin:17-jdk-alpine AS builder

# Métadonnées de l'image
LABEL maintainer="SprintBot Team <dev@sprintbot.com>"
LABEL description="Auth User Service - Microservice d'authentification et gestion utilisateur"
LABEL version="1.0.0"

# Installation de Maven
RUN apk add --no-cache maven

# Définition du répertoire de travail
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .

# Téléchargement des dépendances (mise en cache des layers Docker)
RUN mvn dependency:go-offline -B

# Copie du code source
COPY src ./src

# Build de l'application
RUN mvn clean package -DskipTests -B

# Vérification que le JAR a été créé
RUN ls -la target/

# Stage 2: Image de production légère
FROM eclipse-temurin:17-jre-alpine

# Installation des outils nécessaires
RUN apk add --no-cache curl netcat-openbsd

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -S sprintbot && adduser -S sprintbot -G sprintbot

# Définition du répertoire de travail
WORKDIR /app

# Copie du JAR depuis le stage de build
COPY --from=builder /app/target/auth-user-service-*.jar app.jar

# Création du répertoire pour les logs
RUN mkdir -p /app/logs && chown -R sprintbot:sprintbot /app

# Changement vers l'utilisateur non-root
USER sprintbot

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8081

# Exposition du port
EXPOSE 8081

# Script de santé pour Docker
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Point d'entrée avec configuration JVM optimisée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# Commande par défaut (peut être surchargée)
CMD []
