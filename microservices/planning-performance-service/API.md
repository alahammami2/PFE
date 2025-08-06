# 📚 API Documentation - Planning Performance Service

## 🎯 Vue d'ensemble

Cette documentation détaille tous les endpoints disponibles dans le microservice Planning Performance Service de SprintBot.

**Base URL**: `http://localhost:8082/api`

## 🔐 Authentification

Tous les endpoints nécessitent une authentification JWT. Incluez le token dans l'en-tête :

```http
Authorization: Bearer <your-jwt-token>
```

## 🏃‍♂️ Entraînements

### GET /entrainements
Récupère la liste des entraînements avec pagination et filtres.

**Paramètres de requête :**
- `page` (int, optionnel) : Numéro de page (défaut: 0)
- `size` (int, optionnel) : Taille de page (défaut: 20)
- `sort` (string, optionnel) : Tri (ex: "date,desc")
- `type` (string, optionnel) : Type d'entraînement
- `dateDebut` (date, optionnel) : Date de début (YYYY-MM-DD)
- `dateFin` (date, optionnel) : Date de fin (YYYY-MM-DD)

**Réponse :**
```json
{
  "content": [
    {
      "id": 1,
      "titre": "Entraînement Technique",
      "description": "Travail sur les attaques",
      "date": "2024-01-15",
      "heureDebut": "18:00",
      "heureFin": "20:00",
      "lieu": "Gymnase Principal",
      "type": "TECHNIQUE",
      "intensite": 7,
      "statut": "PLANIFIE",
      "objectifs": ["Améliorer les attaques"],
      "equipementNecessaire": ["Ballons"],
      "nombreMaxParticipants": 12,
      "coachId": 1,
      "createdAt": "2024-01-10T10:00:00Z",
      "updatedAt": "2024-01-10T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "direction": "DESC",
      "property": "date"
    }
  },
  "totalElements": 50,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

### POST /entrainements
Crée un nouvel entraînement.

**Corps de la requête :**
```json
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

**Réponse :** `201 Created`
```json
{
  "id": 1,
  "titre": "Entraînement Technique",
  "description": "Travail sur les attaques et les blocs",
  "date": "2024-01-15",
  "heureDebut": "18:00",
  "heureFin": "20:00",
  "lieu": "Gymnase Principal",
  "type": "TECHNIQUE",
  "intensite": 7,
  "statut": "PLANIFIE",
  "objectifs": ["Améliorer les attaques", "Perfectionner les blocs"],
  "equipementNecessaire": ["Ballons", "Filets"],
  "nombreMaxParticipants": 12,
  "coachId": 1,
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-01-15T10:00:00Z"
}
```

### GET /entrainements/{id}
Récupère les détails d'un entraînement spécifique.

**Paramètres :**
- `id` (long) : ID de l'entraînement

**Réponse :** `200 OK`
```json
{
  "id": 1,
  "titre": "Entraînement Technique",
  "description": "Travail sur les attaques et les blocs",
  "date": "2024-01-15",
  "heureDebut": "18:00",
  "heureFin": "20:00",
  "lieu": "Gymnase Principal",
  "type": "TECHNIQUE",
  "intensite": 7,
  "statut": "PLANIFIE",
  "objectifs": ["Améliorer les attaques", "Perfectionner les blocs"],
  "equipementNecessaire": ["Ballons", "Filets"],
  "nombreMaxParticipants": 12,
  "coachId": 1,
  "participations": [
    {
      "id": 1,
      "joueurId": 5,
      "heureInscription": "2024-01-14T15:30:00Z",
      "statutPresence": "INSCRIT"
    }
  ],
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-01-15T10:00:00Z"
}
```

### PUT /entrainements/{id}
Met à jour un entraînement existant.

**Paramètres :**
- `id` (long) : ID de l'entraînement

**Corps de la requête :**
```json
{
  "titre": "Entraînement Technique Modifié",
  "description": "Travail intensif sur les attaques",
  "intensite": 8
}
```

**Réponse :** `200 OK` (même structure que GET)

### DELETE /entrainements/{id}
Supprime un entraînement.

**Paramètres :**
- `id` (long) : ID de l'entraînement

**Réponse :** `204 No Content`

### GET /entrainements/calendrier
Récupère les entraînements pour la vue calendrier.

**Paramètres de requête :**
- `mois` (int) : Mois (1-12)
- `annee` (int) : Année
- `vue` (string) : Type de vue ("mois", "semaine", "jour")

**Réponse :**
```json
{
  "evenements": [
    {
      "id": 1,
      "titre": "Entraînement Technique",
      "date": "2024-01-15",
      "heureDebut": "18:00",
      "heureFin": "20:00",
      "type": "TECHNIQUE",
      "intensite": 7,
      "lieu": "Gymnase Principal",
      "nombreParticipants": 8,
      "nombreMaxParticipants": 12
    }
  ],
  "statistiques": {
    "totalEntrainements": 15,
    "intensiteMoyenne": 6.5,
    "tauxParticipation": 85.2
  }
}
```

## 👥 Participations

### GET /participations
Récupère la liste des participations.

**Paramètres de requête :**
- `entrainementId` (long, optionnel) : ID de l'entraînement
- `joueurId` (long, optionnel) : ID du joueur
- `statutPresence` (string, optionnel) : Statut de présence

**Réponse :**
```json
[
  {
    "id": 1,
    "entrainementId": 1,
    "joueurId": 5,
    "heureInscription": "2024-01-14T15:30:00Z",
    "heurePresence": "2024-01-15T18:05:00Z",
    "statutPresence": "PRESENT",
    "commentaire": "Arrivé légèrement en retard"
  }
]
```

### POST /participations
Enregistre une nouvelle participation.

**Corps de la requête :**
```json
{
  "entrainementId": 1,
  "joueurId": 5,
  "commentaire": "Inscription de dernière minute"
}
```

**Réponse :** `201 Created`

### PUT /participations/{id}/presence
Met à jour le statut de présence d'une participation.

**Paramètres :**
- `id` (long) : ID de la participation

**Corps de la requête :**
```json
{
  "statutPresence": "PRESENT",
  "heurePresence": "2024-01-15T18:05:00Z",
  "commentaire": "Présent à l'heure"
}
```

**Réponse :** `200 OK`

### GET /participations/statistiques
Récupère les statistiques de participation.

**Paramètres de requête :**
- `joueurId` (long, optionnel) : ID du joueur
- `dateDebut` (date, optionnel) : Date de début
- `dateFin` (date, optionnel) : Date de fin

**Réponse :**
```json
{
  "tauxParticipationGlobal": 85.2,
  "nombreTotalEntrainements": 20,
  "nombreParticipations": 17,
  "nombreAbsences": 3,
  "tendance": "stable",
  "participationsParMois": {
    "2024-01": 8,
    "2024-02": 9
  }
}
```

## 📊 Performances

### GET /performances
Récupère la liste des évaluations de performance.

**Paramètres de requête :**
- `joueurId` (long, optionnel) : ID du joueur
- `entrainementId` (long, optionnel) : ID de l'entraînement
- `categorie` (string, optionnel) : Catégorie d'évaluation
- `dateDebut` (date, optionnel) : Date de début
- `dateFin` (date, optionnel) : Date de fin

**Réponse :**
```json
[
  {
    "id": 1,
    "entrainementId": 1,
    "joueurId": 5,
    "categorie": "TECHNIQUE",
    "note": 8.5,
    "commentaire": "Excellente progression sur les services",
    "evaluateurId": 2,
    "dateEvaluation": "2024-01-15T20:30:00Z"
  }
]
```

### POST /performances
Crée une nouvelle évaluation de performance.

**Corps de la requête :**
```json
{
  "entrainementId": 1,
  "joueurId": 5,
  "categorie": "TECHNIQUE",
  "note": 8.5,
  "commentaire": "Excellente progression sur les services"
}
```

**Réponse :** `201 Created`

### GET /performances/joueur/{joueurId}
Récupère toutes les performances d'un joueur.

**Paramètres :**
- `joueurId` (long) : ID du joueur

**Paramètres de requête :**
- `categorie` (string, optionnel) : Catégorie spécifique
- `limite` (int, optionnel) : Nombre max de résultats

**Réponse :**
```json
{
  "joueurId": 5,
  "performances": [
    {
      "id": 1,
      "entrainementId": 1,
      "categorie": "TECHNIQUE",
      "note": 8.5,
      "commentaire": "Excellente progression",
      "dateEvaluation": "2024-01-15T20:30:00Z"
    }
  ],
  "statistiques": {
    "noteMoyenne": 7.8,
    "meilleureNote": 9.0,
    "nombreEvaluations": 15,
    "progressionGlobale": 12.5,
    "repartitionParCategorie": {
      "TECHNIQUE": 8.2,
      "PHYSIQUE": 7.5,
      "TACTIQUE": 7.8,
      "MENTAL": 8.0
    }
  }
}
```

### GET /performances/analytics
Récupère les analyses de performance avancées.

**Paramètres de requête :**
- `joueurId` (long, optionnel) : ID du joueur
- `periode` (string) : Période d'analyse ("semaine", "mois", "trimestre")

**Réponse :**
```json
{
  "tendanceGlobale": "progression",
  "evolutionParCategorie": {
    "TECHNIQUE": {
      "tendance": "progression",
      "pourcentageEvolution": 15.2,
      "noteActuelle": 8.2,
      "notePrecedente": 7.1
    }
  },
  "comparaisonEquipe": {
    "positionClassement": 3,
    "totalJoueurs": 12,
    "ecartMoyenne": 0.8
  },
  "recommandations": [
    "Continuer le travail technique",
    "Améliorer l'aspect physique"
  ]
}
```

## 🚫 Absences

### GET /absences
Récupère la liste des absences.

**Paramètres de requête :**
- `joueurId` (long, optionnel) : ID du joueur
- `entrainementId` (long, optionnel) : ID de l'entraînement
- `type` (string, optionnel) : Type d'absence
- `statut` (string, optionnel) : Statut de l'absence

**Réponse :**
```json
[
  {
    "id": 1,
    "entrainementId": 1,
    "joueurId": 3,
    "type": "MALADIE",
    "motif": "Grippe",
    "statut": "APPROUVEE",
    "justificationFournie": true,
    "commentaire": "Certificat médical fourni",
    "commentaireApprobation": "Absence justifiée",
    "dateDeclaration": "2024-01-14T10:00:00Z",
    "dateTraitement": "2024-01-14T14:30:00Z",
    "traitePar": 2
  }
]
```

### POST /absences
Déclare une nouvelle absence.

**Corps de la requête :**
```json
{
  "entrainementId": 1,
  "joueurId": 3,
  "type": "MALADIE",
  "motif": "Grippe avec fièvre",
  "justificationFournie": true,
  "commentaire": "Certificat médical disponible"
}
```

**Réponse :** `201 Created`

### POST /absences/{id}/approuver
Approuve une absence.

**Paramètres :**
- `id` (long) : ID de l'absence

**Corps de la requête :**
```json
{
  "commentaireApprobation": "Absence justifiée par certificat médical"
}
```

**Réponse :** `200 OK`

### POST /absences/{id}/rejeter
Rejette une absence.

**Paramètres :**
- `id` (long) : ID de l'absence

**Corps de la requête :**
```json
{
  "commentaireApprobation": "Justification insuffisante"
}
```

**Réponse :** `200 OK`

### GET /absences/en-attente
Récupère les absences en attente d'approbation.

**Réponse :**
```json
[
  {
    "id": 2,
    "entrainementId": 2,
    "joueurId": 4,
    "type": "URGENCE_FAMILIALE",
    "motif": "Urgence familiale",
    "statut": "EN_ATTENTE",
    "justificationFournie": false,
    "dateDeclaration": "2024-01-16T08:00:00Z"
  }
]
```

## 🎯 Objectifs

### GET /objectifs
Récupère la liste des objectifs individuels.

**Paramètres de requête :**
- `joueurId` (long, optionnel) : ID du joueur
- `type` (string, optionnel) : Type d'objectif
- `statut` (string, optionnel) : Statut de l'objectif
- `priorite` (string, optionnel) : Priorité de l'objectif

**Réponse :**
```json
[
  {
    "id": 1,
    "joueurId": 5,
    "titre": "Améliorer le service",
    "description": "Atteindre 80% de réussite au service",
    "type": "TECHNIQUE",
    "priorite": "HAUTE",
    "dateDebut": "2024-01-01",
    "dateFin": "2024-03-01",
    "statut": "EN_COURS",
    "progression": 65,
    "criteresMesure": "Pourcentage de réussite au service",
    "valeurCible": 80,
    "valeurActuelle": 72,
    "uniteMesure": "%"
  }
]
```

### POST /objectifs
Crée un nouvel objectif individuel.

**Corps de la requête :**
```json
{
  "joueurId": 5,
  "titre": "Améliorer le service",
  "description": "Atteindre 80% de réussite au service",
  "type": "TECHNIQUE",
  "priorite": "HAUTE",
  "dateDebut": "2024-01-01",
  "dateFin": "2024-03-01",
  "criteresMesure": "Pourcentage de réussite au service",
  "valeurCible": 80,
  "uniteMesure": "%"
}
```

**Réponse :** `201 Created`

### POST /objectifs/{id}/progression
Met à jour la progression d'un objectif.

**Paramètres :**
- `id` (long) : ID de l'objectif

**Corps de la requête :**
```json
{
  "progression": 75,
  "valeurActuelle": 76,
  "commentaire": "Bonne progression cette semaine"
}
```

**Réponse :** `200 OK`

### GET /objectifs/en-retard
Récupère les objectifs en retard.

**Réponse :**
```json
[
  {
    "id": 2,
    "joueurId": 6,
    "titre": "Améliorer la réception",
    "dateFin": "2024-01-10",
    "progression": 45,
    "joursRetard": 5,
    "priorite": "HAUTE"
  }
]
```

## 📈 Statistiques

### GET /statistiques/globales
Récupère les statistiques globales.

**Paramètres de requête :**
- `dateDebut` (date, optionnel) : Date de début
- `dateFin` (date, optionnel) : Date de fin

**Réponse :**
```json
{
  "totalEntrainements": 45,
  "dureeTotale": 5400,
  "dureeMoyenne": 120,
  "intensiteMoyenne": 6.8,
  "caloriesMoyennes": 450,
  "nombreJoueursActifs": 15,
  "tauxParticipation": 87.5,
  "evolutionDernierMois": {
    "entrainements": 12,
    "duree": 1440,
    "intensite": 7.2
  },
  "repartitionIntensite": {
    "faible": 15,
    "moderee": 45,
    "elevee": 30,
    "tresElevee": 10
  },
  "topJoueurs": [
    {
      "joueurId": 5,
      "scorePerformance": 85,
      "nombreEntrainements": 18
    }
  ]
}
```

### GET /statistiques/joueur/{joueurId}
Récupère les statistiques d'un joueur spécifique.

**Paramètres :**
- `joueurId` (long) : ID du joueur

**Réponse :**
```json
{
  "joueurId": 5,
  "nombreEntrainements": 18,
  "dureeTotale": 2160,
  "dureeMoyenne": 120,
  "intensiteMoyenne": 7.2,
  "caloriesMoyennes": 480,
  "scorePerformanceMoyen": 8.1,
  "tendanceProgression": "croissante",
  "evolutionIntensiteParMois": {
    "2024-01": 6.8,
    "2024-02": 7.2
  },
  "statistiquesVolleyball": {
    "moyenneServices": 15,
    "moyenneAttaques": 25,
    "tauxReussiteService": 78,
    "tauxReussiteAttaque": 65
  },
  "comparaisonEquipe": {
    "intensite": "superieur",
    "duree": "moyen",
    "performance": "superieur"
  },
  "recommandations": [
    "Continuer sur cette lancée",
    "Travailler la régularité"
  ]
}
```

## ❌ Codes d'Erreur

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Données de requête invalides",
  "details": [
    {
      "field": "intensite",
      "message": "L'intensité doit être comprise entre 1 et 10"
    }
  ],
  "path": "/api/entrainements"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT manquant ou invalide",
  "path": "/api/entrainements"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Accès refusé - permissions insuffisantes",
  "path": "/api/entrainements/1"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Entraînement non trouvé avec l'ID: 999",
  "path": "/api/entrainements/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Une erreur interne s'est produite",
  "path": "/api/entrainements"
}
```

## 📝 Notes

- Tous les timestamps sont en format ISO 8601 UTC
- Les dates sont au format YYYY-MM-DD
- Les heures sont au format HH:MM
- La pagination utilise une indexation basée sur 0
- Les filtres de recherche supportent les opérateurs SQL LIKE
- Les réponses sont toujours en format JSON
- L'encodage des caractères est UTF-8
