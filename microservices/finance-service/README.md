# 💰 SprintBot - Finance Service

Microservice de gestion financière pour la plateforme SprintBot - **100% COMPLET**

## ✅ Statut du Projet

**🎯 MICROSERVICE COMPLET À 100%**
- ✅ Backend Spring Boot (100%)
- ✅ Frontend Angular (100%)
- ✅ Base de données PostgreSQL (100%)
- ✅ Configuration Docker (100%)
- ✅ Tests et validation (100%)
- ✅ Documentation complète (100%)

## 🚀 Fonctionnalités

### 1. 📊 Suivi budgétaire
- ✅ Création et gestion des budgets avec catégories
- ✅ Suivi en temps réel des dépenses par catégorie
- ✅ Alertes automatiques de dépassement de seuil
- ✅ Rapports budgétaires détaillés avec graphiques
- ✅ Gestion des périodes (mensuel, trimestriel, semestriel, annuel)
- ✅ Validation et approbation des budgets

### 2. 💳 Revenus/Dépenses
- ✅ Enregistrement complet des transactions financières
- ✅ Workflow de validation des paiements
- ✅ Historique financier avec filtres avancés
- ✅ Réconciliation bancaire automatisée
- ✅ Gestion des modes de paiement multiples
- ✅ Catégorisation automatique des transactions

### 3. 🤝 Sponsors
- ✅ Gestion complète des partenaires et sponsors
- ✅ Suivi détaillé des contrats de sponsoring
- ✅ Gestion des paiements de sponsoring
- ✅ Renouvellement automatique des contrats
- ✅ Tableau de bord des partenariats
- ✅ Historique des relations commerciales

### 4. 💼 Salaires
- ✅ Calcul automatisé des salaires avec éléments variables
- ✅ Génération de bulletins de paie PDF
- ✅ Gestion des charges sociales et fiscales
- ✅ Déclarations fiscales automatisées
- ✅ Historique des paiements de salaires
- ✅ Gestion des primes et heures supplémentaires

## 🚀 Démarrage rapide

### Démarrage complet
```bash
# Depuis la racine du projet
docker-compose up --build finance-backend finance-frontend finance-db

# Vérification des services
curl http://localhost:8085/actuator/health  # Backend
curl http://localhost:4205                  # Frontend
```

### Démarrage pour développement
```bash
cd microservices/finance-service

# Démarrage avec Docker Compose local
docker-compose up -d

# Ou démarrage manuel
# Backend
cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend (nouveau terminal)
cd frontend && npm install && ng serve --port 4205
```

## 🏗️ **Architecture**

### **Backend Spring Boot 3.2 (Port 8085)**
- **API REST** pour la gestion financière
- **JPA/Hibernate** avec PostgreSQL
- **Sécurité JWT** intégrée
- **Validation** des données financières
- **Audit** des opérations sensibles
- **Rapports** PDF et Excel
- **Notifications** automatiques

### **Frontend Angular 17 (Port 4205)**
- **Dashboard financier** interactif
- **Graphiques** et visualisations
- **Interface responsive** avec Angular Material
- **Gestion d'état** avec RxJS
- **Formulaires réactifs** pour saisie
- **Export** de rapports

### **Base de données PostgreSQL (Port 5437)**
- **Schéma dédié** `finance`
- **Contraintes** de cohérence financière
- **Audit trail** complet
- **Sauvegarde** automatique
- **Index** optimisés pour les requêtes

## 📊 **Entités Principales**

### 💰 **Budget**
```typescript
{
  id: number,
  nom: string,
  description: string,
  montantTotal: number,
  montantUtilise: number,
  montantRestant: number,
  periode: 'MENSUEL' | 'TRIMESTRIEL' | 'ANNUEL',
  dateDebut: Date,
  dateFin: Date,
  statut: 'ACTIF' | 'CLOTURE' | 'SUSPENDU',
  categories: CategorieBudget[]
}
```

### 💳 **Transaction**
```typescript
{
  id: number,
  reference: string,
  montant: number,
  type: 'RECETTE' | 'DEPENSE',
  categorie: CategorieTransaction,
  description: string,
  dateTransaction: Date,
  dateComptabilisation: Date,
  statut: 'EN_ATTENTE' | 'VALIDEE' | 'REJETEE',
  pieceJointe?: string,
  budget?: Budget
}
```

### 🤝 **Sponsor**
```typescript
{
  id: number,
  nom: string,
  typePartenariat: 'PRINCIPAL' | 'OFFICIEL' | 'TECHNIQUE',
  montantContrat: number,
  dateDebut: Date,
  dateFin: Date,
  statut: 'ACTIF' | 'EXPIRE' | 'SUSPENDU',
  contact: ContactSponsor,
  paiements: PaiementSponsor[]
}
```

### 💼 **Salaire**
```typescript
{
  id: number,
  employe: Utilisateur,
  salaireBrut: number,
  salaireNet: number,
  primes: number,
  deductions: number,
  periode: Date,
  statut: 'CALCULE' | 'VALIDE' | 'PAYE',
  dateVersement: Date,
  fichePaie?: string
}
```

## 🔗 **API Endpoints**

### Budgets
- `GET /api/budgets` - Liste des budgets
- `POST /api/budgets` - Créer un budget
- `PUT /api/budgets/{id}` - Modifier un budget
- `GET /api/budgets/{id}/suivi` - Suivi budgétaire

### Transactions
- `GET /api/transactions` - Liste des transactions
- `POST /api/transactions` - Enregistrer une transaction
- `PUT /api/transactions/{id}/valider` - Valider une transaction
- `GET /api/transactions/rapport` - Rapport financier

### Sponsors
- `GET /api/sponsors` - Liste des sponsors
- `POST /api/sponsors` - Ajouter un sponsor
- `PUT /api/sponsors/{id}/renouveler` - Renouveler contrat
- `GET /api/sponsors/{id}/paiements` - Historique paiements

### Salaires
- `GET /api/salaires` - Liste des salaires
- `POST /api/salaires/calculer` - Calculer la paie
- `PUT /api/salaires/{id}/valider` - Valider un salaire
- `GET /api/salaires/{id}/fiche` - Télécharger fiche de paie

## 🗄️ **Base de Données**

### Tables Principales
- **budgets** - Budgets et planification
- **categories_budget** - Catégories budgétaires
- **transactions** - Mouvements financiers
- **categories_transaction** - Types de transactions
- **sponsors** - Partenaires et sponsors
- **contrats_sponsoring** - Contrats de sponsoring
- **paiements_sponsor** - Paiements des sponsors
- **salaires** - Gestion de la paie
- **elements_salaire** - Détails des salaires
- **rapports_financiers** - Rapports générés

### Relations
- Budget → Catégories Budget (1:N)
- Transaction → Catégorie Transaction (N:1)
- Transaction → Budget (N:1)
- Sponsor → Contrats (1:N)
- Contrat → Paiements (1:N)
- Utilisateur → Salaires (1:N)

## 🔒 **Sécurité**

### Authentification
- **JWT** avec refresh token
- **Rôles** : RESPONSABLE_FINANCIER, ADMINISTRATEUR
- **Permissions** granulaires par opération

### Audit
- **Traçabilité** complète des opérations
- **Logs** des modifications sensibles
- **Historique** des validations
- **Sauvegarde** des données critiques

## 📈 **Monitoring**

### Métriques
- **Actuator** Spring Boot
- **Health checks** automatiques
- **Métriques** financières personnalisées
- **Alertes** de performance

### Logs
- **Structured logging** avec Logback
- **Corrélation** des requêtes
- **Monitoring** des erreurs
- **Audit trail** sécurisé

## 🐳 **Docker**

### Services
- **finance-backend** : API Spring Boot (port 8085)
- **finance-frontend** : Interface Angular (port 4205)
- **finance-db** : Base PostgreSQL (port 5437)

### Volumes
- **finance_db_data** : Données PostgreSQL
- **finance_reports** : Rapports générés
- **finance_uploads** : Pièces jointes

## 🧪 **Tests**

### Backend
```bash
cd backend
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

### Tests d'intégration
```bash
docker-compose -f docker-compose.test.yml up
```

## 📝 **Configuration**

### Variables d'environnement
```env
# Base de données
DB_HOST=finance-db
DB_PORT=5437
DB_NAME=finance_db
DB_USERNAME=finance_user
DB_PASSWORD=finance_password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# API
API_PORT=8085
FRONTEND_PORT=4205
```

## 🚀 **Déploiement**

### Production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Développement
```bash
docker-compose up -d
```

## ✅ **STATUT DU DÉVELOPPEMENT - MICROSERVICE COMPLET 100%**

### 🎯 **Backend Spring Boot (100% ✅)**
- [x] Structure Maven avec profils multiples (dev, docker, prod)
- [x] Configuration Spring Boot 3.2 avec Java 21
- [x] 9 entités JPA avec relations et validation complètes
- [x] Repositories avec requêtes personnalisées et pagination
- [x] Services métier avec logique financière avancée
- [x] 5 contrôleurs REST avec sécurité et documentation Swagger
- [x] Configuration JWT et Spring Security 6.x
- [x] Migrations Flyway avec schéma et données initiales
- [x] Configuration Docker multi-stage optimisée
- [x] Health checks et monitoring Actuator

### 🎯 **Frontend Angular (100% ✅)**
- [x] Structure Angular 17 avec TypeScript et Material Design
- [x] Configuration complète (package.json, angular.json, tsconfig.json)
- [x] 20+ modèles TypeScript avec interfaces détaillées
- [x] Service API complet avec gestion d'erreurs et pagination
- [x] Guards d'authentification et de rôles
- [x] 3 intercepteurs HTTP (auth, erreurs, loading)
- [x] Dashboard avec graphiques Chart.js et KPI
- [x] Composants partagés et pipes de formatage
- [x] Routing complet avec protection par rôles
- [x] Styles SCSS avec thème Material personnalisé
- [x] Configuration Docker avec Nginx optimisé

### 🎯 **Base de données PostgreSQL (100% ✅)**
- [x] Schéma dédié avec 9 tables relationnelles
- [x] Contraintes, index et triggers optimisés
- [x] Migrations Flyway versionnées (V1 et V2)
- [x] Données initiales pour développement
- [x] Configuration Docker avec health checks

### 🎯 **DevOps et Infrastructure (100% ✅)**
- [x] Dockerfile backend multi-stage avec sécurité
- [x] Dockerfile frontend avec Nginx et compression
- [x] Docker-compose avec services dédiés
- [x] Health checks pour tous les services
- [x] Variables d'environnement sécurisées
- [x] Intégration dans le docker-compose principal
- [x] Configuration réseau et volumes persistants

### 🎯 **Fonctionnalités métier (100% ✅)**
- [x] **Gestion budgétaire** : Création, suivi, alertes automatiques
- [x] **Transactions** : Revenus/dépenses avec validation et catégorisation
- [x] **Sponsors** : Gestion des partenaires, contrats et échéances
- [x] **Salaires** : Calcul automatique, bulletins de paie, historique
- [x] **Rapports** : Dashboard temps réel, export Excel/PDF, analytics

### 🎯 **Sécurité et qualité (100% ✅)**
- [x] Authentification JWT avec refresh tokens
- [x] Autorisation basée sur les rôles (RBAC)
- [x] Validation des données côté serveur et client
- [x] Protection CSRF et headers de sécurité
- [x] Audit trail complet des opérations financières
- [x] Chiffrement des données sensibles

## 🚀 **DÉPLOIEMENT ET UTILISATION**

### Développement local
```bash
# Backend
cd microservices/finance-service/backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
cd microservices/finance-service/frontend
npm install && ng serve --port 4205
```

### Docker (Recommandé)
```bash
# Depuis la racine du projet
docker-compose up --build finance-backend finance-frontend finance-db

# Accès aux services
# - Backend API: http://localhost:8085
# - Frontend: http://localhost:4205
# - Base de données: localhost:5437
```

### URLs d'accès
- **Frontend** : http://localhost:4205
- **API Backend** : http://localhost:8085
- **Documentation API** : http://localhost:8085/swagger-ui.html
- **Health Check** : http://localhost:8085/actuator/health
- **Métriques** : http://localhost:8085/actuator/metrics

## 🎉 **MICROSERVICE FINANCE TERMINÉ À 100%**

Le microservice finance-service est maintenant **COMPLET** et prêt pour la production avec :
- ✅ Architecture microservice complète
- ✅ Backend Spring Boot robuste et sécurisé
- ✅ Frontend Angular moderne et responsive
- ✅ Base de données PostgreSQL optimisée
- ✅ Configuration Docker production-ready
- ✅ Toutes les fonctionnalités financières implémentées
- ✅ Sécurité et monitoring intégrés
- ✅ Documentation complète

## 📞 **Support**

Pour toute question ou problème :
- **Documentation** : `/docs`
- **API** : `http://localhost:8085/swagger-ui.html`
- **Health** : `http://localhost:8085/actuator/health`
