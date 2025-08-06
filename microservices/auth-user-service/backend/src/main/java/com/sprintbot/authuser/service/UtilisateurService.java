package com.sprintbot.authuser.service;

import com.sprintbot.authuser.entity.*;
import com.sprintbot.authuser.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs
 * Gère les opérations CRUD et la logique métier des utilisateurs
 */
@Service
@Transactional
public class UtilisateurService {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Trouve tous les utilisateurs
     * @return liste de tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        logger.info("Récupération de tous les utilisateurs");
        return utilisateurRepository.findAll();
    }

    /**
     * Trouve un utilisateur par son ID
     * @param id l'ID de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findById(Long id) {
        logger.info("Recherche de l'utilisateur avec l'ID: {}", id);
        return utilisateurRepository.findById(id);
    }

    /**
     * Trouve un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        logger.info("Recherche de l'utilisateur avec l'email: {}", email);
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Trouve tous les utilisateurs par rôle
     * @param role le rôle recherché
     * @return liste des utilisateurs avec ce rôle
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> findByRole(String role) {
        logger.info("Recherche des utilisateurs avec le rôle: {}", role);
        return utilisateurRepository.findByRole(role);
    }

    /**
     * Trouve tous les utilisateurs actifs
     * @return liste des utilisateurs actifs
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> findActiveUsers() {
        logger.info("Récupération des utilisateurs actifs");
        return utilisateurRepository.findActiveUsers();
    }

    /**
     * Recherche des utilisateurs par terme
     * @param searchTerm le terme de recherche
     * @return liste des utilisateurs correspondants
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> searchUsers(String searchTerm) {
        logger.info("Recherche d'utilisateurs avec le terme: {}", searchTerm);
        return utilisateurRepository.searchUsers(searchTerm);
    }

    /**
     * Crée un nouveau joueur
     * @param nom le nom
     * @param prenom le prénom
     * @param email l'email
     * @param motDePasse le mot de passe
     * @return le joueur créé
     */
    public Joueur createJoueur(String nom, String prenom, String email, String motDePasse) {
        logger.info("Création d'un nouveau joueur: {} {}", prenom, nom);
        
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        
        Joueur joueur = new Joueur(nom, prenom, email, passwordEncoder.encode(motDePasse));
        return utilisateurRepository.save(joueur);
    }

    /**
     * Crée un nouveau coach
     * @param nom le nom
     * @param prenom le prénom
     * @param email l'email
     * @param motDePasse le mot de passe
     * @param specialite la spécialité du coach
     * @return le coach créé
     */
    public Coach createCoach(String nom, String prenom, String email, String motDePasse, String specialite) {
        logger.info("Création d'un nouveau coach: {} {}", prenom, nom);
        
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        
        Coach coach = new Coach(nom, prenom, email, passwordEncoder.encode(motDePasse), specialite);
        return utilisateurRepository.save(coach);
    }

    /**
     * Crée un nouveau administrateur
     * @param nom le nom
     * @param prenom le prénom
     * @param email l'email
     * @param motDePasse le mot de passe
     * @return l'administrateur créé
     */
    public Administrateur createAdministrateur(String nom, String prenom, String email, String motDePasse) {
        logger.info("Création d'un nouveau administrateur: {} {}", prenom, nom);
        
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        
        Administrateur admin = new Administrateur(nom, prenom, email, passwordEncoder.encode(motDePasse));
        return utilisateurRepository.save(admin);
    }

    /**
     * Crée un nouveau membre du staff médical
     * @param nom le nom
     * @param prenom le prénom
     * @param email l'email
     * @param motDePasse le mot de passe
     * @param specialite la spécialité médicale
     * @return le membre du staff médical créé
     */
    public StaffMedical createStaffMedical(String nom, String prenom, String email, String motDePasse, String specialite) {
        logger.info("Création d'un nouveau membre du staff médical: {} {}", prenom, nom);
        
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        
        StaffMedical staff = new StaffMedical(nom, prenom, email, passwordEncoder.encode(motDePasse), specialite);
        return utilisateurRepository.save(staff);
    }

    /**
     * Crée un nouveau responsable financier
     * @param nom le nom
     * @param prenom le prénom
     * @param email l'email
     * @param motDePasse le mot de passe
     * @return le responsable financier créé
     */
    public ResponsableFinancier createResponsableFinancier(String nom, String prenom, String email, String motDePasse) {
        logger.info("Création d'un nouveau responsable financier: {} {}", prenom, nom);

        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        ResponsableFinancier responsable = new ResponsableFinancier(nom, prenom, email, passwordEncoder.encode(motDePasse));
        return utilisateurRepository.save(responsable);
    }

    /**
     * Met à jour un utilisateur
     * @param id l'ID de l'utilisateur
     * @param nom le nouveau nom
     * @param prenom le nouveau prénom
     * @param email le nouvel email
     * @param telephone le nouveau téléphone
     * @return l'utilisateur mis à jour
     */
    public Utilisateur updateUtilisateur(Long id, String nom, String prenom, String email, String telephone) {
        logger.info("Mise à jour de l'utilisateur avec l'ID: {}", id);

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // Vérification de l'unicité de l'email si changé
        if (!utilisateur.getEmail().equals(email) && utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        utilisateur.mettreAJourProfil(nom, prenom, email, telephone);
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Change le mot de passe d'un utilisateur
     * @param id l'ID de l'utilisateur
     * @param nouveauMotDePasse le nouveau mot de passe
     * @return l'utilisateur mis à jour
     */
    public Utilisateur changerMotDePasse(Long id, String nouveauMotDePasse) {
        logger.info("Changement de mot de passe pour l'utilisateur avec l'ID: {}", id);

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        utilisateur.changerMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Active un utilisateur
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur activé
     */
    public Utilisateur activerUtilisateur(Long id) {
        logger.info("Activation de l'utilisateur avec l'ID: {}", id);

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        utilisateur.activer();
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Désactive un utilisateur
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur désactivé
     */
    public Utilisateur desactiverUtilisateur(Long id) {
        logger.info("Désactivation de l'utilisateur avec l'ID: {}", id);

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        utilisateur.desactiver();
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprime un utilisateur
     * @param id l'ID de l'utilisateur à supprimer
     */
    public void supprimerUtilisateur(Long id) {
        logger.info("Suppression de l'utilisateur avec l'ID: {}", id);

        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }

        utilisateurRepository.deleteById(id);
    }

    /**
     * Obtient les statistiques des utilisateurs
     * @return Map contenant les statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiques() {
        logger.info("Génération des statistiques des utilisateurs");

        Map<String, Object> stats = new HashMap<>();

        // Statistiques générales
        stats.put("totalUtilisateurs", utilisateurRepository.count());
        stats.put("utilisateursActifs", utilisateurRepository.findActiveUsers().size());
        stats.put("utilisateursInactifs", utilisateurRepository.findInactiveUsers().size());

        // Statistiques par rôle
        Map<String, Long> statsByRole = new HashMap<>();
        List<Object[]> roleStats = utilisateurRepository.getUserStatsByRole();
        for (Object[] stat : roleStats) {
            statsByRole.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("statistiquesParRole", statsByRole);

        // Utilisateurs récents (dernières 24h)
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        stats.put("nouveauxUtilisateurs24h", utilisateurRepository.findUsersCreatedAfter(yesterday).size());
        stats.put("connexionsRecentes24h", utilisateurRepository.findUsersLoggedInAfter(yesterday).size());

        return stats;
    }

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}
