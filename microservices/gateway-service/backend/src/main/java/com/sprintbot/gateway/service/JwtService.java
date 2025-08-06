package com.sprintbot.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Service de gestion des tokens JWT
 * 
 * Ce service fournit les fonctionnalités de validation et d'extraction
 * des informations des tokens JWT pour l'authentification centralisée
 * dans le Gateway Service.
 * 
 * Fonctionnalités :
 * - Validation de la signature et de l'expiration des tokens
 * - Extraction des claims (utilisateur, rôles, etc.)
 * - Vérification de l'intégrité des tokens
 * - Support des tokens d'accès et de rafraîchissement
 * 
 * @author SprintBot Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:SprintBot-Gateway-Secret-Key-2024-Very-Long-And-Secure}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-token.expiration:604800000}")
    private Long refreshExpiration;

    /**
     * Extrait le nom d'utilisateur du token JWT
     * 
     * @param token Token JWT
     * @return Nom d'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait les rôles de l'utilisateur du token JWT
     * 
     * @param token Token JWT
     * @return Liste des rôles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    /**
     * Extrait l'ID de l'utilisateur du token JWT
     * 
     * @param token Token JWT
     * @return ID utilisateur
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extrait l'email de l'utilisateur du token JWT
     * 
     * @param token Token JWT
     * @return Email utilisateur
     */
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Extrait le type de token (access ou refresh)
     * 
     * @param token Token JWT
     * @return Type de token
     */
    public String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tokenType", String.class);
    }

    /**
     * Extrait la date d'expiration du token
     * 
     * @param token Token JWT
     * @return Date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du token
     * 
     * @param token Token JWT
     * @param claimsResolver Fonction d'extraction du claim
     * @param <T> Type du claim
     * @return Valeur du claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token
     * 
     * @param token Token JWT
     * @return Claims du token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction des claims du token : {}", e.getMessage());
            throw new RuntimeException("Token JWT invalide", e);
        }
    }

    /**
     * Vérifie si le token est expiré
     * 
     * @param token Token JWT
     * @return true si le token est expiré
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.warn("⚠️ Impossible de vérifier l'expiration du token : {}", e.getMessage());
            return true; // Considérer comme expiré en cas d'erreur
        }
    }

    /**
     * Valide un token JWT
     * 
     * @param token Token JWT à valider
     * @param username Nom d'utilisateur attendu
     * @return true si le token est valide
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isUsernameValid = extractedUsername.equals(username);
            boolean isTokenNotExpired = !isTokenExpired(token);
            boolean isAccessToken = "access".equals(extractTokenType(token));
            
            boolean isValid = isUsernameValid && isTokenNotExpired && isAccessToken;
            
            if (isValid) {
                log.debug("✅ Token JWT valide pour l'utilisateur : {}", username);
            } else {
                log.warn("❌ Token JWT invalide pour l'utilisateur : {} " +
                        "(username: {}, expired: {}, type: {})", 
                        username, isUsernameValid, !isTokenNotExpired, extractTokenType(token));
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la validation du token pour l'utilisateur {} : {}", 
                    username, e.getMessage());
            return false;
        }
    }

    /**
     * Valide un token JWT sans vérifier le nom d'utilisateur
     * 
     * @param token Token JWT à valider
     * @return true si le token est valide
     */
    public Boolean validateToken(String token) {
        try {
            String username = extractUsername(token);
            return validateToken(token, username);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la validation du token : {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un token est un token de rafraîchissement valide
     * 
     * @param token Token JWT
     * @return true si c'est un token de rafraîchissement valide
     */
    public Boolean isValidRefreshToken(String token) {
        try {
            boolean isNotExpired = !isTokenExpired(token);
            boolean isRefreshToken = "refresh".equals(extractTokenType(token));
            
            boolean isValid = isNotExpired && isRefreshToken;
            
            if (isValid) {
                log.debug("✅ Token de rafraîchissement valide");
            } else {
                log.warn("❌ Token de rafraîchissement invalide (expired: {}, type: {})", 
                        !isNotExpired, extractTokenType(token));
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la validation du token de rafraîchissement : {}", 
                    e.getMessage());
            return false;
        }
    }

    /**
     * Extrait le token du header Authorization
     * 
     * @param authHeader Header Authorization
     * @return Token JWT ou null si invalide
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     * 
     * @param token Token JWT
     * @param role Rôle à vérifier
     * @return true si l'utilisateur a le rôle
     */
    public Boolean hasRole(String token, String role) {
        try {
            List<String> roles = extractRoles(token);
            boolean hasRole = roles != null && roles.contains(role);
            
            log.debug("🔍 Vérification du rôle '{}' pour l'utilisateur : {}", 
                    role, hasRole ? "✅ Autorisé" : "❌ Non autorisé");
            
            return hasRole;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification du rôle {} : {}", role, e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si l'utilisateur a au moins un des rôles spécifiés
     * 
     * @param token Token JWT
     * @param roles Rôles à vérifier
     * @return true si l'utilisateur a au moins un des rôles
     */
    public Boolean hasAnyRole(String token, String... roles) {
        try {
            List<String> userRoles = extractRoles(token);
            if (userRoles == null) {
                return false;
            }
            
            for (String role : roles) {
                if (userRoles.contains(role)) {
                    log.debug("✅ Utilisateur autorisé avec le rôle : {}", role);
                    return true;
                }
            }
            
            log.debug("❌ Utilisateur non autorisé - aucun des rôles requis : {}", 
                    String.join(", ", roles));
            return false;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification des rôles : {}", e.getMessage());
            return false;
        }
    }

    /**
     * Génère la clé de signature pour les tokens JWT
     * 
     * @return Clé de signature
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Obtient les informations de l'utilisateur à partir du token
     * 
     * @param token Token JWT
     * @return Informations utilisateur
     */
    public UserInfo getUserInfo(String token) {
        try {
            return UserInfo.builder()
                    .userId(extractUserId(token))
                    .username(extractUsername(token))
                    .email(extractEmail(token))
                    .roles(extractRoles(token))
                    .build();
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction des informations utilisateur : {}", 
                    e.getMessage());
            throw new RuntimeException("Impossible d'extraire les informations utilisateur", e);
        }
    }

    /**
     * Classe pour encapsuler les informations utilisateur
     */
    @lombok.Builder
    @lombok.Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private List<String> roles;
    }
}
