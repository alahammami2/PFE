# Guide d'Installation SprintBot sur Windows

## 🖥️ Prérequis Windows

### 1. Installation de Docker Desktop

1. **Télécharger Docker Desktop** :
   - Aller sur https://docs.docker.com/desktop/install/windows-install/
   - Télécharger Docker Desktop pour Windows

2. **Installation** :
   - Exécuter le fichier `.exe` téléchargé
   - Suivre l'assistant d'installation
   - Redémarrer l'ordinateur si demandé

3. **Configuration** :
   - Lancer Docker Desktop
   - Accepter les termes de service
   - Configurer WSL 2 si demandé

4. **Vérification** :
   ```powershell
   docker --version
   docker-compose --version
   ```

### 2. Installation de Git (si pas déjà installé)

1. Télécharger depuis https://git-scm.com/download/win
2. Installer avec les options par défaut
3. Vérifier : `git --version`

### 3. Installation de Jenkins (Optionnel)

#### Option A : Jenkins avec Docker
```powershell
# Créer un volume pour Jenkins
docker volume create jenkins_home

# Lancer Jenkins
docker run -d -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home --name jenkins jenkins/jenkins:lts
```

#### Option B : Installation native
1. Télécharger depuis https://www.jenkins.io/download/
2. Installer le fichier `.msi`
3. Accéder à http://localhost:8080

## 🚀 Déploiement Rapide

### 1. Cloner le Projet
```powershell
git clone <your-repo-url>
cd sprintbot
```

### 2. Déployer avec PowerShell
```powershell
# Exécuter le script de déploiement
.\deploy.ps1

# Ou spécifier l'environnement
.\deploy.ps1 -Environment production
```

### 3. Déploiement Manuel
```powershell
# Construire les images
docker-compose build

# Démarrer les services
docker-compose up -d

# Vérifier l'état
docker-compose ps
```

## 🔧 Configuration Spécifique Windows

### 1. Variables d'Environnement

Créer un fichier `.env` dans le répertoire racine :
```env
# Base de données
POSTGRES_DB=sprintbot_db
POSTGRES_USER=sprintbot_user
POSTGRES_PASSWORD=VotreMotDePasseSecurise

# Backend
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xmx512m -Xms256m

# Frontend
NODE_ENV=production
```

### 2. Configuration Docker Desktop

1. **Ressources** :
   - Ouvrir Docker Desktop
   - Aller dans Settings > Resources
   - Allouer au moins 4GB de RAM
   - Allouer au moins 2 CPU cores

2. **File Sharing** :
   - Aller dans Settings > Resources > File Sharing
   - Ajouter le répertoire du projet si nécessaire

### 3. Configuration Firewall

Si vous avez des problèmes de connexion :
```powershell
# Autoriser Docker dans le firewall Windows
New-NetFirewallRule -DisplayName "Docker Desktop" -Direction Inbound -Protocol TCP -LocalPort 2375,2376,2377,4789,7946 -Action Allow
```

## 🛠️ Commandes PowerShell Utiles

### Gestion des Services
```powershell
# Voir l'état des conteneurs
docker-compose ps

# Voir les logs
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend

# Redémarrer un service
docker-compose restart backend

# Arrêter tous les services
docker-compose down

# Nettoyer les ressources
docker system prune -a
```

### Debugging
```powershell
# Accéder à un conteneur
docker-compose exec backend bash
docker-compose exec frontend sh

# Voir les ressources utilisées
docker stats

# Inspecter un conteneur
docker inspect sprintbot-backend
```

## 🚨 Résolution de Problèmes Windows

### 1. Erreur "Docker daemon not running"
```powershell
# Démarrer Docker Desktop manuellement
# Ou redémarrer le service Docker
Restart-Service docker
```

### 2. Erreur de permissions
```powershell
# Exécuter PowerShell en tant qu'administrateur
# Ou ajouter l'utilisateur au groupe docker-users
Add-LocalGroupMember -Group "docker-users" -Member $env:USERNAME
```

### 3. Problèmes de réseau
```powershell
# Réinitialiser le réseau Docker
docker network prune
docker-compose down
docker-compose up -d
```

### 4. Erreurs de build
```powershell
# Nettoyer le cache Docker
docker builder prune -a

# Reconstruire sans cache
docker-compose build --no-cache
```

### 5. Problèmes de WSL 2
```powershell
# Mettre à jour WSL
wsl --update

# Redémarrer WSL
wsl --shutdown
```

## 📊 Monitoring sur Windows

### 1. Performance Monitor
```powershell
# Voir l'utilisation des ressources
Get-Counter "\Process(docker)\% Processor Time"
Get-Counter "\Process(docker)\Working Set"
```

### 2. Event Viewer
- Ouvrir Event Viewer
- Aller dans Applications and Services Logs > Docker Desktop

### 3. Docker Desktop Dashboard
- Ouvrir Docker Desktop
- Aller dans l'onglet Containers
- Voir les métriques en temps réel

## 🔄 Automatisation avec Task Scheduler

### Créer une Tâche Planifiée
```powershell
# Créer une tâche pour démarrer automatiquement
$action = New-ScheduledTaskAction -Execute "PowerShell.exe" -Argument "-File C:\path\to\your\project\deploy.ps1"
$trigger = New-ScheduledTaskTrigger -AtStartup
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries
Register-ScheduledTask -TaskName "SprintBot-AutoStart" -Action $action -Trigger $trigger -Settings $settings
```

## 🔐 Sécurité Windows

### 1. Antivirus
- Ajouter le répertoire Docker à l'exclusion de l'antivirus
- Exclure les processus Docker

### 2. Windows Defender
```powershell
# Ajouter des exclusions
Add-MpPreference -ExclusionPath "C:\ProgramData\Docker"
Add-MpPreference -ExclusionProcess "docker.exe"
Add-MpPreference -ExclusionProcess "dockerd.exe"
```

## 📱 Accès depuis d'autres appareils

### Configuration réseau
```powershell
# Trouver l'IP de la machine
ipconfig | findstr "IPv4"

# Accéder depuis un autre appareil
# Frontend: http://[IP-MACHINE]:4200
# Backend: http://[IP-MACHINE]:8080
```

## 🎯 Optimisations Windows

### 1. Performance
```powershell
# Augmenter la limite de mémoire virtuelle
# Panneau de configuration > Système > Paramètres système avancés > Variables d'environnement
```

### 2. Stockage
```powershell
# Nettoyer régulièrement
docker system prune -a --volumes

# Déplacer les données Docker (si nécessaire)
# Docker Desktop > Settings > Resources > Advanced
```
