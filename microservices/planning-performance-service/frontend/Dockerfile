# Dockerfile pour le frontend Angular du service planning-performance
FROM node:18-alpine AS build

WORKDIR /app

# Copier les fichiers de configuration
COPY package*.json ./
RUN npm install

# Copier le code source
COPY . .

# Build de l'application
RUN npm run build

# Stage de production avec Nginx
FROM nginx:alpine

# Copier la configuration Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copier les fichiers buildés
COPY --from=build /app/dist/* /usr/share/nginx/html/

# Exposer le port
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
