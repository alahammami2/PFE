package com.sprintbot.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Application principale du microservice Finance
 * 
 * Ce microservice gère :
 * - Suivi budgétaire et planification financière
 * - Gestion des revenus et dépenses
 * - Administration des sponsors et partenariats
 * - Calcul et gestion des salaires
 * 
 * Fonctionnalités activées :
 * - JPA Auditing pour traçabilité des modifications
 * - Cache pour optimisation des performances
 * - Traitement asynchrone pour les rapports
 * - Tâches planifiées pour les échéances
 * - Gestion transactionnelle pour la cohérence financière
 * 
 * @author SprintBot Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class FinanceServiceApplication {

    /**
     * Point d'entrée principal de l'application
     * 
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(FinanceServiceApplication.class, args);
    }
}
