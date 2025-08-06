package com.sprintbot.authuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Application principale du microservice auth-user-service
 * 
 * Ce microservice gère :
 * - L'authentification des utilisateurs (JWT)
 * - La gestion des utilisateurs et leurs rôles
 * - La mise à jour des profils
 * - La sécurité et l'autorisation
 * 
 * @author SprintBot Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories
@EnableTransactionManagement
public class AuthUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthUserServiceApplication.class, args);
        
        System.out.println("🔐 Auth-User-Service démarré avec succès !");
        System.out.println("📱 API disponible sur : http://localhost:8081");
        System.out.println("🏥 Health check : http://localhost:8081/actuator/health");
        System.out.println("📊 Métriques : http://localhost:8081/actuator/metrics");
    }
}
