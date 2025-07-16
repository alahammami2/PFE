-- Script de création de la base de données PostgreSQL pour SprintBot
-- Exécuter ce script en tant qu'administrateur PostgreSQL

-- Créer la base de données
CREATE DATABASE sprintbot_db;

-- Créer l'utilisateur
CREATE USER sprintbot_user WITH PASSWORD 'sprintbot_password';

-- Accorder tous les privilèges sur la base de données
GRANT ALL PRIVILEGES ON DATABASE sprintbot_db TO sprintbot_user;

-- Se connecter à la base de données sprintbot_db et accorder les privilèges sur le schéma
\c sprintbot_db;
GRANT ALL ON SCHEMA public TO sprintbot_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sprintbot_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sprintbot_user;

-- Définir les privilèges par défaut pour les futures tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO sprintbot_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO sprintbot_user;
