#!/bin/bash

# Script de déploiement SprintBot
# Usage: ./deploy.sh [environment]

set -e

# Variables
ENVIRONMENT=${1:-development}
PROJECT_NAME="sprintbot"
COMPOSE_FILE="docker-compose.yml"

echo "🚀 Déploiement de SprintBot - Environnement: $ENVIRONMENT"

# Fonction de logging
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

# Vérification des prérequis
check_prerequisites() {
    log "Vérification des prérequis..."
    
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker n'est pas installé"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Docker Compose n'est pas installé"
        exit 1
    fi
    
    log "✅ Prérequis vérifiés"
}

# Nettoyage des anciens conteneurs
cleanup() {
    log "Nettoyage des anciens conteneurs..."
    docker-compose -f $COMPOSE_FILE down --remove-orphans || true
    docker system prune -f || true
    log "✅ Nettoyage terminé"
}

# Build des images
build_images() {
    log "Construction des images Docker..."
    
    # Build backend
    log "Construction de l'image backend..."
    cd backend/SprintBot
    docker build -t ${PROJECT_NAME}-backend:latest .
    cd ../..
    
    # Build frontend
    log "Construction de l'image frontend..."
    cd frontend/dashboard-angular
    docker build -t ${PROJECT_NAME}-frontend:latest .
    cd ../..
    
    log "✅ Images construites avec succès"
}

# Démarrage des services
start_services() {
    log "Démarrage des services..."
    
    # Démarrer la base de données en premier
    docker-compose -f $COMPOSE_FILE up -d postgres
    
    # Attendre que PostgreSQL soit prêt
    log "Attente de PostgreSQL..."
    sleep 30
    
    # Démarrer le backend
    docker-compose -f $COMPOSE_FILE up -d backend
    
    # Attendre que le backend soit prêt
    log "Attente du backend..."
    sleep 45
    
    # Démarrer le frontend
    docker-compose -f $COMPOSE_FILE up -d frontend
    
    # Démarrer nginx si configuré
    if [ "$ENVIRONMENT" = "production" ]; then
        docker-compose -f $COMPOSE_FILE up -d nginx
    fi
    
    log "✅ Services démarrés"
}

# Vérification de l'état des services
health_check() {
    log "Vérification de l'état des services..."
    
    # Attendre un peu plus pour que tout soit prêt
    sleep 30
    
    # Vérifier PostgreSQL
    if docker-compose -f $COMPOSE_FILE exec postgres pg_isready -U sprintbot_user -d sprintbot_db; then
        log "✅ PostgreSQL est prêt"
    else
        log "❌ PostgreSQL n'est pas prêt"
    fi
    
    # Vérifier le backend
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log "✅ Backend est prêt"
    else
        log "⚠️ Backend pas encore prêt (peut prendre plus de temps)"
    fi
    
    # Vérifier le frontend
    if curl -f http://localhost:4200 &> /dev/null; then
        log "✅ Frontend est prêt"
    else
        log "⚠️ Frontend pas encore prêt"
    fi
    
    # Afficher l'état des conteneurs
    log "État des conteneurs:"
    docker-compose -f $COMPOSE_FILE ps
}

# Affichage des informations de déploiement
show_info() {
    log "🎉 Déploiement terminé!"
    echo ""
    echo "📋 Informations de déploiement:"
    echo "  - Frontend: http://localhost:4200"
    echo "  - Backend API: http://localhost:8080"
    echo "  - Health Check Backend: http://localhost:8080/actuator/health"
    echo "  - Console H2: http://localhost:8080/h2-console"
    echo ""
    echo "🐳 Commandes utiles:"
    echo "  - Voir les logs: docker-compose logs -f"
    echo "  - Arrêter: docker-compose down"
    echo "  - Redémarrer: docker-compose restart"
    echo ""
}

# Fonction principale
main() {
    check_prerequisites
    cleanup
    build_images
    start_services
    health_check
    show_info
}

# Gestion des erreurs
trap 'log "❌ Erreur lors du déploiement"; exit 1' ERR

# Exécution
main
