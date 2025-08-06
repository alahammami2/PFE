# 🌐 Gateway Service - SprintBot

## Vue d'ensemble

Le **Gateway Service** est le point d'entrée unique pour l'écosystème microservices SprintBot. Il fournit un routage intelligent, une sécurité centralisée, et une gestion unifiée des requêtes pour tous les microservices de la plateforme.

## 🎯 Fonctionnalités principales

### 🔀 Routage intelligent
- **Routage basé sur les chemins** - Redirection automatique vers les microservices appropriés
- **Load balancing** - Distribution équilibrée des requêtes entre les instances
- **Service discovery** - Intégration avec Eureka pour la découverte automatique
- **Circuit breaker** - Protection contre les défaillances en cascade
- **Retry logic** - Tentatives automatiques en cas d'échec temporaire

### 🔐 Sécurité centralisée
- **Authentification JWT** - Validation centralisée des tokens d'accès
- **Autorisation RBAC** - Contrôle d'accès basé sur les rôles
- **CORS global** - Configuration centralisée des politiques CORS
- **Rate limiting** - Protection contre les attaques DDoS
- **Request/Response filtering** - Filtrage et transformation des requêtes

### 📊 Monitoring et observabilité
- **Métriques détaillées** - Latence, throughput, taux d'erreur par route
- **Tracing distribué** - Suivi des requêtes à travers tous les services
- **Health checks** - Surveillance de la santé du gateway et des services
- **Logging centralisé** - Logs structurés pour toutes les requêtes

## 🏗️ Architecture

### Composants techniques
- **Spring Cloud Gateway** - Routeur réactif haute performance
- **Spring Security** - Sécurité et authentification
- **Eureka Client** - Découverte de services
- **Resilience4j** - Circuit breaker et retry
- **Micrometer** - Métriques et monitoring

### Intégration avec l'écosystème
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────┐
│   Frontend      │───▶│  Gateway Service │───▶│  Discovery Service  │
│   Angular       │    │  (Port 8080)     │    │  (Port 8761)        │
└─────────────────┘    └──────────────────┘    └─────────────────────┘
                                │
                                ▼
                    ┌─────────────────────────┐
                    │     Microservices       │
                    │                         │
                    │ • auth-user-service     │
                    │ • planning-performance  │
                    │ • medical-admin         │
                    │ • communication         │
                    │ • finance               │
                    └─────────────────────────┘
```

## 🛣️ Configuration des routes

### Routes principales
| Chemin | Service de destination | Description |
|--------|----------------------|-------------|
| `/api/auth/**` | auth-user-service | Authentification et gestion des utilisateurs |
| `/api/planning/**` | planning-performance-service | Planification et performances |
| `/api/medical/**` | medical-admin-service | Gestion médicale et administrative |
| `/api/communication/**` | communication-service | Messagerie et notifications |
| `/api/finance/**` | finance-service | Gestion financière |

### Filtres appliqués
- **AuthenticationFilter** - Validation JWT sur toutes les routes protégées
- **LoggingFilter** - Logging des requêtes et réponses
- **CorsFilter** - Gestion des politiques CORS
- **RateLimitFilter** - Limitation du taux de requêtes
- **CircuitBreakerFilter** - Protection contre les défaillances

## 🔧 Configuration

### Variables d'environnement principales
```bash
# Configuration de base
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080
GATEWAY_INSTANCE_HOSTNAME=gateway-service

# Découverte de services
EUREKA_CLIENT_SERVICE_URL=http://discovery-service:8761/eureka/

# Sécurité JWT
JWT_SECRET_KEY=your-secret-key
JWT_EXPIRATION_TIME=86400000

# Rate limiting
RATE_LIMIT_REQUESTS_PER_SECOND=100
RATE_LIMIT_BURST_CAPACITY=200
```

## 🚀 Démarrage rapide

### Avec Docker Compose
```bash
cd microservices/gateway-service
docker-compose up -d gateway-service
```

### Vérification du déploiement
```bash
# Health check
curl http://localhost:8080/actuator/health

# Routes configurées
curl http://localhost:8080/actuator/gateway/routes

# Métriques
curl http://localhost:8080/actuator/metrics
```

## 📊 Endpoints de monitoring

### Actuator endpoints
- **Health** : `GET /actuator/health` - État de santé du gateway
- **Info** : `GET /actuator/info` - Informations du service
- **Metrics** : `GET /actuator/metrics` - Métriques détaillées
- **Gateway Routes** : `GET /actuator/gateway/routes` - Routes configurées
- **Prometheus** : `GET /actuator/prometheus` - Métriques Prometheus

### Métriques spécifiques
- `gateway.requests.total` - Nombre total de requêtes
- `gateway.requests.duration` - Durée des requêtes
- `gateway.circuit.breaker.state` - État des circuit breakers
- `gateway.rate.limit.hits` - Hits du rate limiting

## 🔐 Sécurité

### Authentification JWT
Le gateway valide automatiquement les tokens JWT pour toutes les routes protégées :
```yaml
# Routes publiques (sans authentification)
- /api/auth/login
- /api/auth/register
- /actuator/health

# Routes protégées (authentification requise)
- /api/planning/**
- /api/medical/**
- /api/communication/**
- /api/finance/**
```

### Autorisation par rôles
```yaml
# Exemples de règles d'autorisation
/api/medical/admin/** : ROLE_ADMIN, ROLE_MEDICAL_ADMIN
/api/finance/admin/** : ROLE_ADMIN, ROLE_FINANCE_ADMIN
/api/planning/** : ROLE_USER, ROLE_COACH, ROLE_ADMIN
```

## 🛠️ Développement

### Structure du projet
```
gateway-service/
├── README.md
├── DEPLOYMENT.md
├── COMPLETION-SUMMARY.md
├── .env.example
├── .gitignore
├── docker-compose.yml
├── validate-gateway.sh
├── test-gateway-routes.sh
└── backend/
    ├── pom.xml
    ├── Dockerfile
    ├── docker-entrypoint.sh
    └── src/main/
        ├── java/com/sprintbot/gateway/
        │   ├── GatewayServiceApplication.java
        │   ├── config/
        │   │   ├── GatewayConfig.java
        │   │   ├── SecurityConfig.java
        │   │   └── CorsConfig.java
        │   ├── filter/
        │   │   ├── AuthenticationFilter.java
        │   │   ├── LoggingFilter.java
        │   │   └── RateLimitFilter.java
        │   └── service/
        │       └── JwtService.java
        └── resources/
            ├── application.yml
            └── logback-spring.xml
```

### Build et tests
```bash
# Build Maven
cd backend && mvn clean package

# Tests unitaires
mvn test

# Tests d'intégration
mvn verify

# Build Docker
docker-compose build gateway-service
```

## 📚 Documentation technique

### Technologies utilisées
- **Spring Boot** 3.2.0
- **Spring Cloud Gateway** 4.1.0
- **Spring Security** 6.2.0
- **Eureka Client** 4.1.0
- **Resilience4j** 2.2.0
- **Micrometer** 1.12.0

### Patterns implémentés
- **API Gateway Pattern** - Point d'entrée unique
- **Circuit Breaker Pattern** - Résilience aux pannes
- **Retry Pattern** - Gestion des échecs temporaires
- **Rate Limiting Pattern** - Protection contre la surcharge
- **Service Discovery Pattern** - Découverte automatique

## 🔄 Intégration continue

### Pipeline CI/CD
```yaml
# Exemple de pipeline
stages:
  - build
  - test
  - security-scan
  - docker-build
  - deploy

# Tests automatiques
- Unit tests
- Integration tests
- Security tests
- Performance tests
```

## 📞 Support

### Équipe responsable
- **Infrastructure Team** : infrastructure@sprintbot.com
- **Security Team** : security@sprintbot.com

### Ressources
- [Documentation Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Guide de sécurité](./docs/security-guide.md)
- [Monitoring et alertes](./docs/monitoring-guide.md)

---

**🎯 Le Gateway Service est le cœur de l'architecture microservices SprintBot, assurant un routage intelligent et une sécurité centralisée pour tous les services de la plateforme.**
