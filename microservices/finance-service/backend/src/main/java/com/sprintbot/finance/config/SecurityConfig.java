package com.sprintbot.finance.config;

import com.sprintbot.finance.security.JwtAuthenticationEntryPoint;
import com.sprintbot.finance.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration de sécurité pour le service finance
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configuration de la chaîne de filtres de sécurité
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints publics
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll()
                        .requestMatchers("/actuator/metrics").permitAll()
                        
                        // Documentation Swagger
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        
                        // Endpoints de finance - Lecture (FINANCE_USER, FINANCE_MANAGER, ADMIN)
                        .requestMatchers("GET", "/api/budgets/**").hasAnyRole("FINANCE_USER", "FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("GET", "/api/transactions/**").hasAnyRole("FINANCE_USER", "FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("GET", "/api/sponsors/**").hasAnyRole("FINANCE_USER", "FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("GET", "/api/salaires/**").hasAnyRole("FINANCE_USER", "FINANCE_MANAGER", "HR_MANAGER", "ADMIN")
                        .requestMatchers("GET", "/api/rapports/**").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        
                        // Endpoints de finance - Écriture (FINANCE_MANAGER, ADMIN)
                        .requestMatchers("POST", "/api/budgets/**").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("PUT", "/api/budgets/**").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("POST", "/api/transactions").hasAnyRole("FINANCE_USER", "FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("PUT", "/api/transactions/**").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("POST", "/api/transactions/*/valider").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("POST", "/api/transactions/*/rejeter").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("POST", "/api/sponsors/**").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("PUT", "/api/sponsors/**").hasAnyRole("FINANCE_MANAGER", "ADMIN")
                        .requestMatchers("POST", "/api/salaires/**").hasAnyRole("FINANCE_MANAGER", "HR_MANAGER", "ADMIN")
                        
                        // Endpoints de suppression (ADMIN seulement)
                        .requestMatchers("DELETE", "/api/**").hasRole("ADMIN")
                        
                        // Tous les autres endpoints nécessitent une authentification
                        .anyRequest().authenticated()
                );

        // Ajout du filtre JWT avant le filtre d'authentification par nom d'utilisateur/mot de passe
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées (à adapter selon l'environnement)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.sprintbot.com"
        ));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        
        // Headers exposés
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "Content-Disposition"
        ));
        
        // Autoriser les credentials
        configuration.setAllowCredentials(true);
        
        // Durée de cache pour les requêtes preflight
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Encodeur de mot de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Gestionnaire d'authentification
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
