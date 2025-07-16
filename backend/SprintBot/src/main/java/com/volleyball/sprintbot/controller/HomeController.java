package com.volleyball.sprintbot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bienvenue sur SprintBot - Plateforme intelligente pour gérer une équipe de volley-ball");
        response.put("version", "1.0.0");
        response.put("status", "running");
        return response;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SprintBot API");
        return response;
    }
}
