#!/bin/sh

# Script de démarrage pour le service finance
# Gère l'initialisation et le démarrage de l'application Spring Boot

set -e

# Couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction de logging
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [FINANCE-SERVICE]${NC} $1"
}

log_info() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR]${NC} $1"
}

# Affichage des informations de démarrage
log "=== DÉMARRAGE DU SERVICE FINANCE ==="
log "Version Java: $(java -version 2>&1 | head -n 1)"
log "Profil Spring actif: ${SPRING_PROFILES_ACTIVE:-default}"
log "Port du serveur: ${SERVER_PORT:-8085}"
log "Options JVM: ${JAVA_OPTS}"

# Vérification des variables d'environnement critiques
check_env_vars() {
    log_info "Vérification des variables d'environnement..."
    
    # Variables de base de données
    if [ -z "$DB_HOST" ]; then
        log_warn "DB_HOST non défini, utilisation de la valeur par défaut"
    fi
    
    if [ -z "$DB_NAME" ]; then
        log_warn "DB_NAME non défini, utilisation de la valeur par défaut"
    fi
    
    if [ -z "$DB_USERNAME" ]; then
        log_warn "DB_USERNAME non défini, utilisation de la valeur par défaut"
    fi
    
    if [ -z "$DB_PASSWORD" ]; then
        log_warn "DB_PASSWORD non défini, utilisation de la valeur par défaut"
    fi
    
    # Variables JWT
    if [ -z "$JWT_SECRET" ]; then
        log_warn "JWT_SECRET non défini, utilisation de la valeur par défaut"
    fi
    
    # Variables Redis
    if [ -z "$REDIS_HOST" ]; then
        log_warn "REDIS_HOST non défini, utilisation de la valeur par défaut"
    fi
    
    log_info "Vérification des variables d'environnement terminée"
}

# Attente de la disponibilité de la base de données
wait_for_database() {
    local host=${DB_HOST:-localhost}
    local port=${DB_PORT:-5432}
    local max_attempts=30
    local attempt=1
    
    log_info "Attente de la disponibilité de la base de données ${host}:${port}..."
    
    while [ $attempt -le $max_attempts ]; do
        if nc -z "$host" "$port" 2>/dev/null; then
            log_info "Base de données disponible après $attempt tentative(s)"
            return 0
        fi
        
        log_warn "Tentative $attempt/$max_attempts - Base de données non disponible, attente 2s..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    log_error "Impossible de se connecter à la base de données après $max_attempts tentatives"
    return 1
}

# Attente de la disponibilité de Redis
wait_for_redis() {
    local host=${REDIS_HOST:-localhost}
    local port=${REDIS_PORT:-6379}
    local max_attempts=15
    local attempt=1
    
    log_info "Attente de la disponibilité de Redis ${host}:${port}..."
    
    while [ $attempt -le $max_attempts ]; do
        if nc -z "$host" "$port" 2>/dev/null; then
            log_info "Redis disponible après $attempt tentative(s)"
            return 0
        fi
        
        log_warn "Tentative $attempt/$max_attempts - Redis non disponible, attente 2s..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    log_warn "Redis non disponible après $max_attempts tentatives, continuons sans cache"
    return 0
}

# Préparation des répertoires
prepare_directories() {
    log_info "Préparation des répertoires..."
    
    # Création des répertoires s'ils n'existent pas
    mkdir -p /app/logs
    mkdir -p /app/uploads
    mkdir -p /app/reports
    mkdir -p /app/temp
    
    # Vérification des permissions
    if [ ! -w /app/logs ]; then
        log_error "Impossible d'écrire dans le répertoire /app/logs"
        exit 1
    fi
    
    log_info "Répertoires préparés avec succès"
}

# Nettoyage des fichiers temporaires
cleanup_temp_files() {
    log_info "Nettoyage des fichiers temporaires..."
    
    # Suppression des fichiers temporaires anciens (plus de 24h)
    find /app/temp -type f -mtime +1 -delete 2>/dev/null || true
    find /app/reports -name "*.tmp" -mtime +1 -delete 2>/dev/null || true
    
    log_info "Nettoyage terminé"
}

# Configuration des options JVM
configure_jvm() {
    log_info "Configuration des options JVM..."
    
    # Options JVM par défaut si non définies
    if [ -z "$JAVA_OPTS" ]; then
        JAVA_OPTS="-Xms512m -Xmx1024m"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
        JAVA_OPTS="$JAVA_OPTS -XX:G1HeapRegionSize=16m"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseStringDeduplication"
        JAVA_OPTS="$JAVA_OPTS -XX:+OptimizeStringConcat"
        JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
        JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
        JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Europe/Paris"
    fi
    
    # Options de debugging si activées
    if [ "$DEBUG_MODE" = "true" ]; then
        JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
        log_info "Mode debug activé sur le port 5005"
    fi
    
    # Options de profiling si activées
    if [ "$PROFILE_MODE" = "true" ]; then
        JAVA_OPTS="$JAVA_OPTS -XX:+FlightRecorder"
        JAVA_OPTS="$JAVA_OPTS -XX:StartFlightRecording=duration=60s,filename=/app/logs/flight-recording.jfr"
        log_info "Profiling JFR activé"
    fi
    
    export JAVA_OPTS
    log_info "Options JVM configurées: $JAVA_OPTS"
}

# Gestion des signaux pour un arrêt propre
setup_signal_handlers() {
    log_info "Configuration des gestionnaires de signaux..."
    
    # Fonction d'arrêt propre
    shutdown() {
        log_info "Signal d'arrêt reçu, arrêt en cours..."
        
        # Envoi du signal TERM au processus Java s'il existe
        if [ -n "$JAVA_PID" ]; then
            kill -TERM "$JAVA_PID" 2>/dev/null || true
            
            # Attente de l'arrêt propre (max 30s)
            local count=0
            while [ $count -lt 30 ] && kill -0 "$JAVA_PID" 2>/dev/null; do
                sleep 1
                count=$((count + 1))
            done
            
            # Force l'arrêt si nécessaire
            if kill -0 "$JAVA_PID" 2>/dev/null; then
                log_warn "Arrêt forcé du processus Java"
                kill -KILL "$JAVA_PID" 2>/dev/null || true
            fi
        fi
        
        log_info "Service finance arrêté"
        exit 0
    }
    
    # Enregistrement des gestionnaires de signaux
    trap shutdown TERM INT QUIT
}

# Fonction principale de démarrage
start_application() {
    log_info "Démarrage de l'application Spring Boot..."
    
    # Construction de la commande Java
    JAVA_CMD="java $JAVA_OPTS -jar app.jar"
    
    log_info "Commande d'exécution: $JAVA_CMD"
    log "=== DÉMARRAGE DE L'APPLICATION ==="
    
    # Démarrage de l'application en arrière-plan
    exec $JAVA_CMD &
    JAVA_PID=$!
    
    # Attente du processus Java
    wait $JAVA_PID
}

# Fonction principale
main() {
    # Configuration des gestionnaires de signaux
    setup_signal_handlers
    
    # Vérifications préliminaires
    check_env_vars
    prepare_directories
    cleanup_temp_files
    configure_jvm
    
    # Attente des services externes
    if [ "$WAIT_FOR_DB" != "false" ]; then
        wait_for_database || exit 1
    fi
    
    if [ "$WAIT_FOR_REDIS" != "false" ]; then
        wait_for_redis
    fi
    
    # Démarrage de l'application
    start_application
}

# Exécution du script principal
main "$@"
