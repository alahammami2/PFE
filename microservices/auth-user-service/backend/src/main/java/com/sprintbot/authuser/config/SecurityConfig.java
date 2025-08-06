package com.sprintbot.authuser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration de sécurité Spring Security pour le microservice auth-user-service
 * Configure CORS, JWT, et les autorisations d'accès
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    /**
     * Configuration de la chaîne de filtres de sécurité
     * @param http l'objet HttpSecurity
     * @return SecurityFilterChain configuré
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactivation de CSRF pour les API REST
            .csrf(csrf -> csrf.disable())
            
            // Configuration CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configuration de la gestion de session (stateless pour JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                // Endpoints publics (pas d'authentification requise)
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/auth/health",
                    "/actuator/**",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                
                // Endpoints d'authentification (token requis)
                .requestMatchers(
                    "/api/auth/me",
                    "/api/auth/logout",
                    "/api/auth/validate"
                ).authenticated()
                
                // Endpoints de gestion des utilisateurs
                .requestMatchers("/api/users/**").authenticated()
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            
            // Désactivation de l'authentification HTTP Basic
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // Désactivation du formulaire de connexion
            .formLogin(formLogin -> formLogin.disable());

        // Configuration spéciale pour H2 Console (développement uniquement)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * Configuration CORS pour permettre les requêtes cross-origin
     * @return CorsConfigurationSource configuré
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origins autorisées
        configuration.setAllowedOrigins(allowedOrigins);
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(allowedMethods);
        
        // Headers autorisés
        if ("*".equals(allowedHeaders)) {
            configuration.setAllowedHeaders(Arrays.asList("*"));
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }
        
        // Headers exposés au client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Autorisation des credentials (cookies, headers d'auth)
        configuration.setAllowCredentials(allowCredentials);
        
        // Durée de cache pour les requêtes preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Bean pour l'encodage des mots de passe avec BCrypt
     * @return PasswordEncoder configuré avec BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Force 12 pour plus de sécurité
    }
}
