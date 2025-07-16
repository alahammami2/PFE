# SprintBot

Plateforme intelligente pour gérer une équipe de volley-ball

## Description

SprintBot est une plateforme intelligente développée en Spring Boot pour gérer une équipe de volley-ball. Elle permet de gérer les joueurs, les entraînements, les performances, les finances et bien plus encore.

## Architecture

### Entités Principales

#### Utilisateurs (Héritage)
- **Utilisateur** (classe mère abstraite)
  - **Joueur** - Membres de l'équipe avec statistiques physiques et performances
  - **Coach** - Entraîneurs avec spécialités
  - **Administrateur** - Gestionnaires du système
  - **StaffMedical** - Personnel médical
  - **ResponsableFinancier** - Gestionnaires financiers

#### Gestion d'Équipe
- **Equipe** - Équipes de volley-ball
- **Planning** - Planification des entraînements et matchs
- **Evenement** - Événements liés aux plannings
- **RendezVous** - Rendez-vous individuels

#### Performance et Suivi
- **Performance** - Évaluations des joueurs
- **Absence** - Gestion des absences
- **DonneesSante** - Suivi médical des joueurs

#### Communication
- **Message** - Système de messagerie
- **Chatbot** - Bot automatisé
- **DroitsAcces** - Gestion des permissions

#### Finances
- **Budget** - Budgets d'équipe
- **CategorieBudget** - Catégories de dépenses
- **Depense** - Dépenses réelles
- **DemandeAdministrative** - Demandes administratives

## Technologies utilisées

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Web**
- **Spring Validation**
- **Spring Actuator**
- **PostgreSQL** (production)
- **H2 Database** (développement)
- **Maven**

## Prérequis

- Java 21 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 12+ (pour la production)

## Configuration

### Base de Données

#### PostgreSQL (Production)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sprintbot_db
spring.datasource.username=sprintbot_user
spring.datasource.password=sprintbot_password
```

#### H2 (Développement)
```properties
# Utiliser le profil 'dev'
spring.profiles.active=dev
```

### Installation PostgreSQL

1. Exécuter le script `database-setup.sql` en tant qu'administrateur PostgreSQL :
```bash
psql -U postgres -f database-setup.sql
```

## Installation et démarrage

1. Cloner le projet
2. Naviguer vers le dossier du projet :
   ```bash
   cd SprintBot
   ```

3. Compiler l'application :
   ```bash
   mvn clean compile
   ```

4. Démarrer l'application :

   **Avec PostgreSQL :**
   ```bash
   mvn spring-boot:run
   ```

   **Avec H2 (développement) :**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=dev
   ```

5. L'application sera accessible à l'adresse : `http://localhost:8080`

## Fonctionnalités

### Gestion des Joueurs
- Profils complets avec données physiques
- Suivi des performances
- Gestion des absences
- Historique médical

### Gestion des Entraînements
- Planification des séances
- Suivi de la participation
- Évaluation des performances

### Communication
- Système de messagerie interne
- Notifications automatiques
- Chatbot pour les questions fréquentes

### Finances
- Gestion des budgets par catégorie
- Suivi des dépenses
- Rapports financiers

### Administration
- Gestion des utilisateurs
- Contrôle des accès
- Demandes administratives

## Base de données

### PostgreSQL (Production)
Configuration dans `application.properties` pour la production.

### H2 (Développement)
En mode développement, l'application utilise une base de données H2 en mémoire.
Vous pouvez accéder à la console H2 via : `http://localhost:8080/h2-console`

**Paramètres de connexion H2 :**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Tests

Pour exécuter les tests :
```bash
mvn test
```

## Structure du projet

```
src/main/java/com/volleyball/sprintbot/
├── entity/          # Entités JPA (✓ Créées)
├── repository/      # Repositories Spring Data (✓ Créées)
├── service/         # Services métier (à créer)
├── controller/      # Contrôleurs REST (à créer)
└── config/          # Configuration (à créer)
```

### Entités Créées
- Utilisateur (classe mère abstraite)
- Joueur, Coach, Administrateur, StaffMedical, ResponsableFinancier
- Equipe, Planning, Evenement, RendezVous
- Performance, Absence, DonneesSante
- Message, Chatbot, DroitsAcces
- Budget, CategorieBudget, Depense, DemandeAdministrative

### Repositories Créés
- UtilisateurRepository, JoueurRepository, CoachRepository
- EquipeRepository, MessageRepository, AbsenceRepository
- PerformanceRepository, BudgetRepository

## Développement

L'application utilise Spring Boot DevTools pour le rechargement automatique pendant le développement.

### Tests
Les tests utilisent H2 en mémoire pour l'isolation et la rapidité.

## Sécurité

- Authentification par email/mot de passe
- Gestion des rôles et permissions
- Contrôle d'accès basé sur les droits

## Monitoring

- Console H2 disponible en développement : `/h2-console`
- Actuator endpoints : `/actuator`
- Logs détaillés avec niveaux configurables

## Contribution

1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commit vos changements
4. Push vers la branche
5. Ouvrir une Pull Request
