package com.sprintbot.gateway.config;

import com.sprintbot.gateway.filter.AuthenticationFilter;
import com.sprintbot.gateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration de sécurité pour le Gateway Service
 * 
 * Cette configuration gère :
 * - L'authentification JWT centralisée
 * - L'autorisation basée sur les rôles
 * - Les routes publiques et protégées
 * - La gestion des erreurs d'authentification
 * - Les filtres de sécurité personnalisés
 * 
 * @author SprintBot Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    /**
     * Configuration principale de la sécurité
     * 
     * @param http Configuration HTTP de sécurité
     * @return Chaîne de filtres de sécurité configurée
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.debug("🔐 Configuration de la sécurité du Gateway");
        
        return http
            // Désactivation de CSRF pour les APIs REST
            .csrf(csrf -> csrf.disable())
            
            // Configuration CORS (déjà gérée par CorsWebFilter)
            .cors(cors -> cors.disable())
            
            // Configuration des autorisations
            .authorizeExchange(exchanges -> exchanges
                
                // Routes publiques - Pas d'authentification requise
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .pathMatchers("/auth-user-service/api/auth/login", "/auth-user-service/api/auth/register").permitAll()
                .pathMatchers("/api/auth/refresh-token").permitAll()
                .pathMatchers("/auth-user-service/api/auth/refresh-token").permitAll()
                .pathMatchers("/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
                .pathMatchers("/auth-user-service/api/auth/forgot-password", "/auth-user-service/api/auth/reset-password").permitAll()
                .pathMatchers("/eureka/**").permitAll()
                .pathMatchers("/fallback/**").permitAll()
                
                // Routes d'administration - Rôle ADMIN requis
                .pathMatchers("/api/*/admin/**").hasRole("ADMIN")
                .pathMatchers("/actuator/**").hasRole("ADMIN")
                
                // Routes médicales - Rôles spécifiques
                .pathMatchers("/api/medical/admin/**").hasAnyRole("ADMIN", "MEDICAL_ADMIN")
                .pathMatchers("/api/medical/**").hasAnyRole("USER", "COACH", "MEDICAL_ADMIN", "ADMIN")
                
                // Routes financières - Rôles spécifiques
                .pathMatchers("/api/finance/admin/**").hasAnyRole("ADMIN", "FINANCE_ADMIN")
                .pathMatchers("/api/finance/**").hasAnyRole("USER", "COACH", "FINANCE_ADMIN", "ADMIN")
                
                // Routes de planification - Rôles spécifiques
                .pathMatchers("/api/planning/coach/**").hasAnyRole("COACH", "ADMIN")
                .pathMatchers("/api/planning/**").hasAnyRole("USER", "COACH", "ADMIN")
                
                // Routes de communication - Tous les utilisateurs authentifiés
                .pathMatchers("/api/communication/**").hasAnyRole("USER", "COACH", "ADMIN")
                
                // Routes d'authentification - Tous les utilisateurs authentifiés
                .pathMatchers("/api/auth/**").hasAnyRole("USER", "COACH", "ADMIN")
                
                // Toutes les autres routes nécessitent une authentification
                .anyExchange().authenticated()
            )
            
            // Gestion des erreurs d'authentification
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            
            // Ajout du filtre d'authentification JWT
            .addFilterBefore(authenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            
            .build();
    }

    /**
     * Filtre d'authentification JWT personnalisé
     * 
     * @return Filtre d'authentification configuré
     */
    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(jwtService);
    }

    /**
     * Point d'entrée pour les erreurs d'authentification
     * 
     * @return Handler pour les erreurs d'authentification
     */
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            log.warn("🚫 Tentative d'accès non authentifiée : {}",
                exchange.getRequest().getPath().value());

            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
            exchange.getResponse().getHeaders().add("X-Authentication-Error", "true");
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);

            String errorResponse = """
                {
                    "error": "Unauthorized",
                    "message": "Authentification requise pour accéder à cette ressource",
                    "status": 401,
                    "timestamp": "%s",
                    "path": "%s"
                }
                """.formatted(
                    java.time.Instant.now().toString(),
                    exchange.getRequest().getPath().value()
                );

            org.springframework.core.io.buffer.DataBuffer buffer =
                exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());

            return exchange.getResponse().writeWith(Mono.just(buffer));
        };
    }

    /**
     * Handler pour les erreurs d'autorisation (accès refusé)
     * 
     * @return Handler pour les erreurs d'autorisation
     */
    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            log.warn("🚫 Accès refusé pour l'utilisateur : {} sur le chemin : {}",
                exchange.getPrincipal().cast(org.springframework.security.core.Authentication.class)
                    .map(auth -> auth.getName()).block(),
                exchange.getRequest().getPath().value());

            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
            exchange.getResponse().getHeaders().add("X-Authorization-Error", "true");
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);

            String errorResponse = """
                {
                    "error": "Forbidden",
                    "message": "Accès refusé : privilèges insuffisants pour cette ressource",
                    "status": 403,
                    "timestamp": "%s",
                    "path": "%s"
                }
                """.formatted(
                    java.time.Instant.now().toString(),
                    exchange.getRequest().getPath().value()
                );

            org.springframework.core.io.buffer.DataBuffer buffer =
                exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());

            return exchange.getResponse().writeWith(Mono.just(buffer));
        };
    }
}
