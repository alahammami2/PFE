package com.sprintbot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;

/**
 * Application principale du Gateway Service
 * 
 * Ce service fournit un point d'entrée unique pour l'écosystème SprintBot.
 * Il gère le routage intelligent, la sécurité centralisée, et l'observabilité
 * pour tous les microservices de la plateforme.
 * 
 * Fonctionnalités principales :
 * - Routage basé sur les chemins vers les microservices
 * - Authentification et autorisation centralisées
 * - Load balancing automatique avec Eureka
 * - Circuit breaker et retry logic
 * - Rate limiting et protection DDoS
 * - Monitoring et métriques détaillées
 * - CORS global et sécurité
 * 
 * @author SprintBot Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApplication {

    /**
     * Point d'entrée principal de l'application
     * 
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        log.info("🚀 Démarrage du Gateway Service...");
        
        SpringApplication app = new SpringApplication(GatewayServiceApplication.class);
        
        // Configuration des propriétés par défaut
        app.setDefaultProperties(java.util.Map.of(
            "spring.application.name", "gateway-service",
            "server.port", "8080",
            "eureka.client.service-url.defaultZone", "http://localhost:8761/eureka/"
        ));
        
        app.run(args);
        
        log.info("✅ Gateway Service démarré avec succès!");
        log.info("🌐 API Gateway disponible sur : http://localhost:8080");
        log.info("📊 Actuator endpoints disponibles sur : http://localhost:8080/actuator");
    }

    /**
     * Initialisation post-démarrage
     * Affiche les informations importantes du service
     */
    @PostConstruct
    public void init() {
        log.info("🌐 Gateway Service initialisé");
        log.info("🛣️ Routes configurées pour les microservices SprintBot :");
        log.info("   - /api/auth/** → auth-user-service");
        log.info("   - /api/planning/** → planning-performance-service");
        log.info("   - /api/medical/** → medical-admin-service");
        log.info("   - /api/communication/** → communication-service");
        log.info("   - /api/finance/** → finance-service");
        log.info("🔧 Fonctionnalités activées :");
        log.info("   - Service Discovery avec Eureka");
        log.info("   - Load Balancing automatique");
        log.info("   - Circuit Breaker et Retry");
        log.info("   - Rate Limiting");
        log.info("   - Authentification JWT");
        log.info("   - CORS global");
        log.info("   - Monitoring et métriques");
    }

    /**
     * Configuration des routes du Gateway
     * 
     * Cette méthode définit toutes les routes vers les microservices SprintBot
     * avec leurs filtres, prédicats et configurations spécifiques.
     * 
     * @param builder Builder pour la configuration des routes
     * @return Localisateur de routes configuré
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.debug("🛣️ Configuration des routes du Gateway");
        
        return builder.routes()
            
            // Route pour Auth User Service
            .route("auth-user-service", r -> r
                .path("/api/auth/**")
                .filters(f -> f
                    .stripPrefix(2) // Supprime /api/auth
                    .circuitBreaker(config -> config
                        .setName("auth-user-service-cb")
                        .setFallbackUri("forward:/fallback/auth"))
                    .retry(config -> config
                        .setRetries(3)
                        .setBackoff(java.time.Duration.ofMillis(100), 
                                   java.time.Duration.ofMillis(1000), 2, false))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver()))
                    .addRequestHeader("X-Gateway-Service", "SprintBot-Gateway")
                    .addResponseHeader("X-Response-Time", "#{T(System).currentTimeMillis()}")
                )
                .uri("lb://auth-user-service")
            )
            
            // Route pour Planning Performance Service
            .route("planning-performance-service", r -> r
                .path("/api/planning/**")
                .filters(f -> f
                    .stripPrefix(2) // Supprime /api/planning
                    .circuitBreaker(config -> config
                        .setName("planning-performance-service-cb")
                        .setFallbackUri("forward:/fallback/planning"))
                    .retry(config -> config
                        .setRetries(3)
                        .setBackoff(java.time.Duration.ofMillis(100), 
                                   java.time.Duration.ofMillis(1000), 2, false))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver()))
                    .addRequestHeader("X-Gateway-Service", "SprintBot-Gateway")
                )
                .uri("lb://planning-performance-service")
            )
            
            // Route pour Medical Admin Service
            .route("medical-admin-service", r -> r
                .path("/api/medical/**")
                .filters(f -> f
                    .stripPrefix(2) // Supprime /api/medical
                    .circuitBreaker(config -> config
                        .setName("medical-admin-service-cb")
                        .setFallbackUri("forward:/fallback/medical"))
                    .retry(config -> config
                        .setRetries(3)
                        .setBackoff(java.time.Duration.ofMillis(100), 
                                   java.time.Duration.ofMillis(1000), 2, false))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver()))
                    .addRequestHeader("X-Gateway-Service", "SprintBot-Gateway")
                )
                .uri("lb://medical-admin-service")
            )
            
            // Route pour Communication Service
            .route("communication-service", r -> r
                .path("/api/communication/**")
                .filters(f -> f
                    .stripPrefix(2) // Supprime /api/communication
                    .circuitBreaker(config -> config
                        .setName("communication-service-cb")
                        .setFallbackUri("forward:/fallback/communication"))
                    .retry(config -> config
                        .setRetries(3)
                        .setBackoff(java.time.Duration.ofMillis(100), 
                                   java.time.Duration.ofMillis(1000), 2, false))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver()))
                    .addRequestHeader("X-Gateway-Service", "SprintBot-Gateway")
                )
                .uri("lb://communication-service")
            )
            
            // Route pour Finance Service
            .route("finance-service", r -> r
                .path("/api/finance/**")
                .filters(f -> f
                    .stripPrefix(2) // Supprime /api/finance
                    .circuitBreaker(config -> config
                        .setName("finance-service-cb")
                        .setFallbackUri("forward:/fallback/finance"))
                    .retry(config -> config
                        .setRetries(3)
                        .setBackoff(java.time.Duration.ofMillis(100), 
                                   java.time.Duration.ofMillis(1000), 2, false))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver()))
                    .addRequestHeader("X-Gateway-Service", "SprintBot-Gateway")
                )
                .uri("lb://finance-service")
            )
            
            // Route pour Discovery Service (monitoring)
            .route("discovery-service", r -> r
                .path("/eureka/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway-Service", "SprintBot-Gateway")
                )
                .uri("lb://discovery-service")
            )
            
            .build();
    }

    /**
     * Configuration CORS globale pour le Gateway
     * 
     * Cette configuration permet aux applications frontend d'accéder
     * aux APIs via le Gateway en gérant les politiques CORS de manière centralisée.
     * 
     * @return Filtre CORS configuré
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        log.debug("🌐 Configuration CORS globale");
        
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Origines autorisées (à adapter selon l'environnement)
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "https://localhost:*",
            "http://sprintbot.local:*",
            "https://sprintbot.local:*",
            "https://*.sprintbot.com"
        ));
        
        // Méthodes HTTP autorisées
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers autorisés
        corsConfig.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers", "X-Gateway-Service"
        ));
        
        // Headers exposés
        corsConfig.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "X-Response-Time", "X-Gateway-Service"
        ));
        
        // Autoriser les credentials
        corsConfig.setAllowCredentials(true);
        
        // Durée de cache pour les requêtes preflight
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }

    /**
     * Configuration du Rate Limiter Redis
     *
     * @return Rate limiter configuré
     */
    @Bean
    public org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter redisRateLimiter() {
        // Configuration par défaut : 100 requêtes par seconde avec burst de 200
        return new org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter(100, 200, 1);
    }

    /**
     * Résolveur de clé pour le rate limiting basé sur l'utilisateur
     *
     * @return Key resolver configuré
     */
    @Bean
    public org.springframework.cloud.gateway.filter.ratelimit.KeyResolver userKeyResolver() {
        return exchange -> {
            // Utilise l'IP client comme clé par défaut
            String clientIp = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

            // Si un token JWT est présent, utilise l'ID utilisateur
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // TODO: Extraire l'ID utilisateur du JWT
                // Pour l'instant, utilise l'IP
            }

            return reactor.core.publisher.Mono.just(clientIp);
        };
    }
}
