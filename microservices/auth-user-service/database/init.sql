-- Script d'initialisation de la base de données auth_user_db
-- Microservice auth-user-service pour SprintBot

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS auth_user_db;

-- Utilisation de la base de données
\c auth_user_db;

-- Création de l'utilisateur dédié
CREATE USER IF NOT EXISTS auth_user WITH PASSWORD 'auth_user_password';

-- Attribution des privilèges
GRANT ALL PRIVILEGES ON DATABASE auth_user_db TO auth_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO auth_user;

-- Extension pour UUID (optionnel)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table principale des utilisateurs (avec héritage JPA)
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    type_utilisateur VARCHAR(50) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50),
    avatar_url VARCHAR(255),
    
    -- Index pour améliorer les performances
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_role_valid CHECK (role IN ('JOUEUR', 'COACH', 'ADMINISTRATEUR', 'STAFF_MEDICAL', 'RESPONSABLE_FINANCIER'))
);

-- Table des joueurs (héritage)
CREATE TABLE IF NOT EXISTS joueurs (
    id BIGINT PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
    taille DECIMAL(3,2) CHECK (taille > 1.0 AND taille < 2.5),
    poids DECIMAL(5,2) CHECK (poids > 30.0 AND poids < 200.0),
    poste VARCHAR(50),
    date_naissance DATE,
    statut VARCHAR(50) DEFAULT 'ACTIF',
    numero_maillot INTEGER UNIQUE,
    nationalite VARCHAR(100),
    experience_annees INTEGER,
    niveau VARCHAR(20) DEFAULT 'DEBUTANT',
    main_dominante VARCHAR(20),
    
    CONSTRAINT chk_statut_joueur CHECK (statut IN ('ACTIF', 'BLESSE', 'SUSPENDU', 'INACTIF')),
    CONSTRAINT chk_niveau_joueur CHECK (niveau IN ('DEBUTANT', 'INTERMEDIAIRE', 'AVANCE', 'PROFESSIONNEL')),
    CONSTRAINT chk_main_dominante CHECK (main_dominante IN ('DROITE', 'GAUCHE', 'AMBIDEXTRE'))
);

-- Table des coaches (héritage)
CREATE TABLE IF NOT EXISTS coaches (
    id BIGINT PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
    specialite VARCHAR(100),
    experience INTEGER,
    certification VARCHAR(50),
    formation VARCHAR(100),
    date_debut_carriere DATE,
    type_coach VARCHAR(20) DEFAULT 'ASSISTANT',
    salaire DECIMAL(10,2),
    biographie TEXT,
    actif_coaching BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT chk_type_coach CHECK (type_coach IN ('PRINCIPAL', 'ASSISTANT', 'SPECIALISE'))
);

-- Table des administrateurs (héritage)
CREATE TABLE IF NOT EXISTS administrateurs (
    id BIGINT PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
    departement VARCHAR(100),
    niveau_acces VARCHAR(50) DEFAULT 'ADMIN',
    date_nomination DATE DEFAULT CURRENT_DATE,
    permissions VARCHAR(200),
    peut_gerer_utilisateurs BOOLEAN DEFAULT TRUE,
    peut_gerer_finances BOOLEAN DEFAULT FALSE,
    peut_gerer_planning BOOLEAN DEFAULT TRUE,
    peut_voir_rapports BOOLEAN DEFAULT TRUE,
    peut_modifier_systeme BOOLEAN DEFAULT FALSE,
    notes TEXT,
    derniere_action DATE,
    
    CONSTRAINT chk_niveau_acces CHECK (niveau_acces IN ('SUPER_ADMIN', 'ADMIN', 'MODERATEUR'))
);

-- Table du staff médical (héritage)
CREATE TABLE IF NOT EXISTS staff_medical (
    id BIGINT PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
    specialite VARCHAR(100),
    numero_licence VARCHAR(50) UNIQUE,
    formation VARCHAR(100),
    experience_annees INTEGER,
    date_certification DATE,
    date_expiration_licence DATE,
    type_staff VARCHAR(20),
    peut_prescrire BOOLEAN DEFAULT FALSE,
    peut_diagnostiquer BOOLEAN DEFAULT FALSE,
    disponible_urgence BOOLEAN DEFAULT TRUE,
    numero_urgence VARCHAR(20),
    qualifications TEXT,
    salaire DECIMAL(10,2),
    
    CONSTRAINT chk_type_staff CHECK (type_staff IN ('MEDECIN', 'KINESITHERAPEUTE', 'INFIRMIER', 'NUTRITIONNISTE'))
);

-- Table des responsables financiers (héritage)
CREATE TABLE IF NOT EXISTS responsables_financiers (
    id BIGINT PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
    departement VARCHAR(100),
    niveau_autorisation VARCHAR(50) DEFAULT 'JUNIOR',
    limite_approbation DECIMAL(12,2) DEFAULT 1000.00,
    peut_approuver_budgets BOOLEAN DEFAULT FALSE,
    peut_voir_salaires BOOLEAN DEFAULT FALSE,
    peut_modifier_tarifs BOOLEAN DEFAULT FALSE,
    peut_generer_rapports BOOLEAN DEFAULT TRUE,
    formation VARCHAR(100),
    certification VARCHAR(50),
    experience_annees INTEGER,
    date_nomination DATE DEFAULT CURRENT_DATE,
    salaire DECIMAL(10,2),
    responsabilites TEXT,
    actif_financier BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT chk_niveau_autorisation CHECK (niveau_autorisation IN ('JUNIOR', 'SENIOR', 'DIRECTEUR'))
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_role ON utilisateurs(role);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_actif ON utilisateurs(actif);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_type ON utilisateurs(type_utilisateur);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_date_creation ON utilisateurs(date_creation);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_derniere_connexion ON utilisateurs(derniere_connexion);

CREATE INDEX IF NOT EXISTS idx_joueurs_statut ON joueurs(statut);
CREATE INDEX IF NOT EXISTS idx_joueurs_poste ON joueurs(poste);
CREATE INDEX IF NOT EXISTS idx_joueurs_numero_maillot ON joueurs(numero_maillot);

CREATE INDEX IF NOT EXISTS idx_coaches_type ON coaches(type_coach);
CREATE INDEX IF NOT EXISTS idx_coaches_specialite ON coaches(specialite);

CREATE INDEX IF NOT EXISTS idx_admin_niveau_acces ON administrateurs(niveau_acces);
CREATE INDEX IF NOT EXISTS idx_admin_departement ON administrateurs(departement);

CREATE INDEX IF NOT EXISTS idx_staff_type ON staff_medical(type_staff);
CREATE INDEX IF NOT EXISTS idx_staff_licence ON staff_medical(numero_licence);

CREATE INDEX IF NOT EXISTS idx_financier_niveau ON responsables_financiers(niveau_autorisation);
CREATE INDEX IF NOT EXISTS idx_financier_departement ON responsables_financiers(departement);

-- Compte administrateur par défaut (seul compte initial)
-- Mot de passe par défaut : "admin123" (hashé avec BCrypt)
INSERT INTO utilisateurs (type_utilisateur, nom, prenom, email, mot_de_passe, role, actif) VALUES
('ADMINISTRATEUR', 'Administrateur', 'COK', 'admin@cok.tn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMINISTRATEUR', TRUE);

-- Configuration du compte administrateur avec tous les privilèges
INSERT INTO administrateurs (id, niveau_acces, peut_gerer_utilisateurs, peut_gerer_finances, peut_gerer_planning, peut_voir_rapports, peut_modifier_systeme, departement, permissions)
SELECT id, 'SUPER_ADMIN', TRUE, TRUE, TRUE, TRUE, TRUE, 'Administration Générale', 'ALL_PERMISSIONS' FROM utilisateurs WHERE email = 'admin@cok.tn';

-- Fonction pour nettoyer les données de test (optionnel)
CREATE OR REPLACE FUNCTION clean_test_data() RETURNS VOID AS $$
BEGIN
    DELETE FROM utilisateurs WHERE email IN ('admin@sprintbot.com', 'coach@sprintbot.com', 'joueur@sprintbot.com');
END;
$$ LANGUAGE plpgsql;

-- Vue pour les statistiques utilisateurs
CREATE OR REPLACE VIEW v_user_statistics AS
SELECT 
    role,
    COUNT(*) as total,
    COUNT(CASE WHEN actif = TRUE THEN 1 END) as actifs,
    COUNT(CASE WHEN actif = FALSE THEN 1 END) as inactifs,
    COUNT(CASE WHEN derniere_connexion >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as connectes_7j
FROM utilisateurs 
GROUP BY role;

-- Vue pour les joueurs avec informations complètes
CREATE OR REPLACE VIEW v_joueurs_complets AS
SELECT 
    u.id, u.nom, u.prenom, u.email, u.telephone, u.actif,
    j.taille, j.poids, j.poste, j.statut, j.numero_maillot, j.niveau
FROM utilisateurs u
JOIN joueurs j ON u.id = j.id
WHERE u.type_utilisateur = 'JOUEUR';

-- Vue pour les coaches avec informations complètes
CREATE OR REPLACE VIEW v_coaches_complets AS
SELECT 
    u.id, u.nom, u.prenom, u.email, u.telephone, u.actif,
    c.specialite, c.type_coach, c.experience, c.actif_coaching
FROM utilisateurs u
JOIN coaches c ON u.id = c.id
WHERE u.type_utilisateur = 'COACH';

-- Triggers pour maintenir la cohérence des données
CREATE OR REPLACE FUNCTION update_derniere_connexion() RETURNS TRIGGER AS $$
BEGIN
    NEW.derniere_connexion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Commentaires sur les tables
COMMENT ON TABLE utilisateurs IS 'Table principale des utilisateurs avec héritage JPA';
COMMENT ON TABLE joueurs IS 'Table des joueurs avec propriétés spécifiques au volley-ball';
COMMENT ON TABLE coaches IS 'Table des entraîneurs avec leurs spécialisations';
COMMENT ON TABLE administrateurs IS 'Table des administrateurs avec leurs permissions';
COMMENT ON TABLE staff_medical IS 'Table du personnel médical avec licences et qualifications';
COMMENT ON TABLE responsables_financiers IS 'Table des responsables financiers avec autorisations';

-- Attribution finale des privilèges
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO auth_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO auth_user;
