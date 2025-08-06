package com.sprintbot.authuser.controller;

import com.sprintbot.authuser.dto.CreateUserRequest;
import com.sprintbot.authuser.dto.UpdateProfilRequest;
import com.sprintbot.authuser.entity.*;
import com.sprintbot.authuser.service.UtilisateurService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des utilisateurs
 * Gère les opérations CRUD sur les utilisateurs
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class UtilisateurController {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurController.class);

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Récupère tous les utilisateurs
     * @return ResponseEntity avec la liste des utilisateurs
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Utilisateur> users = utilisateurService.findAll();
            logger.info("Récupération de {} utilisateurs", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs: {}", e.getMessage());
            return createErrorResponse("Erreur lors de la récupération des utilisateurs", e.getMessage());
        }
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id l'ID de l'utilisateur
     * @return ResponseEntity avec l'utilisateur
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<Utilisateur> user = utilisateurService.findById(id);
            if (user.isPresent()) {
                logger.info("Utilisateur trouvé avec l'ID: {}", id);
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("Utilisateur non trouvé avec l'ID: {}", id);
                return createErrorResponse("Utilisateur non trouvé", "Aucun utilisateur avec l'ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur {}: {}", id, e.getMessage());
            return createErrorResponse("Erreur lors de la récupération de l'utilisateur", e.getMessage());
        }
    }

    /**
     * Récupère les utilisateurs par rôle
     * @param role le rôle recherché
     * @return ResponseEntity avec la liste des utilisateurs
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<Utilisateur> users = utilisateurService.findByRole(role.toUpperCase());
            logger.info("Récupération de {} utilisateurs avec le rôle: {}", users.size(), role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs par rôle {}: {}", role, e.getMessage());
            return createErrorResponse("Erreur lors de la récupération des utilisateurs", e.getMessage());
        }
    }

    /**
     * Recherche des utilisateurs
     * @param searchTerm le terme de recherche
     * @return ResponseEntity avec la liste des utilisateurs correspondants
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String searchTerm) {
        try {
            List<Utilisateur> users = utilisateurService.searchUsers(searchTerm);
            logger.info("Recherche '{}' a retourné {} utilisateurs", searchTerm, users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche d'utilisateurs avec '{}': {}", searchTerm, e.getMessage());
            return createErrorResponse("Erreur lors de la recherche", e.getMessage());
        }
    }

    /**
     * Récupère les utilisateurs actifs
     * @return ResponseEntity avec la liste des utilisateurs actifs
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveUsers() {
        try {
            List<Utilisateur> users = utilisateurService.findActiveUsers();
            logger.info("Récupération de {} utilisateurs actifs", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs actifs: {}", e.getMessage());
            return createErrorResponse("Erreur lors de la récupération des utilisateurs actifs", e.getMessage());
        }
    }

    /**
     * Crée un nouvel utilisateur
     * @param createRequest les données de création
     * @return ResponseEntity avec l'utilisateur créé
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createRequest) {
        try {
            Utilisateur newUser = null;
            
            switch (createRequest.getRole().toUpperCase()) {
                case "JOUEUR":
                    newUser = utilisateurService.createJoueur(
                        createRequest.getNom(),
                        createRequest.getPrenom(),
                        createRequest.getEmail(),
                        createRequest.getMotDePasse()
                    );
                    // Mise à jour des propriétés spécifiques au joueur
                    if (newUser instanceof Joueur) {
                        Joueur joueur = (Joueur) newUser;
                        if (createRequest.getPoste() != null) joueur.setPoste(createRequest.getPoste());
                        if (createRequest.getTaille() != null) joueur.setTaille(createRequest.getTaille());
                        if (createRequest.getPoids() != null) joueur.setPoids(createRequest.getPoids());
                    }
                    break;
                    
                case "COACH":
                    newUser = utilisateurService.createCoach(
                        createRequest.getNom(),
                        createRequest.getPrenom(),
                        createRequest.getEmail(),
                        createRequest.getMotDePasse(),
                        createRequest.getSpecialite()
                    );
                    break;
                    
                case "ADMINISTRATEUR":
                    newUser = utilisateurService.createAdministrateur(
                        createRequest.getNom(),
                        createRequest.getPrenom(),
                        createRequest.getEmail(),
                        createRequest.getMotDePasse()
                    );
                    // Mise à jour du département
                    if (newUser instanceof Administrateur && createRequest.getDepartement() != null) {
                        ((Administrateur) newUser).setDepartement(createRequest.getDepartement());
                    }
                    break;
                    
                case "STAFF_MEDICAL":
                    newUser = utilisateurService.createStaffMedical(
                        createRequest.getNom(),
                        createRequest.getPrenom(),
                        createRequest.getEmail(),
                        createRequest.getMotDePasse(),
                        createRequest.getSpecialite()
                    );
                    break;
                    
                case "RESPONSABLE_FINANCIER":
                    newUser = utilisateurService.createResponsableFinancier(
                        createRequest.getNom(),
                        createRequest.getPrenom(),
                        createRequest.getEmail(),
                        createRequest.getMotDePasse()
                    );
                    // Mise à jour du département
                    if (newUser instanceof ResponsableFinancier && createRequest.getDepartement() != null) {
                        ((ResponsableFinancier) newUser).setDepartement(createRequest.getDepartement());
                    }
                    break;
                    
                default:
                    throw new RuntimeException("Rôle non supporté: " + createRequest.getRole());
            }
            
            // Mise à jour du téléphone si fourni
            if (createRequest.getTelephone() != null) {
                newUser.setTelephone(createRequest.getTelephone());
            }
            
            logger.info("Nouvel utilisateur créé: {} {} ({})", 
                newUser.getPrenom(), newUser.getNom(), newUser.getRole());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            return createErrorResponse("Erreur lors de la création de l'utilisateur", e.getMessage());
        }
    }

    /**
     * Met à jour un utilisateur
     * @param id l'ID de l'utilisateur
     * @param updateRequest les données de mise à jour
     * @return ResponseEntity avec l'utilisateur mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateProfilRequest updateRequest) {
        try {
            Utilisateur updatedUser = utilisateurService.updateUtilisateur(
                id,
                updateRequest.getNom(),
                updateRequest.getPrenom(),
                updateRequest.getEmail(),
                updateRequest.getTelephone()
            );

            // Mise à jour de l'avatar si fourni
            if (updateRequest.getAvatarUrl() != null) {
                updatedUser.setAvatarUrl(updateRequest.getAvatarUrl());
            }

            logger.info("Utilisateur mis à jour: {} {} (ID: {})",
                updatedUser.getPrenom(), updatedUser.getNom(), id);

            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur {}: {}", id, e.getMessage());
            return createErrorResponse("Erreur lors de la mise à jour de l'utilisateur", e.getMessage());
        }
    }

    /**
     * Change le mot de passe d'un utilisateur
     * @param id l'ID de l'utilisateur
     * @param passwordRequest contient le nouveau mot de passe
     * @return ResponseEntity de confirmation
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordRequest) {
        try {
            String nouveauMotDePasse = passwordRequest.get("nouveauMotDePasse");

            if (nouveauMotDePasse == null || nouveauMotDePasse.trim().isEmpty()) {
                throw new RuntimeException("Le nouveau mot de passe est obligatoire");
            }

            if (nouveauMotDePasse.length() < 8) {
                throw new RuntimeException("Le mot de passe doit contenir au moins 8 caractères");
            }

            utilisateurService.changerMotDePasse(id, nouveauMotDePasse);

            logger.info("Mot de passe changé pour l'utilisateur avec l'ID: {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Mot de passe mis à jour avec succès");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe pour l'utilisateur {}: {}", id, e.getMessage());
            return createErrorResponse("Erreur lors du changement de mot de passe", e.getMessage());
        }
    }

    /**
     * Active un utilisateur
     * @param id l'ID de l'utilisateur
     * @return ResponseEntity avec l'utilisateur activé
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            Utilisateur activatedUser = utilisateurService.activerUtilisateur(id);

            logger.info("Utilisateur activé: {} {} (ID: {})",
                activatedUser.getPrenom(), activatedUser.getNom(), id);

            return ResponseEntity.ok(activatedUser);

        } catch (Exception e) {
            logger.error("Erreur lors de l'activation de l'utilisateur {}: {}", id, e.getMessage());
            return createErrorResponse("Erreur lors de l'activation de l'utilisateur", e.getMessage());
        }
    }

    /**
     * Désactive un utilisateur
     * @param id l'ID de l'utilisateur
     * @return ResponseEntity avec l'utilisateur désactivé
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            Utilisateur deactivatedUser = utilisateurService.desactiverUtilisateur(id);

            logger.info("Utilisateur désactivé: {} {} (ID: {})",
                deactivatedUser.getPrenom(), deactivatedUser.getNom(), id);

            return ResponseEntity.ok(deactivatedUser);

        } catch (Exception e) {
            logger.error("Erreur lors de la désactivation de l'utilisateur {}: {}", id, e.getMessage());
            return createErrorResponse("Erreur lors de la désactivation de l'utilisateur", e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur
     * @param id l'ID de l'utilisateur
     * @return ResponseEntity de confirmation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            utilisateurService.supprimerUtilisateur(id);

            logger.info("Utilisateur supprimé avec l'ID: {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur supprimé avec succès");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur {}: {}", id, e.getMessage());
            return createErrorResponse("Erreur lors de la suppression de l'utilisateur", e.getMessage());
        }
    }

    /**
     * Récupère les statistiques des utilisateurs
     * @return ResponseEntity avec les statistiques
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            Map<String, Object> statistics = utilisateurService.getStatistiques();
            logger.info("Statistiques des utilisateurs récupérées");
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return createErrorResponse("Erreur lors de la récupération des statistiques", e.getMessage());
        }
    }

    /**
     * Vérifie si un email existe
     * @param email l'email à vérifier
     * @return ResponseEntity avec le résultat de la vérification
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = utilisateurService.emailExists(email);

            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("exists", exists);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de l'email {}: {}", email, e.getMessage());
            return createErrorResponse("Erreur lors de la vérification de l'email", e.getMessage());
        }
    }

    /**
     * Crée une réponse d'erreur standardisée
     * @param error le message d'erreur principal
     * @param message le message détaillé
     * @return ResponseEntity avec l'erreur
     */
    private ResponseEntity<?> createErrorResponse(String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
