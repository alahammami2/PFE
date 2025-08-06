package com.sprintbot.authuser.service;

import com.sprintbot.authuser.config.JwtConfig;
import com.sprintbot.authuser.entity.Utilisateur;
import com.sprintbot.authuser.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service d'authentification pour la gestion des connexions utilisateur
 * Gère l'authentification, la génération de tokens JWT et la sécurité
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Authentifie un utilisateur avec email et mot de passe
     * @param email l'email de l'utilisateur
     * @param motDePasse le mot de passe en clair
     * @return Map contenant les tokens et informations utilisateur
     */
    public Map<String, Object> authenticate(String email, String motDePasse) {
        logger.info("Tentative d'authentification pour l'email: {}", email);
        
        try {
            // Recherche de l'utilisateur par email
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
            
            if (utilisateurOpt.isEmpty()) {
                logger.warn("Utilisateur non trouvé pour l'email: {}", email);
                throw new RuntimeException("Email ou mot de passe incorrect");
            }
            
            Utilisateur utilisateur = utilisateurOpt.get();
            
            // Vérification que l'utilisateur est actif
            if (!utilisateur.isActif()) {
                logger.warn("Tentative de connexion d'un utilisateur inactif: {}", email);
                throw new RuntimeException("Compte utilisateur désactivé");
            }
            
            // Vérification du mot de passe
            if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
                logger.warn("Mot de passe incorrect pour l'email: {}", email);
                throw new RuntimeException("Email ou mot de passe incorrect");
            }
            
            // Mise à jour de la dernière connexion
            utilisateur.login();
            utilisateurRepository.save(utilisateur);
            
            // Génération des tokens
            String accessToken = jwtConfig.generateToken(
                utilisateur.getEmail(), 
                utilisateur.getRole(), 
                utilisateur.getId()
            );
            
            String refreshToken = jwtConfig.generateRefreshToken(
                utilisateur.getEmail(), 
                utilisateur.getId()
            );
            
            logger.info("Authentification réussie pour l'utilisateur: {}", email);
            
            // Préparation de la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtConfig.getExpiration() / 1000); // en secondes
            response.put("user", createUserInfo(utilisateur));
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification pour {}: {}", email, e.getMessage());
            throw new RuntimeException("Erreur d'authentification: " + e.getMessage());
        }
    }

    /**
     * Rafraîchit un token d'accès à partir d'un refresh token
     * @param refreshToken le refresh token
     * @return Map contenant le nouveau token d'accès
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        logger.info("Tentative de rafraîchissement de token");
        
        try {
            // Validation du refresh token
            if (!jwtConfig.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Token de rafraîchissement invalide");
            }
            
            String email = jwtConfig.extractEmail(refreshToken);
            Long userId = jwtConfig.extractUserId(refreshToken);
            
            // Vérification de l'expiration
            if (jwtConfig.isTokenExpired(refreshToken)) {
                throw new RuntimeException("Token de rafraîchissement expiré");
            }
            
            // Recherche de l'utilisateur
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
            if (utilisateurOpt.isEmpty() || !utilisateurOpt.get().getId().equals(userId)) {
                throw new RuntimeException("Utilisateur non trouvé ou token invalide");
            }
            
            Utilisateur utilisateur = utilisateurOpt.get();
            
            // Vérification que l'utilisateur est toujours actif
            if (!utilisateur.isActif()) {
                throw new RuntimeException("Compte utilisateur désactivé");
            }
            
            // Génération d'un nouveau token d'accès
            String newAccessToken = jwtConfig.generateToken(
                utilisateur.getEmail(), 
                utilisateur.getRole(), 
                utilisateur.getId()
            );
            
            logger.info("Token rafraîchi avec succès pour l'utilisateur: {}", email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtConfig.getExpiration() / 1000);
            response.put("user", createUserInfo(utilisateur));
            
            return response;
            
        } catch (Exception e) {
            logger.error("Erreur lors du rafraîchissement du token: {}", e.getMessage());
            throw new RuntimeException("Erreur de rafraîchissement: " + e.getMessage());
        }
    }

    /**
     * Valide un token d'accès
     * @param token le token à valider
     * @return true si le token est valide
     */
    public boolean validateAccessToken(String token) {
        try {
            if (!jwtConfig.isAccessToken(token)) {
                return false;
            }
            
            String email = jwtConfig.extractEmail(token);
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
            
            if (utilisateurOpt.isEmpty()) {
                return false;
            }
            
            Utilisateur utilisateur = utilisateurOpt.get();
            return utilisateur.isActif() && jwtConfig.validateToken(token, email);
            
        } catch (Exception e) {
            logger.warn("Erreur lors de la validation du token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrait les informations utilisateur d'un token
     * @param token le token JWT
     * @return Map contenant les informations utilisateur
     */
    public Map<String, Object> getUserFromToken(String token) {
        try {
            String email = jwtConfig.extractEmail(token);
            Long userId = jwtConfig.extractUserId(token);
            String role = jwtConfig.extractRole(token);
            
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
            if (utilisateurOpt.isEmpty()) {
                throw new RuntimeException("Utilisateur non trouvé");
            }
            
            return createUserInfo(utilisateurOpt.get());
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction des informations utilisateur: {}", e.getMessage());
            throw new RuntimeException("Token invalide");
        }
    }

    /**
     * Crée un objet contenant les informations utilisateur (sans mot de passe)
     * @param utilisateur l'utilisateur
     * @return Map contenant les informations sécurisées
     */
    private Map<String, Object> createUserInfo(Utilisateur utilisateur) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", utilisateur.getId());
        userInfo.put("nom", utilisateur.getNom());
        userInfo.put("prenom", utilisateur.getPrenom());
        userInfo.put("email", utilisateur.getEmail());
        userInfo.put("role", utilisateur.getRole());
        userInfo.put("telephone", utilisateur.getTelephone());
        userInfo.put("actif", utilisateur.getActif());
        userInfo.put("dateCreation", utilisateur.getDateCreation());
        userInfo.put("derniereConnexion", utilisateur.getDerniereConnexion());
        userInfo.put("avatarUrl", utilisateur.getAvatarUrl());
        userInfo.put("nomComplet", utilisateur.getNomComplet());
        
        return userInfo;
    }

    /**
     * Déconnecte un utilisateur (côté serveur, pour logging)
     * @param email l'email de l'utilisateur
     */
    public void logout(String email) {
        logger.info("Déconnexion de l'utilisateur: {}", email);
        // Note: Avec JWT, la déconnexion est principalement côté client
        // Ici on peut ajouter une logique de blacklist des tokens si nécessaire
    }
}
