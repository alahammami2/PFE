package com.sprintbot.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Application principale du Discovery Service (Eureka Server)
 * 
 * Ce service fournit la découverte de services pour l'écosystème SprintBot.
 * Il permet aux microservices de s'enregistrer automatiquement et de se découvrir
 * mutuellement sans configuration statique.
 * 
 * Fonctionnalités principales :
 * - Registre central des services
 * - Health checks automatiques
 * - Load balancing côté client
 * - Failover et haute disponibilité
 * - Dashboard de monitoring
 * 
 * @author SprintBot Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication
@EnableEurekaServer
@EnableWebSecurity
public class DiscoveryServiceApplication {

    /**
     * Point d'entrée principal de l'application
     * 
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        // Configuration des propriétés système pour Eureka
        System.setProperty("eureka.client.register-with-eureka", "false");
        System.setProperty("eureka.client.fetch-registry", "false");
        
        log.info("🚀 Démarrage du Discovery Service (Eureka Server)...");
        
        SpringApplication app = new SpringApplication(DiscoveryServiceApplication.class);
        
        // Configuration des propriétés par défaut
        app.setDefaultProperties(java.util.Map.of(
            "spring.application.name", "discovery-service",
            "server.port", "8761",
            "eureka.instance.hostname", "localhost",
            "eureka.client.service-url.defaultZone", "http://localhost:8761/eureka/"
        ));
        
        app.run(args);
        
        log.info("✅ Discovery Service démarré avec succès!");
        log.info("🌐 Dashboard Eureka disponible sur : http://localhost:8761");
        log.info("📊 Actuator endpoints disponibles sur : http://localhost:8761/actuator");
    }

    /**
     * Initialisation post-démarrage
     * Affiche les informations importantes du service
     */
    @PostConstruct
    public void init() {
        log.info("🔍 Discovery Service (Eureka Server) initialisé");
        log.info("📋 Services attendus dans l'écosystème SprintBot :");
        log.info("   - auth-user-service (8081)");
        log.info("   - planning-performance-service (8082)");
        log.info("   - medical-admin-service (8083)");
        log.info("   - communication-service (8084)");
        log.info("   - finance-service (8085)");
        log.info("   - gateway-service (8080)");
        log.info("🔧 Configuration Eureka :");
        log.info("   - Self Registration: DISABLED");
        log.info("   - Fetch Registry: DISABLED");
        log.info("   - Dashboard: ENABLED");
        log.info("   - Security: ENABLED");
    }

    /**
     * Configuration de sécurité pour le Discovery Service
     * 
     * Cette configuration :
     * - Protège le dashboard Eureka avec une authentification basique
     * - Permet l'accès libre aux endpoints Eureka pour les services
     * - Sécurise les endpoints Actuator
     * - Configure CSRF et CORS appropriés
     * 
     * @param http Configuration de sécurité HTTP
     * @return Chaîne de filtres de sécurité configurée
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.debug("🔐 Configuration de la sécurité du Discovery Service");
        
        http
            // Configuration CSRF - Désactivé pour les endpoints Eureka
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/eureka/**")
                .ignoringRequestMatchers("/actuator/**")
            )
            
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                // Endpoints Eureka - Accès libre pour les services
                .requestMatchers("/eureka/**").permitAll()
                
                // Endpoints publics
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                
                // Endpoints Actuator sensibles - Authentification requise
                .requestMatchers(EndpointRequest.toAnyEndpoint()
                    .excluding("health", "info")).authenticated()
                
                // Dashboard Eureka - Authentification requise
                .requestMatchers("/").authenticated()
                .requestMatchers("/eureka/css/**").permitAll()
                .requestMatchers("/eureka/js/**").permitAll()
                .requestMatchers("/eureka/fonts/**").permitAll()
                
                // Toutes les autres requêtes
                .anyRequest().authenticated()
            )
            
            // Configuration de l'authentification HTTP Basic
            .httpBasic(basic -> basic
                .realmName("SprintBot Discovery Service")
            )
            
            // Configuration des headers de sécurité
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            
            // Configuration de la gestion des sessions
            .sessionManagement(session -> session
                .maximumSessions(10)
                .maxSessionsPreventsLogin(false)
            );

        log.debug("✅ Sécurité du Discovery Service configurée");
        return http.build();
    }
}
