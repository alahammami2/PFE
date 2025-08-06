-- Migration V2: Insertion des données initiales
-- Auteur: SprintBot Finance Service
-- Date: 2024-01-15

-- Utilisation du schéma finance
SET search_path TO finance;

-- Insertion des catégories de budget par défaut
INSERT INTO categorie_budget (nom, description, couleur, icone) VALUES
('Équipement Sportif', 'Matériel et équipements pour l''équipe de volleyball', '#FF6B35', 'sports_volleyball'),
('Déplacements', 'Frais de transport et hébergement pour les matchs', '#4ECDC4', 'directions_bus'),
('Formation', 'Stages, formations et développement des compétences', '#45B7D1', 'school'),
('Infrastructure', 'Maintenance et amélioration des installations', '#96CEB4', 'home_repair_service'),
('Communication', 'Marketing, publicité et communication', '#FFEAA7', 'campaign'),
('Administration', 'Frais administratifs et de gestion', '#DDA0DD', 'admin_panel_settings'),
('Événements', 'Organisation d''événements et compétitions', '#FFB6C1', 'event'),
('Médical', 'Soins médicaux et prévention des blessures', '#98FB98', 'medical_services'),
('Arbitrage', 'Frais d''arbitrage et officiels', '#F0E68C', 'gavel'),
('Divers', 'Autres dépenses non catégorisées', '#D3D3D3', 'category');

-- Insertion des catégories de transaction par défaut
INSERT INTO categorie_transaction (nom, description, type_transaction, couleur, icone) VALUES
-- Recettes
('Sponsoring', 'Revenus provenant des sponsors et partenaires', 'RECETTE', '#4CAF50', 'handshake'),
('Subventions', 'Aides financières publiques et privées', 'RECETTE', '#8BC34A', 'account_balance'),
('Cotisations', 'Cotisations des membres et joueurs', 'RECETTE', '#CDDC39', 'card_membership'),
('Billetterie', 'Ventes de billets pour les matchs', 'RECETTE', '#FFC107', 'confirmation_number'),
('Merchandising', 'Vente de produits dérivés', 'RECETTE', '#FF9800', 'store'),
('Formations', 'Revenus des stages et formations', 'RECETTE', '#FF5722', 'school'),
('Location', 'Location d''installations ou matériel', 'RECETTE', '#795548', 'home'),
('Dons', 'Dons et contributions bénévoles', 'RECETTE', '#9C27B0', 'volunteer_activism'),
('Autres Recettes', 'Autres sources de revenus', 'RECETTE', '#607D8B', 'attach_money'),

-- Dépenses
('Équipement', 'Achat d''équipements sportifs', 'DEPENSE', '#F44336', 'sports_volleyball'),
('Transport', 'Frais de déplacement', 'DEPENSE', '#E91E63', 'directions_bus'),
('Hébergement', 'Frais d''hébergement et restauration', 'DEPENSE', '#9C27B0', 'hotel'),
('Salaires', 'Rémunération du personnel', 'DEPENSE', '#673AB7', 'payments'),
('Charges Sociales', 'Cotisations sociales et charges patronales', 'DEPENSE', '#3F51B5', 'account_balance_wallet'),
('Assurances', 'Assurances diverses', 'DEPENSE', '#2196F3', 'security'),
('Maintenance', 'Entretien et réparations', 'DEPENSE', '#03A9F4', 'build'),
('Communication', 'Frais de communication et marketing', 'DEPENSE', '#00BCD4', 'campaign'),
('Formation Staff', 'Formation du personnel et encadrement', 'DEPENSE', '#009688', 'school'),
('Médical', 'Frais médicaux et de santé', 'DEPENSE', '#4CAF50', 'medical_services'),
('Arbitrage', 'Frais d''arbitrage', 'DEPENSE', '#8BC34A', 'gavel'),
('Licences', 'Licences et affiliations', 'DEPENSE', '#CDDC39', 'card_membership'),
('Autres Dépenses', 'Autres frais non catégorisés', 'DEPENSE', '#FFC107', 'receipt');

-- Insertion d'un budget exemple pour la saison en cours
INSERT INTO budget (
    nom, 
    description, 
    montant_total, 
    montant_utilise, 
    montant_restant, 
    seuil_alerte,
    date_debut, 
    date_fin, 
    statut, 
    periode_budget, 
    auto_renouvellement,
    categorie_budget_id,
    utilisateur_createur_id,
    commentaires
) VALUES (
    'Budget Saison 2024-2025',
    'Budget principal pour la saison sportive 2024-2025',
    50000.00,
    0.00,
    50000.00,
    85.0,
    '2024-09-01',
    '2025-06-30',
    'ACTIF',
    'ANNUEL',
    false,
    (SELECT id FROM categorie_budget WHERE nom = 'Équipement Sportif'),
    1, -- ID utilisateur admin par défaut
    'Budget initial pour la nouvelle saison'
);

-- Insertion d'un sponsor exemple
INSERT INTO sponsor (
    nom,
    contact_nom,
    contact_email,
    contact_telephone,
    adresse,
    site_web,
    type_partenariat,
    montant_contrat,
    montant_verse,
    montant_restant,
    date_debut,
    date_fin,
    statut,
    auto_renouvellement,
    description_partenariat,
    avantages,
    obligations
) VALUES (
    'SportTech Solutions',
    'Marie Dubois',
    'marie.dubois@sporttech.com',
    '+33 1 23 45 67 89',
    '123 Avenue du Sport, 75001 Paris',
    'https://www.sporttech-solutions.com',
    'PRINCIPAL',
    15000.00,
    5000.00,
    10000.00,
    '2024-09-01',
    '2025-08-31',
    'ACTIF',
    true,
    'Partenariat principal pour l''équipement technique',
    'Équipements sportifs, visibilité sur les maillots, présence événements',
    'Respect des valeurs du club, participation aux événements majeurs'
);

-- Insertion d'un contrat de sponsoring
INSERT INTO contrat_sponsoring (
    sponsor_id,
    numero_contrat,
    date_signature,
    date_debut,
    date_fin,
    montant,
    devise,
    conditions_paiement,
    clauses_particulieres,
    statut
) VALUES (
    (SELECT id FROM sponsor WHERE nom = 'SportTech Solutions'),
    'CONT-2024-001',
    '2024-08-15',
    '2024-09-01',
    '2025-08-31',
    15000.00,
    'EUR',
    'Paiement en 3 fois : 5000€ à la signature, 5000€ en janvier, 5000€ en mai',
    'Clause de renouvellement automatique si performance sportive satisfaisante',
    'ACTIF'
);

-- Insertion d'un paiement de sponsor
INSERT INTO paiement_sponsor (
    sponsor_id,
    montant,
    date_paiement,
    date_prevue,
    mode_paiement,
    reference_paiement,
    statut,
    commentaires
) VALUES (
    (SELECT id FROM sponsor WHERE nom = 'SportTech Solutions'),
    5000.00,
    '2024-08-20',
    '2024-08-15',
    'VIREMENT',
    'VIR-2024-08-001',
    'RECU',
    'Premier versement du contrat de sponsoring principal'
);

-- Insertion d'exemples de transactions
INSERT INTO transaction (
    reference,
    description,
    montant,
    montant_ttc,
    taux_tva,
    date_transaction,
    type_transaction,
    statut,
    mode_paiement,
    reference_externe,
    budget_id,
    categorie_transaction_id,
    utilisateur_id,
    validateur_id,
    date_validation
) VALUES 
(
    'REC-2024-001',
    'Premier versement SportTech Solutions',
    5000.00,
    5000.00,
    0.00,
    '2024-08-20',
    'RECETTE',
    'VALIDEE',
    'VIREMENT',
    'VIR-2024-08-001',
    (SELECT id FROM budget WHERE nom = 'Budget Saison 2024-2025'),
    (SELECT id FROM categorie_transaction WHERE nom = 'Sponsoring'),
    1,
    1,
    '2024-08-20 10:30:00'
),
(
    'DEP-2024-001',
    'Achat ballons de volleyball Mikasa',
    450.00,
    540.00,
    20.00,
    '2024-09-05',
    'DEPENSE',
    'VALIDEE',
    'CARTE_BANCAIRE',
    'CB-2024-09-001',
    (SELECT id FROM budget WHERE nom = 'Budget Saison 2024-2025'),
    (SELECT id FROM categorie_transaction WHERE nom = 'Équipement'),
    1,
    1,
    '2024-09-05 14:15:00'
),
(
    'DEP-2024-002',
    'Transport match à Lyon',
    280.00,
    280.00,
    0.00,
    '2024-09-12',
    'DEPENSE',
    'EN_ATTENTE',
    'VIREMENT',
    NULL,
    (SELECT id FROM budget WHERE nom = 'Budget Saison 2024-2025'),
    (SELECT id FROM categorie_transaction WHERE nom = 'Transport'),
    1,
    NULL,
    NULL
);

-- Insertion d'un exemple de salaire
INSERT INTO salaire (
    employe_id,
    periode,
    salaire_brut,
    primes,
    bonus,
    deductions,
    heures_supplementaires,
    jours_absence,
    cotisations_sociales,
    impot,
    salaire_net,
    statut,
    date_validation,
    validateur_id,
    commentaires
) VALUES (
    101, -- ID employé exemple
    '2024-09-01',
    2500.00,
    200.00,
    0.00,
    0.00,
    5,
    0,
    621.00, -- 23% de cotisations
    405.00, -- 15% d'impôt
    1674.00, -- Salaire net calculé
    'VALIDE',
    '2024-09-25 16:00:00',
    1,
    'Salaire septembre 2024 avec prime de performance'
);

-- Insertion des éléments de salaire détaillés
INSERT INTO element_salaire (
    salaire_id,
    libelle,
    type_element,
    montant,
    quantite,
    taux,
    obligatoire,
    imposable,
    cotisable,
    ordre_affichage
) VALUES 
(
    (SELECT id FROM salaire WHERE employe_id = 101 AND periode = '2024-09-01'),
    'Salaire de base',
    'GAIN',
    2500.00,
    1.00,
    NULL,
    true,
    true,
    true,
    1
),
(
    (SELECT id FROM salaire WHERE employe_id = 101 AND periode = '2024-09-01'),
    'Prime de performance',
    'GAIN',
    200.00,
    1.00,
    NULL,
    false,
    true,
    true,
    2
),
(
    (SELECT id FROM salaire WHERE employe_id = 101 AND periode = '2024-09-01'),
    'Heures supplémentaires',
    'GAIN',
    125.00,
    5.00,
    25.00,
    false,
    true,
    true,
    3
),
(
    (SELECT id FROM salaire WHERE employe_id = 101 AND periode = '2024-09-01'),
    'Cotisations sociales salariales',
    'RETENUE',
    -621.00,
    1.00,
    23.00,
    true,
    false,
    false,
    4
),
(
    (SELECT id FROM salaire WHERE employe_id = 101 AND periode = '2024-09-01'),
    'Impôt sur le revenu',
    'RETENUE',
    -405.00,
    1.00,
    15.00,
    true,
    false,
    false,
    5
);

-- Mise à jour des montants dans le budget après les transactions
UPDATE budget 
SET 
    montant_utilise = 730.00, -- 450 + 280
    montant_restant = 49270.00 -- 50000 - 730
WHERE nom = 'Budget Saison 2024-2025';

-- Mise à jour des montants dans le sponsor après le paiement
UPDATE sponsor 
SET 
    montant_verse = 5000.00,
    montant_restant = 10000.00
WHERE nom = 'SportTech Solutions';

-- Insertion de données de test pour les statistiques
INSERT INTO transaction (reference, description, montant, date_transaction, type_transaction, statut, categorie_transaction_id, utilisateur_id, validateur_id, date_validation)
SELECT 
    'TEST-' || generate_series || '-' || EXTRACT(MONTH FROM date_val),
    'Transaction de test ' || generate_series,
    (random() * 1000 + 100)::DECIMAL(15,2),
    date_val,
    CASE WHEN random() > 0.6 THEN 'RECETTE' ELSE 'DEPENSE' END,
    'VALIDEE',
    (SELECT id FROM categorie_transaction ORDER BY random() LIMIT 1),
    1,
    1,
    date_val + INTERVAL '1 day'
FROM 
    generate_series(1, 20) AS generate_series,
    generate_series('2024-01-01'::date, '2024-12-31'::date, '15 days'::interval) AS date_val
WHERE generate_series <= 20;

-- Commentaires sur les données insérées
COMMENT ON TABLE categorie_budget IS 'Catégories prédéfinies pour organiser les budgets par domaine d''activité';
COMMENT ON TABLE categorie_transaction IS 'Catégories pour classifier les recettes et dépenses';

-- Affichage d'un résumé des données insérées
DO $$
BEGIN
    RAISE NOTICE 'Données initiales insérées avec succès:';
    RAISE NOTICE '- % catégories de budget', (SELECT COUNT(*) FROM categorie_budget);
    RAISE NOTICE '- % catégories de transaction', (SELECT COUNT(*) FROM categorie_transaction);
    RAISE NOTICE '- % budget(s)', (SELECT COUNT(*) FROM budget);
    RAISE NOTICE '- % sponsor(s)', (SELECT COUNT(*) FROM sponsor);
    RAISE NOTICE '- % transaction(s)', (SELECT COUNT(*) FROM transaction);
    RAISE NOTICE '- % salaire(s)', (SELECT COUNT(*) FROM salaire);
    RAISE NOTICE 'Base de données finance initialisée et prête à l''utilisation.';
END $$;
