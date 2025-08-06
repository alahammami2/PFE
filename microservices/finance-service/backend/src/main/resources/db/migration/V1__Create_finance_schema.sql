-- Migration V1: Création du schéma finance et des tables principales
-- Auteur: SprintBot Finance Service
-- Date: 2024-01-15

-- Création du schéma finance
CREATE SCHEMA IF NOT EXISTS finance;

-- Utilisation du schéma finance
SET search_path TO finance;

-- Table des catégories de budget
CREATE TABLE categorie_budget (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    couleur VARCHAR(7), -- Code couleur hexadécimal
    icone VARCHAR(50),
    actif BOOLEAN NOT NULL DEFAULT true,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des budgets
CREATE TABLE budget (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    montant_total DECIMAL(15,2) NOT NULL CHECK (montant_total >= 0),
    montant_utilise DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (montant_utilise >= 0),
    montant_restant DECIMAL(15,2) NOT NULL CHECK (montant_restant >= 0),
    seuil_alerte DECIMAL(5,2) NOT NULL DEFAULT 80 CHECK (seuil_alerte >= 0 AND seuil_alerte <= 100),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'CLOTURE', 'SUSPENDU')),
    periode_budget VARCHAR(20) NOT NULL CHECK (periode_budget IN ('MENSUEL', 'TRIMESTRIEL', 'SEMESTRIEL', 'ANNUEL', 'PONCTUEL')),
    auto_renouvellement BOOLEAN NOT NULL DEFAULT false,
    categorie_budget_id BIGINT REFERENCES categorie_budget(id),
    utilisateur_createur_id BIGINT NOT NULL,
    commentaires TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_budget_dates CHECK (date_fin >= date_debut),
    CONSTRAINT chk_budget_montants CHECK (montant_total = montant_utilise + montant_restant)
);

-- Table des catégories de transaction
CREATE TABLE categorie_transaction (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    type_transaction VARCHAR(20) NOT NULL CHECK (type_transaction IN ('RECETTE', 'DEPENSE')),
    couleur VARCHAR(7), -- Code couleur hexadécimal
    icone VARCHAR(50),
    actif BOOLEAN NOT NULL DEFAULT true,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des transactions
CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    montant DECIMAL(15,2) NOT NULL CHECK (montant > 0),
    montant_ttc DECIMAL(15,2),
    taux_tva DECIMAL(5,2) DEFAULT 0 CHECK (taux_tva >= 0 AND taux_tva <= 100),
    date_transaction DATE NOT NULL,
    type_transaction VARCHAR(20) NOT NULL CHECK (type_transaction IN ('RECETTE', 'DEPENSE')),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'VALIDEE', 'REJETEE', 'ANNULEE')),
    mode_paiement VARCHAR(50),
    reference_externe VARCHAR(100),
    budget_id BIGINT REFERENCES budget(id),
    categorie_transaction_id BIGINT REFERENCES categorie_transaction(id),
    utilisateur_id BIGINT NOT NULL,
    validateur_id BIGINT,
    date_validation TIMESTAMP,
    motif_rejet TEXT,
    pieces_jointes TEXT[], -- Array de chemins vers les fichiers
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des sponsors
CREATE TABLE sponsor (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    contact_nom VARCHAR(100),
    contact_email VARCHAR(100),
    contact_telephone VARCHAR(20),
    adresse TEXT,
    site_web VARCHAR(200),
    type_partenariat VARCHAR(30) NOT NULL CHECK (type_partenariat IN ('PRINCIPAL', 'OFFICIEL', 'TECHNIQUE', 'MEDIA', 'INSTITUTIONNEL')),
    montant_contrat DECIMAL(15,2) NOT NULL CHECK (montant_contrat >= 0),
    montant_verse DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (montant_verse >= 0),
    montant_restant DECIMAL(15,2) NOT NULL CHECK (montant_restant >= 0),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'EXPIRE', 'SUSPENDU', 'RESILIE')),
    auto_renouvellement BOOLEAN NOT NULL DEFAULT false,
    conditions_renouvellement TEXT,
    logo_url VARCHAR(500),
    description_partenariat TEXT,
    avantages TEXT,
    obligations TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_sponsor_dates CHECK (date_fin >= date_debut),
    CONSTRAINT chk_sponsor_montants CHECK (montant_contrat = montant_verse + montant_restant)
);

-- Table des contrats de sponsoring
CREATE TABLE contrat_sponsoring (
    id BIGSERIAL PRIMARY KEY,
    sponsor_id BIGINT NOT NULL REFERENCES sponsor(id) ON DELETE CASCADE,
    numero_contrat VARCHAR(50) NOT NULL UNIQUE,
    date_signature DATE NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    montant DECIMAL(15,2) NOT NULL CHECK (montant >= 0),
    devise VARCHAR(3) NOT NULL DEFAULT 'EUR',
    conditions_paiement TEXT,
    clauses_particulieres TEXT,
    document_url VARCHAR(500),
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'EXPIRE', 'RESILIE')),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_contrat_dates CHECK (date_fin >= date_debut AND date_signature <= date_debut)
);

-- Table des paiements de sponsors
CREATE TABLE paiement_sponsor (
    id BIGSERIAL PRIMARY KEY,
    sponsor_id BIGINT NOT NULL REFERENCES sponsor(id) ON DELETE CASCADE,
    montant DECIMAL(15,2) NOT NULL CHECK (montant > 0),
    date_paiement DATE NOT NULL,
    date_prevue DATE,
    mode_paiement VARCHAR(50) NOT NULL,
    reference_paiement VARCHAR(100),
    statut VARCHAR(20) NOT NULL DEFAULT 'RECU' CHECK (statut IN ('ATTENDU', 'RECU', 'EN_RETARD', 'ANNULE')),
    commentaires TEXT,
    document_url VARCHAR(500),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des salaires
CREATE TABLE salaire (
    id BIGSERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL,
    periode DATE NOT NULL, -- Premier jour du mois de paie
    salaire_brut DECIMAL(15,2) NOT NULL CHECK (salaire_brut >= 0),
    primes DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (primes >= 0),
    bonus DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (bonus >= 0),
    deductions DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (deductions >= 0),
    heures_supplementaires INTEGER NOT NULL DEFAULT 0 CHECK (heures_supplementaires >= 0),
    jours_absence INTEGER NOT NULL DEFAULT 0 CHECK (jours_absence >= 0),
    cotisations_sociales DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (cotisations_sociales >= 0),
    impot DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (impot >= 0),
    salaire_net DECIMAL(15,2) NOT NULL CHECK (salaire_net >= 0),
    statut VARCHAR(20) NOT NULL DEFAULT 'CALCULE' CHECK (statut IN ('CALCULE', 'VALIDE', 'PAYE', 'ANNULE')),
    date_validation TIMESTAMP,
    validateur_id BIGINT,
    date_paiement TIMESTAMP,
    mode_paiement VARCHAR(50),
    reference_paiement VARCHAR(100),
    commentaires TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_salaire_employe_periode UNIQUE (employe_id, periode)
);

-- Table des éléments de salaire
CREATE TABLE element_salaire (
    id BIGSERIAL PRIMARY KEY,
    salaire_id BIGINT NOT NULL REFERENCES salaire(id) ON DELETE CASCADE,
    libelle VARCHAR(100) NOT NULL,
    type_element VARCHAR(20) NOT NULL CHECK (type_element IN ('GAIN', 'RETENUE', 'COTISATION', 'INFORMATION')),
    montant DECIMAL(15,2) NOT NULL DEFAULT 0,
    quantite DECIMAL(10,2),
    taux DECIMAL(5,2),
    obligatoire BOOLEAN NOT NULL DEFAULT false,
    imposable BOOLEAN NOT NULL DEFAULT true,
    cotisable BOOLEAN NOT NULL DEFAULT true,
    ordre_affichage INTEGER NOT NULL DEFAULT 1,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index pour optimiser les performances
CREATE INDEX idx_budget_statut ON budget(statut);
CREATE INDEX idx_budget_dates ON budget(date_debut, date_fin);
CREATE INDEX idx_budget_categorie ON budget(categorie_budget_id);
CREATE INDEX idx_budget_utilisateur ON budget(utilisateur_createur_id);

CREATE INDEX idx_transaction_statut ON transaction(statut);
CREATE INDEX idx_transaction_type ON transaction(type_transaction);
CREATE INDEX idx_transaction_date ON transaction(date_transaction);
CREATE INDEX idx_transaction_reference ON transaction(reference);
CREATE INDEX idx_transaction_budget ON transaction(budget_id);
CREATE INDEX idx_transaction_categorie ON transaction(categorie_transaction_id);
CREATE INDEX idx_transaction_utilisateur ON transaction(utilisateur_id);

CREATE INDEX idx_sponsor_statut ON sponsor(statut);
CREATE INDEX idx_sponsor_type ON sponsor(type_partenariat);
CREATE INDEX idx_sponsor_dates ON sponsor(date_debut, date_fin);

CREATE INDEX idx_paiement_sponsor_date ON paiement_sponsor(date_paiement);
CREATE INDEX idx_paiement_sponsor_statut ON paiement_sponsor(statut);
CREATE INDEX idx_paiement_sponsor_sponsor ON paiement_sponsor(sponsor_id);

CREATE INDEX idx_salaire_employe ON salaire(employe_id);
CREATE INDEX idx_salaire_periode ON salaire(periode);
CREATE INDEX idx_salaire_statut ON salaire(statut);
CREATE INDEX idx_salaire_employe_periode ON salaire(employe_id, periode);

CREATE INDEX idx_element_salaire_salaire ON element_salaire(salaire_id);
CREATE INDEX idx_element_salaire_type ON element_salaire(type_element);

-- Triggers pour mettre à jour automatiquement date_modification
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.date_modification = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_categorie_budget_modtime BEFORE UPDATE ON categorie_budget FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_budget_modtime BEFORE UPDATE ON budget FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_categorie_transaction_modtime BEFORE UPDATE ON categorie_transaction FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_transaction_modtime BEFORE UPDATE ON transaction FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_sponsor_modtime BEFORE UPDATE ON sponsor FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_contrat_sponsoring_modtime BEFORE UPDATE ON contrat_sponsoring FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_paiement_sponsor_modtime BEFORE UPDATE ON paiement_sponsor FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_salaire_modtime BEFORE UPDATE ON salaire FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_element_salaire_modtime BEFORE UPDATE ON element_salaire FOR EACH ROW EXECUTE FUNCTION update_modified_column();

-- Commentaires sur les tables
COMMENT ON SCHEMA finance IS 'Schéma pour la gestion financière de SprintBot';
COMMENT ON TABLE budget IS 'Table des budgets avec suivi des montants et alertes';
COMMENT ON TABLE transaction IS 'Table des transactions financières avec workflow de validation';
COMMENT ON TABLE sponsor IS 'Table des sponsors et partenaires avec contrats';
COMMENT ON TABLE salaire IS 'Table des salaires avec calculs automatiques';
COMMENT ON TABLE element_salaire IS 'Détail des éléments composant un salaire';
