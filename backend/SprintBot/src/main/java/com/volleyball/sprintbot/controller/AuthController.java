package com.volleyball.sprintbot.controller;

import com.volleyball.sprintbot.dto.LoginRequest;
import com.volleyball.sprintbot.dto.UpdateProfilRequest;
import com.volleyball.sprintbot.entity.Utilisateur;
import com.volleyball.sprintbot.service.AuthService;
import com.volleyball.sprintbot.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Utilisateur> utilisateur = authService.authenticate(
            loginRequest.getEmail(), 
            loginRequest.getMotDePasse()
        );

        if (utilisateur.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Connexion réussie");
            response.put("utilisateur", utilisateur.get());
            response.put("role", utilisateur.get().getRole());
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Email ou mot de passe incorrect");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/profil/{id}")
    public ResponseEntity<?> updateProfil(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfilRequest request) {
        
        try {
            Utilisateur utilisateur = utilisateurService.updateProfil(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profil mis à jour avec succès");
            response.put("utilisateur", utilisateur);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour : " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profil/{id}")
    public ResponseEntity<?> getProfil(@PathVariable Long id) {
        try {
            Utilisateur utilisateur = utilisateurService.findById(id);
            return ResponseEntity.ok(utilisateur);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}