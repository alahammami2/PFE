# Script de déploiement SprintBot pour Windows PowerShell
# Usage: .\deploy.ps1 [environment]

param(
    [string]$Environment = "development"
)

$ErrorActionPreference = "Stop"

# Variables
$ProjectName = "sprintbot"
$ComposeFile = "docker-compose.yml"

Write-Host "🚀 Déploiement de SprintBot - Environnement: $Environment" -ForegroundColor Green

# Fonction de logging
function Write-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor Cyan
}

# Vérification des prérequis
function Test-Prerequisites {
    Write-Log "Vérification des prérequis..."
    
    try {
        docker --version | Out-Null
        Write-Log "✅ Docker trouvé"
    }
    catch {
        Write-Host "❌ Docker n'est pas installé ou n'est pas dans le PATH" -ForegroundColor Red
        Write-Host "Veuillez installer Docker Desktop pour Windows" -ForegroundColor Yellow
        Write-Host "https://docs.docker.com/desktop/install/windows-install/" -ForegroundColor Yellow
        exit 1
    }
    
    try {
        docker-compose --version | Out-Null
        Write-Log "✅ Docker Compose trouvé"
    }
    catch {
        Write-Host "❌ Docker Compose n'est pas installé" -ForegroundColor Red
        exit 1
    }
    
    Write-Log "✅ Prérequis vérifiés"
}

# Nettoyage des anciens conteneurs
function Invoke-Cleanup {
    Write-Log "Nettoyage des anciens conteneurs..."
    
    try {
        docker-compose -f $ComposeFile down --remove-orphans 2>$null
        docker system prune -f 2>$null
        Write-Log "✅ Nettoyage terminé"
    }
    catch {
        Write-Log "⚠️ Erreur lors du nettoyage (normal si première exécution)"
    }
}

# Build des images
function Build-Images {
    Write-Log "Construction des images Docker..."
    
    # Build backend
    Write-Log "Construction de l'image backend..."
    Set-Location "backend\SprintBot"
    docker build -t "${ProjectName}-backend:latest" .
    Set-Location "..\..\"
    
    # Build frontend
    Write-Log "Construction de l'image frontend..."
    Set-Location "frontend\dashboard-angular"
    docker build -t "${ProjectName}-frontend:latest" .
    Set-Location "..\..\"
    
    Write-Log "✅ Images construites avec succès"
}

# Démarrage des services
function Start-Services {
    Write-Log "Démarrage des services..."
    
    # Démarrer la base de données en premier
    docker-compose -f $ComposeFile up -d postgres
    
    # Attendre que PostgreSQL soit prêt
    Write-Log "Attente de PostgreSQL..."
    Start-Sleep -Seconds 30
    
    # Démarrer le backend
    docker-compose -f $ComposeFile up -d backend
    
    # Attendre que le backend soit prêt
    Write-Log "Attente du backend..."
    Start-Sleep -Seconds 45
    
    # Démarrer le frontend
    docker-compose -f $ComposeFile up -d frontend
    
    # Démarrer nginx si en production
    if ($Environment -eq "production") {
        docker-compose -f $ComposeFile up -d nginx
    }
    
    Write-Log "✅ Services démarrés"
}

# Vérification de l'état des services
function Test-HealthCheck {
    Write-Log "Vérification de l'état des services..."
    
    # Attendre un peu plus pour que tout soit prêt
    Start-Sleep -Seconds 30
    
    # Vérifier PostgreSQL
    try {
        docker-compose -f $ComposeFile exec postgres pg_isready -U sprintbot_user -d sprintbot_db
        Write-Log "✅ PostgreSQL est prêt"
    }
    catch {
        Write-Log "❌ PostgreSQL n'est pas prêt"
    }
    
    # Vérifier le backend
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Log "✅ Backend est prêt"
        }
    }
    catch {
        Write-Log "⚠️ Backend pas encore prêt (peut prendre plus de temps)"
    }
    
    # Vérifier le frontend
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:4200" -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Log "✅ Frontend est prêt"
        }
    }
    catch {
        Write-Log "⚠️ Frontend pas encore prêt"
    }
    
    # Afficher l'état des conteneurs
    Write-Log "État des conteneurs:"
    docker-compose -f $ComposeFile ps
}

# Affichage des informations de déploiement
function Show-DeploymentInfo {
    Write-Log "🎉 Déploiement terminé!"
    Write-Host ""
    Write-Host "📋 Informations de déploiement:" -ForegroundColor Yellow
    Write-Host "  - Frontend: http://localhost:4200" -ForegroundColor White
    Write-Host "  - Backend API: http://localhost:8080" -ForegroundColor White
    Write-Host "  - Health Check Backend: http://localhost:8080/actuator/health" -ForegroundColor White
    Write-Host "  - Console H2: http://localhost:8080/h2-console" -ForegroundColor White
    Write-Host ""
    Write-Host "🐳 Commandes utiles:" -ForegroundColor Yellow
    Write-Host "  - Voir les logs: docker-compose logs -f" -ForegroundColor White
    Write-Host "  - Arrêter: docker-compose down" -ForegroundColor White
    Write-Host "  - Redémarrer: docker-compose restart" -ForegroundColor White
    Write-Host ""
}

# Fonction principale
function Main {
    try {
        Test-Prerequisites
        Invoke-Cleanup
        Build-Images
        Start-Services
        Test-HealthCheck
        Show-DeploymentInfo
    }
    catch {
        Write-Host "❌ Erreur lors du déploiement: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

# Exécution
Main
