package com.sprintbot.authuser.config;

import com.sprintbot.authuser.entity.*;
import com.sprintbot.authuser.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Chargeur de données initial pour le microservice auth-user-service
 * Crée les utilisateurs par défaut au démarrage de l'application
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🔄 Initialisation des données par défaut...");
        
        try {
            createDefaultUsers();
            logger.info("✅ Données par défaut créées avec succès !");
        } catch (Exception e) {
            logger.error("❌ Erreur lors de la création des données par défaut: {}", e.getMessage());
        }
    }

    /**
     * Crée les utilisateurs par défaut s'ils n'existent pas déjà
     */
    private void createDefaultUsers() {
        // Création de l'administrateur par défaut
        createAdminIfNotExists();
        
        // Création du coach par défaut
        createCoachIfNotExists();
        
        // Création du joueur par défaut
        createJoueurIfNotExists();
        
        // Création du staff médical par défaut
        createStaffMedicalIfNotExists();
        
        // Création du responsable financier par défaut
        createResponsableFinancierIfNotExists();
    }

    /**
     * Crée un administrateur par défaut
     */
    private void createAdminIfNotExists() {
        String email = "admin@sprintbot.com";
        
        if (!utilisateurRepository.existsByEmail(email)) {
            Administrateur admin = new Administrateur(
                "Admin", 
                "Super", 
                email, 
                passwordEncoder.encode("admin123")
            );
            
            // Configuration spécifique à l'administrateur
            admin.setDepartement("Administration Générale");
            admin.setNiveauAcces("SUPER_ADMIN");
            admin.setPeutGererUtilisateurs(true);
            admin.setPeutGererFinances(true);
            admin.setPeutGererPlanning(true);
            admin.setPeutVoirRapports(true);
            admin.setPeutModifierSysteme(true);
            admin.setTelephone("+216 20 123 456");
            admin.setNotes("Administrateur principal du système SprintBot");
            
            utilisateurRepository.save(admin);
            logger.info("👨‍💼 Administrateur créé: {} ({})", admin.getNomComplet(), email);
        } else {
            logger.info("👨‍💼 Administrateur existe déjà: {}", email);
        }
    }

    /**
     * Crée un coach par défaut
     */
    private void createCoachIfNotExists() {
        String email = "coach@sprintbot.com";
        
        if (!utilisateurRepository.existsByEmail(email)) {
            Coach coach = new Coach(
                "Dupont", 
                "Jean", 
                email, 
                passwordEncoder.encode("coach123"),
                "Entraînement général et tactique"
            );
            
            // Configuration spécifique au coach
            coach.setTypeCoach("PRINCIPAL");
            coach.setExperience(10);
            coach.setCertification("Niveau 3 FIVB");
            coach.setFormation("Master STAPS - Volley-ball");
            coach.setDateDebutCarriere(LocalDate.of(2014, 9, 1));
            coach.setSalaire(3500.0);
            coach.setTelephone("+216 20 234 567");
            coach.setBiographie("Coach principal avec 10 ans d'expérience en volley-ball professionnel");
            
            utilisateurRepository.save(coach);
            logger.info("🏐 Coach créé: {} ({})", coach.getNomComplet(), email);
        } else {
            logger.info("🏐 Coach existe déjà: {}", email);
        }
    }

    /**
     * Crée un joueur par défaut
     */
    private void createJoueurIfNotExists() {
        String email = "joueur@sprintbot.com";
        
        if (!utilisateurRepository.existsByEmail(email)) {
            Joueur joueur = new Joueur(
                "Martin", 
                "Pierre", 
                email, 
                passwordEncoder.encode("joueur123")
            );
            
            // Configuration spécifique au joueur
            joueur.setTaille(1.85f);
            joueur.setPoids(78.5f);
            joueur.setPoste("Attaquant");
            joueur.setDateNaissance(LocalDate.of(1995, 3, 15));
            joueur.setNumeroMaillot(12);
            joueur.setNationalite("Tunisienne");
            joueur.setExperienceAnnees(8);
            joueur.setNiveau("AVANCE");
            joueur.setMainDominante("DROITE");
            joueur.setTelephone("+216 20 345 678");
            
            utilisateurRepository.save(joueur);
            logger.info("🏐 Joueur créé: {} ({})", joueur.getNomComplet(), email);
        } else {
            logger.info("🏐 Joueur existe déjà: {}", email);
        }
    }

    /**
     * Crée un membre du staff médical par défaut
     */
    private void createStaffMedicalIfNotExists() {
        String email = "medecin@sprintbot.com";
        
        if (!utilisateurRepository.existsByEmail(email)) {
            StaffMedical staff = new StaffMedical(
                "Benali", 
                "Fatma", 
                email, 
                passwordEncoder.encode("medecin123"),
                "Médecine du sport"
            );
            
            // Configuration spécifique au staff médical
            staff.setTypeStaff("MEDECIN");
            staff.setNumeroLicence("MED-2024-001");
            staff.setFormation("Doctorat en Médecine du Sport");
            staff.setExperienceAnnees(12);
            staff.setDateCertification(LocalDate.of(2012, 6, 15));
            staff.setDateExpirationLicence(LocalDate.of(2025, 6, 15));
            staff.setPeutPrescrire(true);
            staff.setPeutDiagnostiquer(true);
            staff.setDisponibleUrgence(true);
            staff.setNumeroUrgence("+216 20 456 789");
            staff.setTelephone("+216 71 123 456");
            staff.setSalaire(4000.0);
            staff.setQualifications("Spécialiste en traumatologie sportive, Diplôme en nutrition sportive");
            
            utilisateurRepository.save(staff);
            logger.info("🏥 Staff médical créé: {} ({})", staff.getNomComplet(), email);
        } else {
            logger.info("🏥 Staff médical existe déjà: {}", email);
        }
    }

    /**
     * Crée un responsable financier par défaut
     */
    private void createResponsableFinancierIfNotExists() {
        String email = "financier@sprintbot.com";
        
        if (!utilisateurRepository.existsByEmail(email)) {
            ResponsableFinancier responsable = new ResponsableFinancier(
                "Trabelsi", 
                "Ahmed", 
                email, 
                passwordEncoder.encode("financier123")
            );
            
            // Configuration spécifique au responsable financier
            responsable.setDepartement("Finances et Comptabilité");
            responsable.setNiveauAutorisation("SENIOR");
            responsable.setLimiteApprobation(15000.0);
            responsable.setPeutApprouverBudgets(true);
            responsable.setPeutVoirSalaires(true);
            responsable.setPeutModifierTarifs(false);
            responsable.setPeutGenererRapports(true);
            responsable.setFormation("Master en Finance");
            responsable.setCertification("Expert-comptable");
            responsable.setExperienceAnnees(15);
            responsable.setSalaire(3800.0);
            responsable.setTelephone("+216 20 567 890");
            responsable.setResponsabilites("Gestion budgétaire, contrôle des dépenses, reporting financier");
            
            utilisateurRepository.save(responsable);
            logger.info("💰 Responsable financier créé: {} ({})", responsable.getNomComplet(), email);
        } else {
            logger.info("💰 Responsable financier existe déjà: {}", email);
        }
    }
}
