package com.sprintbot.authuser.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Configuration et utilitaires JWT pour l'authentification
 * Gère la création, validation et extraction des tokens JWT
 */
@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Génère un token JWT pour un utilisateur
     * @param email l'email de l'utilisateur
     * @param role le rôle de l'utilisateur
     * @param userId l'ID de l'utilisateur
     * @return le token JWT généré
     */
    public String generateToken(String email, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        claims.put("type", "access");
        return createToken(claims, email, expiration);
    }

    /**
     * Génère un refresh token
     * @param email l'email de l'utilisateur
     * @param userId l'ID de l'utilisateur
     * @return le refresh token généré
     */
    public String generateRefreshToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, email, refreshExpiration);
    }

    /**
     * Crée un token avec les claims spécifiés
     * @param claims les claims à inclure
     * @param subject le sujet du token (généralement l'email)
     * @param expiration la durée d'expiration en millisecondes
     * @return le token créé
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrait l'email du token
     * @param token le token JWT
     * @return l'email extrait
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait l'ID utilisateur du token
     * @param token le token JWT
     * @return l'ID utilisateur
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extrait le rôle du token
     * @param token le token JWT
     * @return le rôle extrait
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extrait le type de token
     * @param token le token JWT
     * @return le type de token (access/refresh)
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    /**
     * Extrait la date d'expiration du token
     * @param token le token JWT
     * @return la date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du token
     * @param token le token JWT
     * @param claimsResolver la fonction pour extraire le claim
     * @return le claim extrait
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token
     * @param token le token JWT
     * @return tous les claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si le token est expiré
     * @param token le token JWT
     * @return true si le token est expiré
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valide un token pour un utilisateur
     * @param token le token JWT
     * @param email l'email de l'utilisateur
     * @return true si le token est valide
     */
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifie si c'est un token d'accès
     * @param token le token JWT
     * @return true si c'est un access token
     */
    public Boolean isAccessToken(String token) {
        try {
            return "access".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifie si c'est un refresh token
     * @param token le token JWT
     * @return true si c'est un refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtient le temps restant avant expiration en millisecondes
     * @param token le token JWT
     * @return le temps restant en millisecondes
     */
    public Long getTimeToExpiration(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - new Date().getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    // Getters pour les valeurs de configuration
    public Long getExpiration() {
        return expiration;
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}
