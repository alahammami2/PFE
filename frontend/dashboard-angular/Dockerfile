# Étape 1: Build avec Node.js
FROM node:18-alpine AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration npm
COPY package*.json ./

# Installer les dépendances avec legacy-peer-deps pour résoudre les conflits
RUN npm install --legacy-peer-deps

# Copier le code source
COPY . .

# Construire l'application pour la production
RUN npm run build --prod

# Étape 2: Servir avec Nginx
FROM nginx:alpine

# Copier la configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/nginx.conf

# Copier les fichiers buildés depuis l'étape de build
COPY --from=build /app/dist/* /usr/share/nginx/html/

# Exposer le port 80
EXPOSE 80

# Commande de démarrage
CMD ["nginx", "-g", "daemon off;"]
