package com.sprintbot.authuser.controller;

import com.sprintbot.authuser.dto.LoginRequest;
import com.sprintbot.authuser.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification
 * Gère les endpoints de connexion, déconnexion et rafraîchissement de tokens
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Endpoint de connexion
     * @param loginRequest les informations de connexion
     * @return ResponseEntity avec les tokens JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());
        
        try {
            Map<String, Object> authResponse = authService.authenticate(
                loginRequest.getEmail(), 
                loginRequest.getMotDePasse()
            );
            
            logger.info("Connexion réussie pour l'email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion pour {}: {}", loginRequest.getEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur d'authentification");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint de rafraîchissement de token
     * @param refreshTokenRequest contient le refresh token
     * @return ResponseEntity avec le nouveau token d'accès
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        logger.info("Tentative de rafraîchissement de token");
        
        try {
            String refreshToken = refreshTokenRequest.get("refreshToken");
            
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("Refresh token manquant");
            }
            
            Map<String, Object> authResponse = authService.refreshToken(refreshToken);
            
            logger.info("Token rafraîchi avec succès");
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            logger.error("Erreur lors du rafraîchissement du token: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur de rafraîchissement");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint de déconnexion
     * @param request contient l'email de l'utilisateur
     * @return ResponseEntity de confirmation
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email != null && !email.trim().isEmpty()) {
                authService.logout(email);
                logger.info("Déconnexion réussie pour l'email: {}", email);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Déconnexion réussie");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la déconnexion: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur de déconnexion");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint pour valider un token
     * @param request contient le token à valider
     * @return ResponseEntity avec le statut de validation
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            
            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("Token manquant");
            }
            
            boolean isValid = authService.validateAccessToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("timestamp", System.currentTimeMillis());
            
            if (isValid) {
                // Ajouter les informations utilisateur si le token est valide
                Map<String, Object> userInfo = authService.getUserFromToken(token);
                response.put("user", userInfo);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la validation du token: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", "Token invalide");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint pour obtenir les informations de l'utilisateur connecté
     * @param authorizationHeader le header Authorization avec le token
     * @return ResponseEntity avec les informations utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extraction du token du header Authorization
            String token = authorizationHeader.replace("Bearer ", "");
            
            if (!authService.validateAccessToken(token)) {
                throw new RuntimeException("Token invalide ou expiré");
            }
            
            Map<String, Object> userInfo = authService.getUserFromToken(token);
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des informations utilisateur: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Accès non autorisé");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint de santé pour vérifier le statut du service d'authentification
     * @return ResponseEntity avec le statut du service
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auth-user-service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
}
