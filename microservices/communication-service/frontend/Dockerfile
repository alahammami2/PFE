# Dockerfile pour le frontend du service de communication
# Multi-stage build pour optimiser la taille de l'image

# Stage 1: Build
FROM node:20-alpine AS builder

# Métadonnées
LABEL maintainer="SprintBot Team"
LABEL description="Frontend du service de communication SprintBot"
LABEL version="1.0.0"

# Installation des outils nécessaires
RUN apk add --no-cache git python3 make g++

# Répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration
COPY package*.json ./
COPY angular.json ./
COPY tsconfig*.json ./

# Installation des dépendances
RUN npm install --only=production

# Copier le code source
COPY src/ src/

# Build de l'application
ARG BUILD_CONFIGURATION=docker
RUN npm run build:${BUILD_CONFIGURATION}

# Stage 2: Runtime avec Nginx
FROM nginx:1.25-alpine AS runtime

# Métadonnées
LABEL maintainer="SprintBot Team"
LABEL description="Frontend du service de communication SprintBot - Runtime"
LABEL version="1.0.0"

# Installation des outils nécessaires
RUN apk add --no-cache curl

# Copier la configuration Nginx
COPY nginx.conf /etc/nginx/nginx.conf
COPY docker/default.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers buildés depuis le stage de build
COPY --from=builder /app/dist/communication-service /usr/share/nginx/html

# Copier les scripts
COPY docker/ /docker/
RUN chmod +x /docker/entrypoint.sh

# Variables d'environnement
ENV NGINX_PORT=80
ENV API_URL=http://localhost:8084

# Exposition du port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# Point d'entrée
ENTRYPOINT ["/docker/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
