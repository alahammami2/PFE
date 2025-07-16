# Guide de Déploiement SprintBot

## 🚀 Déploiement avec Docker et Jenkins

### Prérequis

- Docker 20.10+
- Docker Compose 2.0+
- Jenkins 2.400+
- Git

### Structure du Projet

```
sprintbot/
├── backend/SprintBot/          # Application Spring Boot
│   ├── Dockerfile
│   └── src/main/resources/
│       └── application-docker.properties
├── frontend/dashboard-angular/  # Application Angular
│   ├── Dockerfile
│   └── nginx.conf
├── nginx/                      # Configuration Nginx
│   └── nginx.conf
├── docker-compose.yml          # Orchestration des services
├── Jenkinsfile                 # Pipeline CI/CD
├── deploy.sh                   # Script de déploiement
└── DEPLOYMENT.md              # Ce fichier
```

## 🐳 Déploiement avec Docker Compose

### 1. Déploiement Rapide

```bash
# Cloner le projet
git clone <your-repo-url>
cd sprintbot

# Rendre le script exécutable
chmod +x deploy.sh

# Déployer
./deploy.sh
```

### 2. Déploiement Manuel

```bash
# Construire les images
docker-compose build

# Démarrer les services
docker-compose up -d

# Vérifier l'état
docker-compose ps
```

### 3. Services Déployés

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| Frontend | 4200 | http://localhost:4200 | Interface Angular |
| Backend | 8080 | http://localhost:8080 | API Spring Boot |
| PostgreSQL | 5432 | localhost:5432 | Base de données |
| Nginx | 80 | http://localhost | Reverse proxy |

## 🔧 Configuration Jenkins

### 1. Installation des Plugins Requis

- Docker Pipeline
- NodeJS Plugin
- Maven Integration
- Git Plugin
- Pipeline Stage View

### 2. Configuration des Outils

Dans Jenkins > Manage Jenkins > Global Tool Configuration :

- **Maven** : Nom = `Maven-3.9.6`, Version = `3.9.6`
- **NodeJS** : Nom = `NodeJS-18`, Version = `18.x`

### 3. Configuration des Credentials

Dans Jenkins > Manage Jenkins > Manage Credentials :

- `docker-registry-credentials` : Username/Password pour Docker Registry
- `database-credentials` : Username/Password pour PostgreSQL

### 4. Création du Job Pipeline

1. New Item > Pipeline
2. Pipeline script from SCM
3. SCM = Git
4. Repository URL = votre repo
5. Script Path = `Jenkinsfile`

## 🔍 Monitoring et Logs

### Vérification de l'État

```bash
# État des conteneurs
docker-compose ps

# Logs en temps réel
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Health Checks

- Backend : http://localhost:8080/actuator/health
- Frontend : http://localhost:4200/health
- Global : http://localhost/health

## 🛠️ Commandes Utiles

### Gestion des Services

```bash
# Arrêter tous les services
docker-compose down

# Redémarrer un service
docker-compose restart backend

# Reconstruire et redémarrer
docker-compose up -d --build

# Voir les ressources utilisées
docker stats
```

### Debugging

```bash
# Accéder à un conteneur
docker-compose exec backend bash
docker-compose exec frontend sh

# Voir les logs détaillés
docker-compose logs --tail=100 backend

# Inspecter la configuration
docker-compose config
```

## 🔒 Sécurité

### Variables d'Environnement

Créer un fichier `.env` pour les variables sensibles :

```env
POSTGRES_PASSWORD=your_secure_password
SPRING_SECURITY_PASSWORD=your_admin_password
JWT_SECRET=your_jwt_secret
```

### SSL/TLS (Production)

1. Placer les certificats dans `nginx/ssl/`
2. Modifier `nginx/nginx.conf` pour HTTPS
3. Redémarrer nginx

## 📊 Performance

### Optimisations Recommandées

1. **Backend** :
   - Ajuster `JAVA_OPTS` dans le Dockerfile
   - Configurer la pool de connexions
   - Activer le cache Redis si nécessaire

2. **Frontend** :
   - Build avec `--prod` pour la production
   - Activer la compression gzip
   - Configurer le cache des assets

3. **Base de Données** :
   - Ajuster `shared_buffers` PostgreSQL
   - Configurer les index appropriés
   - Monitoring avec pg_stat_statements

## 🚨 Troubleshooting

### Problèmes Courants

1. **Backend ne démarre pas** :
   ```bash
   # Vérifier les logs
   docker-compose logs backend
   
   # Vérifier la connectivité DB
   docker-compose exec postgres psql -U sprintbot_user -d sprintbot_db
   ```

2. **Frontend erreurs de build** :
   ```bash
   # Nettoyer node_modules
   docker-compose exec frontend rm -rf node_modules
   docker-compose restart frontend
   ```

3. **Problèmes de réseau** :
   ```bash
   # Recréer le réseau
   docker-compose down
   docker network prune
   docker-compose up -d
   ```

## 📈 Mise à l'Échelle

### Scaling Horizontal

```bash
# Scaler le backend
docker-compose up -d --scale backend=3

# Scaler le frontend
docker-compose up -d --scale frontend=2
```

### Load Balancing

Modifier `nginx/nginx.conf` pour ajouter plusieurs instances :

```nginx
upstream backend {
    server backend_1:8080;
    server backend_2:8080;
    server backend_3:8080;
}
```

## 🔄 Mise à Jour

### Déploiement Continu

Le pipeline Jenkins se déclenche automatiquement sur :
- Push sur `main`/`master`
- Push sur `develop`
- Pull Request

### Rollback

```bash
# Revenir à la version précédente
git checkout <previous-commit>
./deploy.sh

# Ou utiliser les tags Docker
docker-compose down
docker tag sprintbot-backend:previous sprintbot-backend:latest
docker-compose up -d
```
