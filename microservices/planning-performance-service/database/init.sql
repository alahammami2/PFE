-- =====================================================
-- SprintBot - Planning Performance Service Database
-- =====================================================

-- Création du schéma
CREATE SCHEMA IF NOT EXISTS planning_performance;
SET search_path TO planning_performance;

-- =====================================================
-- Table des entraînements
-- =====================================================
CREATE TABLE entrainements (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_entrainement DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    lieu VARCHAR(255),
    type_entrainement VARCHAR(50) NOT NULL, -- PHYSIQUE, TECHNIQUE, TACTIQUE, MATCH
    niveau_intensite INTEGER CHECK (niveau_intensite BETWEEN 1 AND 10),
    coach_id BIGINT NOT NULL, -- Référence vers auth-user-service
    statut VARCHAR(20) DEFAULT 'PLANIFIE', -- PLANIFIE, EN_COURS, TERMINE, ANNULE
    objectifs TEXT,
    materiel_requis TEXT,
    nombre_max_joueurs INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table des participations aux entraînements
-- =====================================================
CREATE TABLE participations (
    id BIGSERIAL PRIMARY KEY,
    entrainement_id BIGINT NOT NULL REFERENCES entrainements(id) ON DELETE CASCADE,
    joueur_id BIGINT NOT NULL, -- Référence vers auth-user-service
    statut_participation VARCHAR(20) DEFAULT 'INSCRIT', -- INSCRIT, PRESENT, ABSENT, EXCUSE
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT,
    UNIQUE(entrainement_id, joueur_id)
);

-- =====================================================
-- Table des performances
-- =====================================================
CREATE TABLE performances (
    id BIGSERIAL PRIMARY KEY,
    entrainement_id BIGINT NOT NULL REFERENCES entrainements(id) ON DELETE CASCADE,
    joueur_id BIGINT NOT NULL, -- Référence vers auth-user-service
    note_globale DECIMAL(3,1) CHECK (note_globale BETWEEN 0 AND 10),
    note_technique DECIMAL(3,1) CHECK (note_technique BETWEEN 0 AND 10),
    note_physique DECIMAL(3,1) CHECK (note_physique BETWEEN 0 AND 10),
    note_mental DECIMAL(3,1) CHECK (note_mental BETWEEN 0 AND 10),
    commentaire_coach TEXT,
    auto_evaluation DECIMAL(3,1) CHECK (auto_evaluation BETWEEN 0 AND 10),
    commentaire_joueur TEXT,
    objectifs_atteints BOOLEAN DEFAULT FALSE,
    points_forts TEXT,
    points_amelioration TEXT,
    evaluateur_id BIGINT NOT NULL, -- Coach qui évalue
    date_evaluation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(entrainement_id, joueur_id)
);

-- =====================================================
-- Table des absences
-- =====================================================
CREATE TABLE absences (
    id BIGSERIAL PRIMARY KEY,
    entrainement_id BIGINT NOT NULL REFERENCES entrainements(id) ON DELETE CASCADE,
    joueur_id BIGINT NOT NULL, -- Référence vers auth-user-service
    motif VARCHAR(100) NOT NULL, -- MALADIE, BLESSURE, PERSONNEL, PROFESSIONNEL, AUTRE
    description TEXT,
    justifiee BOOLEAN DEFAULT FALSE,
    justificatif_url VARCHAR(500), -- URL du document justificatif
    date_declaration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    declarant_id BIGINT NOT NULL, -- Qui a déclaré l'absence (joueur ou coach)
    UNIQUE(entrainement_id, joueur_id)
);

-- =====================================================
-- Table des objectifs individuels
-- =====================================================
CREATE TABLE objectifs_individuels (
    id BIGSERIAL PRIMARY KEY,
    joueur_id BIGINT NOT NULL, -- Référence vers auth-user-service
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_objectif VARCHAR(50) NOT NULL, -- TECHNIQUE, PHYSIQUE, MENTAL, TACTIQUE
    date_creation DATE NOT NULL,
    date_echeance DATE,
    statut VARCHAR(20) DEFAULT 'EN_COURS', -- EN_COURS, ATTEINT, ABANDONNE, REPORTE
    progression INTEGER DEFAULT 0 CHECK (progression BETWEEN 0 AND 100),
    coach_id BIGINT NOT NULL, -- Coach qui a défini l'objectif
    commentaires TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table des statistiques d'entraînement
-- =====================================================
CREATE TABLE statistiques_entrainement (
    id BIGSERIAL PRIMARY KEY,
    joueur_id BIGINT NOT NULL, -- Référence vers auth-user-service
    mois INTEGER NOT NULL CHECK (mois BETWEEN 1 AND 12),
    annee INTEGER NOT NULL,
    nombre_entrainements_planifies INTEGER DEFAULT 0,
    nombre_entrainements_presents INTEGER DEFAULT 0,
    nombre_absences INTEGER DEFAULT 0,
    taux_presence DECIMAL(5,2) DEFAULT 0.0,
    moyenne_performance DECIMAL(3,1) DEFAULT 0.0,
    progression_mensuelle DECIMAL(3,1) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(joueur_id, mois, annee)
);

-- =====================================================
-- Index pour optimiser les performances
-- =====================================================
CREATE INDEX idx_entrainements_date ON entrainements(date_entrainement);
CREATE INDEX idx_entrainements_coach ON entrainements(coach_id);
CREATE INDEX idx_participations_joueur ON participations(joueur_id);
CREATE INDEX idx_participations_entrainement ON participations(entrainement_id);
CREATE INDEX idx_performances_joueur ON performances(joueur_id);
CREATE INDEX idx_performances_entrainement ON performances(entrainement_id);
CREATE INDEX idx_absences_joueur ON absences(joueur_id);
CREATE INDEX idx_objectifs_joueur ON objectifs_individuels(joueur_id);
CREATE INDEX idx_statistiques_joueur_date ON statistiques_entrainement(joueur_id, annee, mois);

-- =====================================================
-- Données de test
-- =====================================================

-- Entraînements de test
INSERT INTO entrainements (titre, description, date_entrainement, heure_debut, heure_fin, lieu, type_entrainement, niveau_intensite, coach_id, objectifs, nombre_max_joueurs) VALUES
('Entraînement Technique', 'Travail sur les passes et réceptions', '2024-08-01', '18:00', '20:00', 'Gymnase Principal', 'TECHNIQUE', 6, 2, 'Améliorer la précision des passes', 12),
('Préparation Physique', 'Renforcement musculaire et cardio', '2024-08-02', '17:30', '19:30', 'Salle de Sport', 'PHYSIQUE', 8, 2, 'Développer l''endurance et la force', 15),
('Tactique Collective', 'Schémas de jeu et positionnement', '2024-08-03', '19:00', '21:00', 'Gymnase Principal', 'TACTIQUE', 7, 2, 'Maîtriser les systèmes de jeu', 12),
('Match Amical', 'Match d''entraînement contre équipe locale', '2024-08-04', '15:00', '17:00', 'Gymnase Municipal', 'MATCH', 9, 2, 'Appliquer les acquis en situation réelle', 12);

-- Participations de test (joueurs avec IDs 3, 4, 5 de auth-user-service)
INSERT INTO participations (entrainement_id, joueur_id, statut_participation) VALUES
(1, 3, 'PRESENT'), (1, 4, 'PRESENT'), (1, 5, 'ABSENT'),
(2, 3, 'PRESENT'), (2, 4, 'EXCUSE'), (2, 5, 'PRESENT'),
(3, 3, 'PRESENT'), (3, 4, 'PRESENT'), (3, 5, 'PRESENT'),
(4, 3, 'INSCRIT'), (4, 4, 'INSCRIT'), (4, 5, 'INSCRIT');

-- Performances de test
INSERT INTO performances (entrainement_id, joueur_id, note_globale, note_technique, note_physique, note_mental, commentaire_coach, auto_evaluation, commentaire_joueur, objectifs_atteints, points_forts, points_amelioration, evaluateur_id) VALUES
(1, 3, 8.5, 9.0, 7.5, 8.0, 'Excellente technique, continue comme ça', 8.0, 'Je me sens en progrès', true, 'Précision des passes', 'Vitesse de réaction', 2),
(1, 4, 7.0, 6.5, 8.0, 7.5, 'Bon physique, travaille la technique', 7.5, 'Besoin de plus de pratique technique', false, 'Condition physique', 'Technique de réception', 2),
(2, 3, 9.0, 8.0, 9.5, 8.5, 'Performance exceptionnelle', 8.5, 'Très satisfait de ma séance', true, 'Endurance remarquable', 'Maintenir ce niveau', 2),
(2, 5, 6.5, 6.0, 7.0, 6.5, 'Effort correct, peut mieux faire', 6.0, 'Difficile mais instructif', false, 'Motivation', 'Condition physique générale', 2);

-- Absences de test
INSERT INTO absences (entrainement_id, joueur_id, motif, description, justifiee, date_declaration, declarant_id) VALUES
(1, 5, 'MALADIE', 'Grippe saisonnière', true, CURRENT_TIMESTAMP, 5),
(2, 4, 'PROFESSIONNEL', 'Réunion de travail importante', true, CURRENT_TIMESTAMP, 4);

-- Objectifs individuels de test
INSERT INTO objectifs_individuels (joueur_id, titre, description, type_objectif, date_creation, date_echeance, progression, coach_id, commentaires) VALUES
(3, 'Améliorer le service', 'Atteindre 80% de réussite au service', 'TECHNIQUE', '2024-07-01', '2024-09-01', 65, 2, 'Bon progrès, continue les exercices spécifiques'),
(4, 'Développer l''endurance', 'Courir 5km en moins de 25 minutes', 'PHYSIQUE', '2024-07-01', '2024-08-15', 40, 2, 'Progression lente mais régulière'),
(5, 'Confiance en soi', 'Prendre plus d''initiatives en match', 'MENTAL', '2024-07-15', '2024-10-01', 30, 2, 'Travail avec le préparateur mental nécessaire');

-- Statistiques de test
INSERT INTO statistiques_entrainement (joueur_id, mois, annee, nombre_entrainements_planifies, nombre_entrainements_presents, nombre_absences, taux_presence, moyenne_performance, progression_mensuelle) VALUES
(3, 7, 2024, 8, 7, 1, 87.50, 8.75, 0.5),
(4, 7, 2024, 8, 6, 2, 75.00, 7.25, 0.2),
(5, 7, 2024, 8, 5, 3, 62.50, 6.50, -0.1);

COMMIT;
