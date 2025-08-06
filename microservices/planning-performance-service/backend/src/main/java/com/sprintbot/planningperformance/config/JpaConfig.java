package com.sprintbot.planningperformance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.sprintbot.planningperformance.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // Configuration JPA pour le microservice planning-performance
}
