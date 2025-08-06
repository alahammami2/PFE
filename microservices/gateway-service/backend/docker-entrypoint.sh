#!/bin/bash

# Script d'entrée pour Gateway Service - SprintBot
# Point d'entrée unique pour l'écosystème microservices

set -e

# Configuration des couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Fonction de logging avec couleurs
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [GATEWAY-SERVICE] $1${NC}"
}

log_info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [INFO] $1${NC}"
}

log_warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [WARN] $1${NC}"
}

log_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR] $1${NC}"
}

log_success() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [SUCCESS] $1${NC}"
}

# Fonction de nettoyage pour arrêt propre
cleanup() {
    log_warn "🛑 Signal d'arrêt reçu, nettoyage en cours..."
    
    if [ ! -z "$GATEWAY_PID" ]; then
        log_info "Arrêt du Gateway Service (PID: $GATEWAY_PID)"
        kill -TERM "$GATEWAY_PID" 2>/dev/null || true
        wait "$GATEWAY_PID" 2>/dev/null || true
    fi
    
    log_success "✅ Nettoyage terminé"
    exit 0
}

# Configuration des signaux pour arrêt propre
trap cleanup SIGTERM SIGINT SIGQUIT

# Affichage des informations de démarrage
echo ""
echo -e "${PURPLE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${PURPLE}║                    🌐 GATEWAY SERVICE                        ║${NC}"
echo -e "${PURPLE}║                     SprintBot Platform                       ║${NC}"
echo -e "${PURPLE}║                                                              ║${NC}"
echo -e "${PURPLE}║  Point d'entrée unique pour l'écosystème microservices      ║${NC}"
echo -e "${PURPLE}║  Version: 1.0.0                                             ║${NC}"
echo -e "${PURPLE}║  Team: Infrastructure Team                                  ║${NC}"
echo -e "${PURPLE}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Validation des variables d'environnement critiques
log "🔍 Validation des variables d'environnement..."

# Variables obligatoires
REQUIRED_VARS=(
    "SPRING_PROFILES_ACTIVE"
    "SERVER_PORT"
    "EUREKA_CLIENT_SERVICE_URL"
    "JWT_SECRET_KEY"
)

for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        log_error "❌ Variable d'environnement manquante: $var"
        exit 1
    fi
    log_info "✅ $var = ${!var}"
done

# Variables optionnelles avec valeurs par défaut
REDIS_HOST=${REDIS_HOST:-redis}
REDIS_PORT=${REDIS_PORT:-6379}
REDIS_DATABASE=${REDIS_DATABASE:-0}
JWT_EXPIRATION_TIME=${JWT_EXPIRATION_TIME:-86400000}
RATE_LIMIT_REQUESTS_PER_SECOND=${RATE_LIMIT_REQUESTS_PER_SECOND:-100}
LOGGING_LEVEL_GATEWAY=${LOGGING_LEVEL_GATEWAY:-INFO}

log_info "🔧 Configuration Redis: ${REDIS_HOST}:${REDIS_PORT}/${REDIS_DATABASE}"
log_info "🔐 JWT Expiration: ${JWT_EXPIRATION_TIME}ms"
log_info "⚡ Rate Limit: ${RATE_LIMIT_REQUESTS_PER_SECOND} req/s"
log_info "📝 Log Level: ${LOGGING_LEVEL_GATEWAY}"

# Attente de la disponibilité d'Eureka
log "🔍 Vérification de la disponibilité d'Eureka..."
EUREKA_HOST=$(echo $EUREKA_CLIENT_SERVICE_URL | sed 's|http://||' | sed 's|/eureka/||' | cut -d':' -f1)
EUREKA_PORT=$(echo $EUREKA_CLIENT_SERVICE_URL | sed 's|http://||' | sed 's|/eureka/||' | cut -d':' -f2)

if [ -z "$EUREKA_PORT" ]; then
    EUREKA_PORT=8761
fi

log_info "Attente d'Eureka sur ${EUREKA_HOST}:${EUREKA_PORT}..."

EUREKA_WAIT_TIME=0
EUREKA_MAX_WAIT=120

while ! nc -z "$EUREKA_HOST" "$EUREKA_PORT"; do
    if [ $EUREKA_WAIT_TIME -ge $EUREKA_MAX_WAIT ]; then
        log_error "❌ Timeout: Eureka non disponible après ${EUREKA_MAX_WAIT}s"
        exit 1
    fi
    
    log_warn "⏳ Eureka non disponible, attente... (${EUREKA_WAIT_TIME}s/${EUREKA_MAX_WAIT}s)"
    sleep 5
    EUREKA_WAIT_TIME=$((EUREKA_WAIT_TIME + 5))
done

log_success "✅ Eureka disponible sur ${EUREKA_HOST}:${EUREKA_PORT}"

# Attente de la disponibilité de Redis
log "🔍 Vérification de la disponibilité de Redis..."
log_info "Attente de Redis sur ${REDIS_HOST}:${REDIS_PORT}..."

REDIS_WAIT_TIME=0
REDIS_MAX_WAIT=60

while ! nc -z "$REDIS_HOST" "$REDIS_PORT"; do
    if [ $REDIS_WAIT_TIME -ge $REDIS_MAX_WAIT ]; then
        log_warn "⚠️ Redis non disponible après ${REDIS_MAX_WAIT}s (rate limiting désactivé)"
        break
    fi
    
    log_warn "⏳ Redis non disponible, attente... (${REDIS_WAIT_TIME}s/${REDIS_MAX_WAIT}s)"
    sleep 3
    REDIS_WAIT_TIME=$((REDIS_WAIT_TIME + 3))
done

if nc -z "$REDIS_HOST" "$REDIS_PORT"; then
    log_success "✅ Redis disponible sur ${REDIS_HOST}:${REDIS_PORT}"
else
    log_warn "⚠️ Redis non disponible - Le rate limiting sera désactivé"
fi

# Configuration JVM optimisée
log "⚙️ Configuration JVM..."

# Calcul de la mémoire disponible
TOTAL_MEMORY=$(cat /proc/meminfo | grep MemTotal | awk '{print $2}')
TOTAL_MEMORY_MB=$((TOTAL_MEMORY / 1024))

# Configuration automatique de la heap
if [ $TOTAL_MEMORY_MB -lt 1024 ]; then
    HEAP_SIZE="512m"
elif [ $TOTAL_MEMORY_MB -lt 2048 ]; then
    HEAP_SIZE="1024m"
else
    HEAP_SIZE="2048m"
fi

# Options JVM optimisées
JVM_OPTS="${JAVA_OPTS} -Xms512m -Xmx${HEAP_SIZE}"
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:G1HeapRegionSize=16m"
JVM_OPTS="$JVM_OPTS -XX:+UseStringDeduplication"
JVM_OPTS="$JVM_OPTS -XX:+OptimizeStringConcat"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedOops"
JVM_OPTS="$JVM_OPTS -Djava.awt.headless=true"
JVM_OPTS="$JVM_OPTS -Djava.security.egd=file:/dev/./urandom"
JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8"
JVM_OPTS="$JVM_OPTS -Duser.timezone=Europe/Paris"

log_info "💾 Mémoire totale: ${TOTAL_MEMORY_MB}MB"
log_info "🔧 Heap configurée: ${HEAP_SIZE}"
log_info "⚡ GC: G1 avec optimisations"

# Préparation des répertoires
log "📁 Préparation des répertoires..."
mkdir -p /app/logs /app/config /app/tmp
chmod 755 /app/logs /app/config /app/tmp

# Configuration des propriétés système
SYSTEM_PROPS="-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"
SYSTEM_PROPS="$SYSTEM_PROPS -Dserver.port=${SERVER_PORT}"
SYSTEM_PROPS="$SYSTEM_PROPS -Deureka.client.service-url.defaultZone=${EUREKA_CLIENT_SERVICE_URL}"
SYSTEM_PROPS="$SYSTEM_PROPS -Dspring.data.redis.host=${REDIS_HOST}"
SYSTEM_PROPS="$SYSTEM_PROPS -Dspring.data.redis.port=${REDIS_PORT}"
SYSTEM_PROPS="$SYSTEM_PROPS -Dspring.data.redis.database=${REDIS_DATABASE}"
SYSTEM_PROPS="$SYSTEM_PROPS -Djwt.secret=${JWT_SECRET_KEY}"
SYSTEM_PROPS="$SYSTEM_PROPS -Djwt.expiration=${JWT_EXPIRATION_TIME}"
SYSTEM_PROPS="$SYSTEM_PROPS -Drate-limit.requests-per-second=${RATE_LIMIT_REQUESTS_PER_SECOND}"
SYSTEM_PROPS="$SYSTEM_PROPS -Dlogging.level.com.sprintbot.gateway=${LOGGING_LEVEL_GATEWAY}"

# Affichage de la configuration finale
log "🚀 Configuration finale du Gateway Service:"
log_info "📍 Profil Spring: ${SPRING_PROFILES_ACTIVE}"
log_info "🌐 Port: ${SERVER_PORT}"
log_info "🔍 Eureka: ${EUREKA_CLIENT_SERVICE_URL}"
log_info "💾 Redis: ${REDIS_HOST}:${REDIS_PORT}/${REDIS_DATABASE}"
log_info "🔐 JWT configuré avec expiration: ${JWT_EXPIRATION_TIME}ms"
log_info "⚡ Rate limiting: ${RATE_LIMIT_REQUESTS_PER_SECOND} req/s"
log_info "📝 Niveau de log: ${LOGGING_LEVEL_GATEWAY}"

# Démarrage de l'application
log "🚀 Démarrage du Gateway Service..."
echo ""

# Construction de la commande complète
JAVA_CMD="java $JVM_OPTS $SYSTEM_PROPS -cp /app:/app/lib/* com.sprintbot.gateway.GatewayServiceApplication"

log_info "Commande d'exécution: $JAVA_CMD"
echo ""

# Exécution de l'application en arrière-plan
exec $JAVA_CMD &
GATEWAY_PID=$!

log_success "✅ Gateway Service démarré avec le PID: $GATEWAY_PID"
log_info "🌐 API Gateway disponible sur: http://localhost:${SERVER_PORT}"
log_info "📊 Actuator endpoints: http://localhost:${SERVER_PORT}/actuator"
log_info "🔍 Health check: http://localhost:${SERVER_PORT}/actuator/health"
log_info "📈 Métriques: http://localhost:${SERVER_PORT}/actuator/metrics"
log_info "🛣️ Routes: http://localhost:${SERVER_PORT}/actuator/gateway/routes"

# Attente de l'arrêt du processus
wait $GATEWAY_PID
