# 🏐 SprintBot - Plateforme Intelligente de Gestion d'Équipe de Volley-Ball

[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://docker.com)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-orange?logo=jenkins)](https://jenkins.io)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-14-red?logo=angular)](https://angular.io)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)](https://postgresql.org)

## 🚀 Démarrage Rapide

### Installation avec Docker (Recommandé)

```bash
# Cloner le repository
git clone https://github.com/alahammami2/PFE.git
cd PFE

# Démarrer avec Docker Compose (Windows)
.\deploy.ps1

# Ou manuellement
docker-compose up -d
```

### Accès aux Services
- **Frontend** : http://localhost:4200
- **Backend API** : http://localhost:8080  
- **Jenkins** : http://localhost:8081
- **Health Check** : http://localhost:8080/actuator/health

## 🏗️ Architecture

### Stack Technique
- **Backend** : Spring Boot 3.2.0 + PostgreSQL 15
- **Frontend** : Angular 14 + Bootstrap 4
- **Infrastructure** : Docker + Jenkins + Nginx
- **CI/CD** : Pipeline Jenkins automatisé

### Services Docker
| Service | Port | Description |
|---------|------|-------------|
| Frontend | 4200 | Interface Angular |
| Backend | 8080 | API Spring Boot |
| PostgreSQL | 5432 | Base de données |
| Jenkins | 8081 | CI/CD Pipeline |

## 📁 Structure du Projet

```
PFE/
├── backend/SprintBot/          # Spring Boot Application
├── frontend/dashboard-angular/ # Angular Application  
├── docker-compose.yml          # Services Orchestration
├── Jenkinsfile                 # CI/CD Pipeline
├── deploy.ps1                  # Windows Deployment
└── nginx/                      # Reverse Proxy Config
```

## 🔧 Configuration Jenkins

```powershell
# Installation automatique
.\setup-jenkins.ps1

# Accès : http://localhost:8081
# Mot de passe initial dans les logs Docker
```

## 📊 Fonctionnalités

### Gestion des Utilisateurs
- 🏐 **Joueurs** - Profils et performances
- 👨‍🏫 **Coaches** - Gestion entraînements  
- 👨‍💼 **Administrateurs** - Gestion système
- 🏥 **Staff Médical** - Suivi médical
- 💰 **Responsables Financiers** - Gestion budgets

### Modules Principaux
- 📅 Planning et entraînements
- 📈 Statistiques et performances
- 💰 Gestion financière
- 🏥 Suivi médical
- 💬 Communication et notifications
- 🤖 Assistant chatbot

## 🛠️ Développement Local

### Backend
```bash
cd backend/SprintBot
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Frontend  
```bash
cd frontend/dashboard-angular
npm install --legacy-peer-deps
npm start
```

## 🚀 Déploiement

### Pipeline CI/CD Automatique
Le Jenkinsfile inclut :
1. ✅ Build Backend (Maven)
2. ✅ Build Frontend (npm)
3. ✅ Tests automatisés
4. ✅ Images Docker
5. ✅ Déploiement automatique

### Commandes Utiles
```bash
# État des services
docker-compose ps

# Logs en temps réel
docker-compose logs -f

# Redémarrer
docker-compose restart

# Arrêter tout
docker-compose down

# Nettoyer
docker system prune -f
```

## 📚 Documentation

- [📖 Guide de Déploiement](DEPLOYMENT.md)
- [🪟 Installation Windows](INSTALLATION_WINDOWS.md)  
- [⚙️ Configuration Manuelle](GUIDE_CONFIGURATION.md)

## 🔐 Sécurité

- 🔐 Authentification JWT
- 👥 Autorisation par rôles
- 🔒 Chiffrement BCrypt
- 🛡️ Protection CSRF
- ✅ Validation des données

## 📈 Monitoring

- ❤️ Health checks automatiques
- 📊 Métriques Spring Actuator
- 📝 Logs centralisés
- 🐳 Monitoring Docker

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/Feature`)
3. Commit (`git commit -m 'Add Feature'`)
4. Push (`git push origin feature/Feature`)
5. Pull Request

## 👥 Équipe

**Développeur Principal** - [@alahammami2](https://github.com/alahammami2)

## 🆘 Support

- 📖 [Documentation](DEPLOYMENT.md)
- 🐛 [Issues GitHub](https://github.com/alahammami2/PFE/issues)
- 💬 Créer une nouvelle issue

---

⭐ **Donnez une étoile si vous aimez le projet !**
