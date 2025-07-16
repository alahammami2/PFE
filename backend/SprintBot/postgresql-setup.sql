-- Script de configuration PostgreSQL pour SprintBot
-- Exécuter ce script en tant qu'administrateur PostgreSQL dans pgAdmin 4

-- ========================================
-- ÉTAPE 1: Créer la base de données
-- ========================================
-- Exécuter d'abord cette partie dans la base "postgres" par défaut

CREATE DATABASE sprintbot_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Créer l'utilisateur
CREATE USER sprintbot_user WITH PASSWORD 'sprintbot_password';

-- Accorder tous les privilèges sur la base de données
GRANT ALL PRIVILEGES ON DATABASE sprintbot_db TO sprintbot_user;

-- ========================================
-- ÉTAPE 2: Configurer les privilèges
-- ========================================
-- Après avoir créé la base, se connecter à "sprintbot_db" et exécuter :

-- Se connecter à la base sprintbot_db
\c sprintbot_db;

-- Accorder les privilèges sur le schéma public
GRANT ALL ON SCHEMA public TO sprintbot_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sprintbot_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sprintbot_user;

-- Définir les privilèges par défaut pour les futures tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO sprintbot_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO sprintbot_user;

-- Accorder les privilèges de création
GRANT CREATE ON SCHEMA public TO sprintbot_user;

-- ========================================
-- VÉRIFICATION
-- ========================================
-- Vérifier que l'utilisateur a bien les droits
SELECT 
    grantee, 
    privilege_type 
FROM information_schema.role_table_grants 
WHERE grantee = 'sprintbot_user';

-- Afficher les bases de données
\l

-- Afficher les utilisateurs
\du
