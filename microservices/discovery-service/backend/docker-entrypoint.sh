#!/bin/bash

# Script d'entrée Docker pour Discovery Service (Eureka Server)
# SprintBot - Service de découverte pour l'écosystème microservices

set -e

# Couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonctions utilitaires pour les logs
log_info() {
    echo -e "${BLUE}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# Fonction d'affichage du banner
show_banner() {
    echo ""
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                    🔍 SPRINTBOT DISCOVERY SERVICE             ║"
    echo "║                      Eureka Server v1.0.0                   ║"
    echo "║                                                              ║"
    echo "║  Service de découverte pour l'écosystème microservices      ║"
    echo "║  Infrastructure Team - SprintBot Platform                   ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo ""
}

# Fonction de validation de l'environnement
validate_environment() {
    log_info "🔍 Validation de l'environnement..."
    
    # Vérification de Java
    if ! command -v java &> /dev/null; then
        log_error "Java n'est pas installé ou non disponible dans le PATH"
        exit 1
    fi
    
    local java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_info "☕ Version Java détectée: $java_version"
    
    # Vérification de l'application JAR
    if [ ! -f "/app/app.jar" ]; then
        log_error "Fichier application JAR non trouvé: /app/app.jar"
        exit 1
    fi
    
    log_success "✅ Environnement validé"
}

# Fonction de configuration des variables d'environnement
setup_environment() {
    log_info "⚙️ Configuration de l'environnement..."
    
    # Variables par défaut
    export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
    export SERVER_PORT=${SERVER_PORT:-8761}
    export EUREKA_INSTANCE_HOSTNAME=${EUREKA_INSTANCE_HOSTNAME:-discovery-service}
    
    # Configuration JVM
    export JAVA_OPTS="${JAVA_OPTS:--Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport}"
    
    # Configuration Eureka
    export EUREKA_CLIENT_REGISTER_WITH_EUREKA=${EUREKA_CLIENT_REGISTER_WITH_EUREKA:-false}
    export EUREKA_CLIENT_FETCH_REGISTRY=${EUREKA_CLIENT_FETCH_REGISTRY:-false}
    export EUREKA_SERVER_ENABLE_SELF_PRESERVATION=${EUREKA_SERVER_ENABLE_SELF_PRESERVATION:-true}
    export EUREKA_SERVER_EVICTION_INTERVAL=${EUREKA_SERVER_EVICTION_INTERVAL:-60000}
    
    # Configuration du dashboard
    export EUREKA_DASHBOARD_ENABLED=${EUREKA_DASHBOARD_ENABLED:-true}
    export EUREKA_DASHBOARD_USERNAME=${EUREKA_DASHBOARD_USERNAME:-admin}
    export EUREKA_DASHBOARD_PASSWORD=${EUREKA_DASHBOARD_PASSWORD:-admin123}
    
    # Configuration du monitoring
    export MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=${MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE:-health,info,metrics,prometheus}
    export MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=${MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS:-always}
    
    log_info "📋 Configuration active:"
    log_info "   - Profil Spring: $SPRING_PROFILES_ACTIVE"
    log_info "   - Port serveur: $SERVER_PORT"
    log_info "   - Hostname Eureka: $EUREKA_INSTANCE_HOSTNAME"
    log_info "   - Dashboard activé: $EUREKA_DASHBOARD_ENABLED"
    log_info "   - Self preservation: $EUREKA_SERVER_ENABLE_SELF_PRESERVATION"
    
    log_success "✅ Environnement configuré"
}

# Fonction de préparation des répertoires
prepare_directories() {
    log_info "📁 Préparation des répertoires..."
    
    # Création des répertoires nécessaires
    mkdir -p /app/logs
    mkdir -p /app/config
    mkdir -p /app/tmp
    
    # Vérification des permissions
    if [ ! -w "/app/logs" ]; then
        log_warning "⚠️ Répertoire logs non accessible en écriture"
    fi
    
    log_success "✅ Répertoires préparés"
}

# Fonction d'attente des dépendances
wait_for_dependencies() {
    log_info "⏳ Vérification des dépendances..."
    
    # Dans le cas d'Eureka Server, pas de dépendances externes critiques
    # Mais on peut vérifier la connectivité réseau de base
    
    log_info "🌐 Test de connectivité réseau..."
    if ping -c 1 8.8.8.8 &> /dev/null; then
        log_success "✅ Connectivité réseau OK"
    else
        log_warning "⚠️ Connectivité réseau limitée"
    fi
    
    log_success "✅ Dépendances vérifiées"
}

# Fonction de démarrage de l'application
start_application() {
    log_info "🚀 Démarrage du Discovery Service..."
    
    # Construction de la commande Java
    local java_cmd="java $JAVA_OPTS"
    
    # Ajout des propriétés système
    java_cmd="$java_cmd -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE"
    java_cmd="$java_cmd -Dserver.port=$SERVER_PORT"
    java_cmd="$java_cmd -Deureka.instance.hostname=$EUREKA_INSTANCE_HOSTNAME"
    java_cmd="$java_cmd -Djava.security.egd=file:/dev/./urandom"
    java_cmd="$java_cmd -Djava.awt.headless=true"
    java_cmd="$java_cmd -Dfile.encoding=UTF-8"
    java_cmd="$java_cmd -Duser.timezone=Europe/Paris"
    
    # Ajout du JAR
    java_cmd="$java_cmd -jar /app/app.jar"
    
    log_info "📋 Commande de démarrage: $java_cmd"
    log_info "🌐 Dashboard Eureka sera disponible sur: http://localhost:$SERVER_PORT"
    log_info "📊 Endpoints Actuator disponibles sur: http://localhost:$SERVER_PORT/actuator"
    
    log_success "✅ Lancement de l'application..."
    
    # Exécution de la commande
    exec $java_cmd
}

# Fonction de nettoyage en cas d'arrêt
cleanup() {
    log_info "🧹 Nettoyage en cours..."
    
    # Nettoyage des fichiers temporaires
    if [ -d "/app/tmp" ]; then
        rm -rf /app/tmp/*
    fi
    
    log_success "✅ Nettoyage terminé"
}

# Gestion des signaux pour un arrêt propre
trap cleanup SIGTERM SIGINT

# Fonction principale
main() {
    show_banner
    
    log_info "🔍 Initialisation du Discovery Service (Eureka Server)..."
    
    # Exécution des étapes d'initialisation
    validate_environment
    setup_environment
    prepare_directories
    wait_for_dependencies
    
    log_success "🎉 Initialisation terminée avec succès!"
    
    # Démarrage de l'application
    start_application
}

# Vérification si le script est exécuté directement
if [ "${BASH_SOURCE[0]}" == "${0}" ]; then
    main "$@"
fi
