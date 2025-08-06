package com.sprintbot.gateway.filter;

import com.sprintbot.gateway.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtre d'authentification JWT pour le Gateway Service
 * 
 * Ce filtre intercepte toutes les requêtes entrantes et :
 * - Extrait et valide les tokens JWT
 * - Configure le contexte de sécurité Spring
 * - Ajoute les informations utilisateur aux headers
 * - Gère les erreurs d'authentification
 * 
 * Le filtre est appliqué avant les filtres de sécurité Spring
 * et permet une authentification centralisée pour tous les microservices.
 * 
 * @author SprintBot Team
 * @version 1.0.0
 */
@Slf4j
public class AuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    // Routes publiques qui ne nécessitent pas d'authentification
    private static final List<String> PUBLIC_ROUTES = List.of(
        "/actuator/health",
        "/actuator/info",
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh-token",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/users", // Pour l'enregistrement d'utilisateurs
        "/eureka",
        "/fallback"
    );

    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        
        log.debug("🔍 Traitement de la requête : {} {}", method, path);

        // Vérifier si la route est publique
        if (isPublicRoute(path) || "OPTIONS".equals(method)) {
            log.debug("🌐 Route publique, pas d'authentification requise : {}", path);
            return chain.filter(exchange);
        }

        // Extraire le token JWT du header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = jwtService.extractTokenFromHeader(authHeader);

        if (token == null) {
            log.warn("🚫 Token JWT manquant pour la route protégée : {}", path);
            return handleAuthenticationError(exchange, "Token JWT manquant");
        }

        try {
            // Valider le token JWT
            if (!jwtService.validateToken(token)) {
                log.warn("🚫 Token JWT invalide pour la route : {}", path);
                return handleAuthenticationError(exchange, "Token JWT invalide");
            }

            // Extraire les informations utilisateur
            JwtService.UserInfo userInfo = jwtService.getUserInfo(token);
            
            log.debug("✅ Authentification réussie pour l'utilisateur : {} sur la route : {}", 
                    userInfo.getUsername(), path);

            // Créer l'objet Authentication pour Spring Security
            Authentication authentication = createAuthentication(userInfo);

            // Ajouter les headers utilisateur pour les microservices
            ServerWebExchange mutatedExchange = addUserHeaders(exchange, userInfo);

            // Configurer le contexte de sécurité et continuer la chaîne
            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'authentification pour la route {} : {}", path, e.getMessage());
            return handleAuthenticationError(exchange, "Erreur d'authentification");
        }
    }

    /**
     * Vérifie si une route est publique
     * 
     * @param path Chemin de la requête
     * @return true si la route est publique
     */
    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    /**
     * Crée un objet Authentication pour Spring Security
     * 
     * @param userInfo Informations utilisateur
     * @return Objet Authentication
     */
    private Authentication createAuthentication(JwtService.UserInfo userInfo) {
        // Convertir les rôles en authorities Spring Security
        List<SimpleGrantedAuthority> authorities = userInfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        // Créer l'objet Authentication
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                        userInfo.getUsername(),
                        null, // Pas de credentials nécessaires
                        authorities
                );

        // Ajouter les détails utilisateur
        authentication.setDetails(userInfo);

        log.debug("🔐 Authentication créée pour l'utilisateur : {} avec les rôles : {}", 
                userInfo.getUsername(), userInfo.getRoles());

        return authentication;
    }

    /**
     * Ajoute les headers utilisateur pour les microservices
     * 
     * @param exchange Exchange de la requête
     * @param userInfo Informations utilisateur
     * @return Exchange modifié avec les headers
     */
    private ServerWebExchange addUserHeaders(ServerWebExchange exchange, JwtService.UserInfo userInfo) {
        return exchange.mutate()
                .request(request -> request
                    .header("X-User-Id", userInfo.getUserId().toString())
                    .header("X-User-Username", userInfo.getUsername())
                    .header("X-User-Email", userInfo.getEmail())
                    .header("X-User-Roles", String.join(",", userInfo.getRoles()))
                    .header("X-Gateway-Service", "SprintBot-Gateway")
                    .header("X-Request-Timestamp", String.valueOf(System.currentTimeMillis()))
                )
                .build();
    }

    /**
     * Gère les erreurs d'authentification
     * 
     * @param exchange Exchange de la requête
     * @param message Message d'erreur
     * @return Mono avec la réponse d'erreur
     */
    private Mono<Void> handleAuthenticationError(ServerWebExchange exchange, String message) {
        log.warn("🚫 Erreur d'authentification : {} pour la route : {}", 
                message, exchange.getRequest().getPath().value());

        // Configurer la réponse d'erreur
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("X-Authentication-Error", "true");

        // Corps de la réponse d'erreur
        String errorResponse = String.format("""
            {
                "error": "Unauthorized",
                "message": "%s",
                "status": 401,
                "timestamp": "%s",
                "path": "%s"
            }
            """, 
            message,
            java.time.Instant.now().toString(),
            exchange.getRequest().getPath().value()
        );

        // Écrire la réponse
        org.springframework.core.io.buffer.DataBuffer buffer = 
                exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
