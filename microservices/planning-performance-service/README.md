# 🏐 SprintBot - Planning Performance Service

Microservice dédié à la planification des entraînements, au suivi des performances et à la gestion des absences pour les équipes de volleyball.

## 📋 Table des Matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture](#architecture)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Frontend](#frontend)
- [Base de Données](#base-de-données)
- [Tests](#tests)
- [Déploiement](#déploiement)
- [Monitoring](#monitoring)

## 🎯 Vue d'ensemble

### Fonctionnalités Principales

- **📅 Planification d'entraînements** : Création, modification et gestion du calendrier d'entraînements
- **👥 Gestion des participations** : Suivi des présences et absences en temps réel
- **📊 Évaluation des performances** : Système d'évaluation par catégories (Technique, Physique, Tactique, Mental)
- **🎯 Objectifs individuels** : Définition et suivi des objectifs personnalisés par joueur
- **📈 Statistiques avancées** : Analyses détaillées et tendances de performance
- **🚨 Alertes et notifications** : Système d'alertes pour les absences et objectifs en retard

### Technologies Utilisées

#### Backend
- **Spring Boot 3.2** - Framework principal
- **Spring Security 6** - Sécurité et authentification
- **Spring Data JPA** - Accès aux données
- **PostgreSQL** - Base de données
- **Maven** - Gestion des dépendances
- **Docker** - Containerisation

#### Frontend
- **Angular 17** - Framework frontend
- **TypeScript** - Langage de programmation
- **Angular Material** - Composants UI
- **Bootstrap 5** - Framework CSS
- **Chart.js** - Graphiques et visualisations
- **RxJS** - Programmation réactive

## 🏗️ Architecture

### Structure du Projet

```
planning-performance-service/
├── backend/                    # Application Spring Boot
│   ├── src/main/java/
│   │   └── com/sprintbot/planning/
│   │       ├── config/         # Configuration Spring
│   │       ├── controller/     # Contrôleurs REST
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── entity/        # Entités JPA
│   │       ├── repository/    # Repositories Spring Data
│   │       └── service/       # Services métier
│   ├── src/main/resources/
│   │   ├── application.yml    # Configuration application
│   │   └── db/migration/      # Scripts de migration
│   └── Dockerfile
├── frontend/                   # Application Angular
│   ├── src/app/
│   │   ├── components/        # Composants Angular
│   │   ├── services/          # Services Angular
│   │   ├── models/           # Modèles TypeScript
│   │   └── guards/           # Guards de navigation
│   └── Dockerfile
├── database/                   # Configuration base de données
│   └── init.sql              # Script d'initialisation
└── docker-compose.yml        # Orchestration Docker
```

### Modèle de Données

#### Entités Principales

1. **Entrainement** - Séances d'entraînement planifiées
2. **Participation** - Présences des joueurs aux entraînements
3. **Performance** - Évaluations de performance par catégorie
4. **Absence** - Déclarations d'absence avec workflow d'approbation
5. **ObjectifIndividuel** - Objectifs personnalisés avec suivi de progression
6. **StatistiqueEntrainement** - Métriques détaillées d'entraînement

## 🚀 Installation

### Prérequis

- **Docker** et **Docker Compose**
- **Java 17+** (pour développement local)
- **Node.js 18+** et **npm** (pour développement frontend)
- **PostgreSQL 15+** (pour développement local)

### Démarrage Rapide

1. **Cloner le repository**
```bash
git clone <repository-url>
cd microservices/planning-performance-service
```

2. **Démarrer avec Docker Compose**
```bash
docker-compose up -d
```

3. **Vérifier le démarrage**
```bash
# Backend
curl http://localhost:8082/actuator/health

# Frontend
curl http://localhost:4202
```

### Développement Local

#### Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
ng serve --port 4202
```

#### Base de Données
```bash
docker run --name planning-db \
  -e POSTGRES_DB=planning_performance_db \
  -e POSTGRES_USER=planning_user \
  -e POSTGRES_PASSWORD=planning_password \
  -p 5433:5432 \
  -d postgres:15
```

## ⚙️ Configuration

### Variables d'Environnement

#### Backend (application.yml)
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/planning_performance_db
    username: ${DB_USERNAME:planning_user}
    password: ${DB_PASSWORD:planning_password}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: ${SHOW_SQL:false}

  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: ${JWT_EXPIRATION:86400000}

logging:
  level:
    com.sprintbot: ${LOG_LEVEL:INFO}
```

#### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8082/api',
  authServiceUrl: 'http://localhost:8081/api/auth'
};
```

### Profils Spring

- **dev** - Développement local
- **docker** - Conteneur Docker
- **prod** - Production

## � API Documentation

### Endpoints Principaux

#### 🏃‍♂️ Entraînements
```http
GET    /api/entrainements              # Liste des entraînements
POST   /api/entrainements              # Créer un entraînement
GET    /api/entrainements/{id}         # Détails d'un entraînement
PUT    /api/entrainements/{id}         # Modifier un entraînement
DELETE /api/entrainements/{id}         # Supprimer un entraînement
GET    /api/entrainements/calendrier   # Vue calendrier
GET    /api/entrainements/search       # Recherche avec filtres
```

#### 👥 Participations
```http
GET    /api/participations                    # Liste des participations
POST   /api/participations                    # Enregistrer une participation
GET    /api/participations/entrainement/{id}  # Participations par entraînement
PUT    /api/participations/{id}/presence      # Marquer présent/absent
GET    /api/participations/statistiques       # Statistiques de participation
```

#### 📊 Performances
```http
GET    /api/performances              # Liste des performances
POST   /api/performances              # Créer une évaluation
GET    /api/performances/{id}         # Détails d'une performance
PUT    /api/performances/{id}         # Modifier une évaluation
GET    /api/performances/joueur/{id}  # Performances d'un joueur
GET    /api/performances/analytics    # Analyses de performance
```

#### 🚫 Absences
```http
GET    /api/absences                 # Liste des absences
POST   /api/absences                 # Déclarer une absence
GET    /api/absences/{id}            # Détails d'une absence
PUT    /api/absences/{id}            # Modifier une absence
POST   /api/absences/{id}/approuver  # Approuver une absence
POST   /api/absences/{id}/rejeter    # Rejeter une absence
GET    /api/absences/en-attente      # Absences en attente
```

#### 🎯 Objectifs
```http
GET    /api/objectifs                    # Liste des objectifs
POST   /api/objectifs                    # Créer un objectif
GET    /api/objectifs/{id}               # Détails d'un objectif
PUT    /api/objectifs/{id}               # Modifier un objectif
POST   /api/objectifs/{id}/progression   # Mettre à jour la progression
GET    /api/objectifs/joueur/{id}        # Objectifs d'un joueur
GET    /api/objectifs/en-retard          # Objectifs en retard
```

#### 📈 Statistiques
```http
GET    /api/statistiques                 # Liste des statistiques
POST   /api/statistiques                 # Enregistrer des statistiques
GET    /api/statistiques/globales        # Statistiques globales
GET    /api/statistiques/joueur/{id}     # Statistiques d'un joueur
GET    /api/statistiques/tendance        # Tendances et évolutions
GET    /api/statistiques/export          # Export des données
```

### Exemples de Requêtes

#### Créer un Entraînement
```http
POST /api/entrainements
Content-Type: application/json

{
  "titre": "Entraînement Technique",
  "description": "Travail sur les attaques et les blocs",
  "date": "2024-01-15",
  "heureDebut": "18:00",
  "heureFin": "20:00",
  "lieu": "Gymnase Principal",
  "type": "TECHNIQUE",
  "intensite": 7,
  "objectifs": ["Améliorer les attaques", "Perfectionner les blocs"],
  "equipementNecessaire": ["Ballons", "Filets"],
  "nombreMaxParticipants": 12
}
```

#### Évaluer une Performance
```http
POST /api/performances
Content-Type: application/json

{
  "entrainementId": 1,
  "joueurId": 5,
  "categorie": "TECHNIQUE",
  "note": 8.5,
  "commentaire": "Excellente progression sur les services",
  "evaluateurId": 2
}
```

#### Déclarer une Absence
```http
POST /api/absences
Content-Type: application/json

{
  "entrainementId": 1,
  "joueurId": 3,
  "type": "MALADIE",
  "motif": "Grippe",
  "justificationFournie": true,
  "commentaire": "Certificat médical fourni"
}
```

### Codes de Réponse

- **200 OK** - Requête réussie
- **201 Created** - Ressource créée avec succès
- **400 Bad Request** - Données invalides
- **401 Unauthorized** - Authentification requise
- **403 Forbidden** - Accès refusé
- **404 Not Found** - Ressource non trouvée
- **500 Internal Server Error** - Erreur serveur

## 🖥️ Frontend

### Structure des Composants

```
src/app/
├── components/
│   ├── dashboard/              # Tableau de bord principal
│   ├── entrainements/          # Gestion des entraînements
│   │   ├── entrainement-list/
│   │   ├── entrainement-form/
│   │   ├── entrainement-detail/
│   │   └── calendar/
│   ├── participations/         # Gestion des participations
│   │   ├── participation-list/
│   │   └── presence-form/
│   ├── performances/           # Évaluation des performances
│   │   ├── performance-list/
│   │   └── performance-form/
│   ├── absences/              # Gestion des absences
│   │   ├── absence-list/
│   │   ├── absence-form/
│   │   └── absence-detail/
│   ├── objectifs/             # Objectifs individuels
│   │   ├── objectif-list/
│   │   ├── objectif-form/
│   │   └── objectif-detail/
│   └── statistiques/          # Analyses et statistiques
│       ├── statistique-dashboard/
│       ├── statistique-joueur/
│       └── statistique-equipe/
├── services/                  # Services Angular
│   ├── entrainement.service.ts
│   ├── participation.service.ts
│   ├── performance.service.ts
│   ├── absence.service.ts
│   ├── objectif.service.ts
│   └── statistique.service.ts
├── models/                    # Modèles TypeScript
│   ├── entrainement.model.ts
│   ├── participation.model.ts
│   ├── performance.model.ts
│   ├── absence.model.ts
│   ├── objectif.model.ts
│   └── statistique.model.ts
└── guards/                    # Guards de navigation
    ├── auth.guard.ts
    └── role.guard.ts
```

### Fonctionnalités Frontend

#### 📊 Dashboard
- Vue d'ensemble des statistiques
- Graphiques de performance
- Alertes et notifications
- Actions rapides

#### 📅 Calendrier d'Entraînements
- Vue mensuelle, hebdomadaire et liste
- Création/modification d'entraînements
- Filtrage par type et intensité
- Export des plannings

#### 👥 Gestion des Présences
- Prise de présence en temps réel
- Historique des participations
- Statistiques d'assiduité
- Alertes d'absence

#### 📈 Évaluation des Performances
- Système d'évaluation par catégories
- Graphiques de progression
- Comparaisons entre joueurs
- Recommandations personnalisées

#### 🎯 Suivi des Objectifs
- Création d'objectifs SMART
- Suivi de progression
- Alertes d'échéance
- Recommandations d'amélioration

### Technologies Frontend

- **Angular 17** avec Standalone Components
- **Angular Material** pour les composants UI
- **Bootstrap 5** pour le layout responsive
- **Chart.js** pour les graphiques
- **RxJS** pour la gestion d'état réactive
- **TypeScript** pour le typage fort

## 🗄️ Base de Données

### Schéma de Base de Données

#### Table: entrainements
```sql
CREATE TABLE entrainements (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    lieu VARCHAR(100),
    type VARCHAR(20) NOT NULL,
    intensite INTEGER CHECK (intensite >= 1 AND intensite <= 10),
    statut VARCHAR(20) DEFAULT 'PLANIFIE',
    objectifs TEXT[],
    equipement_necessaire TEXT[],
    nombre_max_participants INTEGER,
    coach_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Table: participations
```sql
CREATE TABLE participations (
    id BIGSERIAL PRIMARY KEY,
    entrainement_id BIGINT NOT NULL REFERENCES entrainements(id),
    joueur_id BIGINT NOT NULL,
    heure_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    heure_presence TIMESTAMP,
    statut_presence VARCHAR(20) DEFAULT 'INSCRIT',
    commentaire TEXT,
    UNIQUE(entrainement_id, joueur_id)
);
```

#### Table: performances
```sql
CREATE TABLE performances (
    id BIGSERIAL PRIMARY KEY,
    entrainement_id BIGINT NOT NULL REFERENCES entrainements(id),
    joueur_id BIGINT NOT NULL,
    categorie VARCHAR(20) NOT NULL,
    note DECIMAL(3,1) CHECK (note >= 0 AND note <= 10),
    commentaire TEXT,
    evaluateur_id BIGINT,
    date_evaluation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Table: absences
```sql
CREATE TABLE absences (
    id BIGSERIAL PRIMARY KEY,
    entrainement_id BIGINT NOT NULL REFERENCES entrainements(id),
    joueur_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    motif TEXT NOT NULL,
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
    justification_fournie BOOLEAN DEFAULT FALSE,
    commentaire TEXT,
    commentaire_approbation TEXT,
    date_declaration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_traitement TIMESTAMP,
    traite_par BIGINT
);
```

### Relations et Contraintes

- **Clés étrangères** : Références vers les utilisateurs (auth-user-service)
- **Contraintes d'unicité** : Participation unique par joueur/entraînement
- **Contraintes de validation** : Notes entre 0-10, intensité entre 1-10
- **Index** : Sur les dates, joueur_id, entrainement_id pour les performances

### Migration et Versioning

- Scripts de migration dans `database/migrations/`
- Versioning automatique avec Flyway
- Sauvegarde automatique avant migration
- Rollback possible en cas d'erreur

## 🧪 Tests

### Tests Backend

#### Tests Unitaires
```bash
cd backend
./mvnw test
```

#### Tests d'Intégration
```bash
./mvnw test -Dtest=**/*IntegrationTest
```

#### Coverage
```bash
./mvnw jacoco:report
# Rapport disponible dans target/site/jacoco/index.html
```

### Tests Frontend

#### Tests Unitaires
```bash
cd frontend
npm test
```

#### Tests E2E
```bash
npm run e2e
```

#### Coverage
```bash
npm run test:coverage
```

### Tests de Performance

#### Load Testing avec JMeter
```bash
# Démarrer l'application
docker-compose up -d

# Exécuter les tests de charge
jmeter -n -t tests/load-test.jmx -l results.jtl
```

## 🚀 Déploiement

### Environnements

#### Développement
```bash
docker-compose -f docker-compose.dev.yml up -d
```

#### Staging
```bash
docker-compose -f docker-compose.staging.yml up -d
```

#### Production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### CI/CD Pipeline

#### GitHub Actions
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: ./mvnw test

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker images
        run: docker-compose build
      - name: Push to registry
        run: docker-compose push

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to production
        run: |
          ssh ${{ secrets.PROD_SERVER }} \
          "cd /app && docker-compose pull && docker-compose up -d"
```

### Configuration Production

#### Variables d'Environnement
```bash
# Base de données
DB_HOST=prod-db-server
DB_PORT=5432
DB_NAME=planning_performance_db
DB_USERNAME=planning_user
DB_PASSWORD=secure_password

# Sécurité
JWT_SECRET=super_secure_jwt_secret_key
JWT_EXPIRATION=86400000

# Monitoring
LOG_LEVEL=WARN
METRICS_ENABLED=true
```

#### Optimisations
- **Connection pooling** : HikariCP configuré pour la production
- **Cache Redis** : Cache des requêtes fréquentes
- **CDN** : Assets statiques servis via CDN
- **Compression** : Gzip activé pour les réponses HTTP

## 📊 Monitoring

### Métriques Applicatives

#### Spring Boot Actuator
```http
GET /actuator/health      # État de santé
GET /actuator/metrics     # Métriques applicatives
GET /actuator/info        # Informations sur l'application
GET /actuator/prometheus  # Métriques Prometheus
```

#### Métriques Métier
- Nombre d'entraînements créés/jour
- Taux de participation moyen
- Temps de réponse des APIs
- Nombre d'évaluations de performance
- Taux d'absences par équipe

### Logging

#### Configuration Logback
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/planning-performance-service.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/planning-performance-service.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.sprintbot.planning" level="INFO"/>
    <root level="WARN">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### Alertes

#### Prometheus + Grafana
- **Dashboards** : Métriques en temps réel
- **Alertes** : Notifications sur Slack/Email
- **SLA Monitoring** : Disponibilité et performance

#### Alertes Métier
- Absence de données de performance > 24h
- Taux d'absence > 30% sur un entraînement
- Objectifs en retard > 7 jours
- Erreurs d'API > 5% sur 5 minutes

## 🔧 Maintenance

### Sauvegarde

#### Base de Données
```bash
# Sauvegarde quotidienne
pg_dump -h localhost -U planning_user planning_performance_db > backup_$(date +%Y%m%d).sql

# Restauration
psql -h localhost -U planning_user planning_performance_db < backup_20240115.sql
```

#### Fichiers de Configuration
- Sauvegarde des configurations dans Git
- Chiffrement des secrets avec Ansible Vault
- Versioning des scripts de déploiement

### Mise à Jour

#### Rolling Update
```bash
# Mise à jour sans interruption
docker-compose pull
docker-compose up -d --no-deps backend
docker-compose up -d --no-deps frontend
```

#### Migration de Base de Données
```bash
# Vérification avant migration
./mvnw flyway:validate

# Exécution des migrations
./mvnw flyway:migrate

# Rollback si nécessaire
./mvnw flyway:undo
```

## 🤝 Contribution

### Standards de Code

#### Backend (Java)
- **Checkstyle** : Respect des conventions Google Java Style
- **SpotBugs** : Détection des bugs potentiels
- **JaCoCo** : Coverage minimum 80%

#### Frontend (TypeScript)
- **ESLint** : Linting du code TypeScript
- **Prettier** : Formatage automatique
- **Husky** : Pre-commit hooks

### Workflow Git

1. **Feature Branch** : `feature/nom-de-la-fonctionnalite`
2. **Pull Request** : Review obligatoire
3. **Tests** : Tous les tests doivent passer
4. **Merge** : Squash and merge vers develop
5. **Release** : Merge develop vers main

### Documentation

- **Code** : Commentaires JSDoc/Javadoc
- **API** : Documentation OpenAPI/Swagger
- **Architecture** : Diagrammes C4 Model
- **Changelog** : Suivi des versions

## 📞 Support

### Contacts

- **Équipe Développement** : dev@sprintbot.com
- **Support Technique** : support@sprintbot.com
- **Documentation** : docs@sprintbot.com

### Ressources

- **Wiki** : Documentation détaillée
- **Issues** : Suivi des bugs et améliorations
- **Discussions** : Forum de la communauté
- **Releases** : Notes de version

---

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

**SprintBot Planning Performance Service** - Optimisez la performance de votre équipe de volleyball ! 🏐
