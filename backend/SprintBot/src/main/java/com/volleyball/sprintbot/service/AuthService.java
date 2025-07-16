package com.volleyball.sprintbot.service;

import com.volleyball.sprintbot.entity.Utilisateur;
import com.volleyball.sprintbot.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Utilisateur> authenticate(String email, String motDePasse) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        
        if (utilisateur.isPresent() && 
            passwordEncoder.matches(motDePasse, utilisateur.get().getMotDePasse())) {
            
            // Mettre à jour la dernière connexion
            utilisateur.get().login();
            utilisateurRepository.save(utilisateur.get());
            
            return utilisateur;
        }
        
        return Optional.empty();
    }

    public Utilisateur register(Utilisateur utilisateur) {
        // Encoder le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return utilisateurRepository.save(utilisateur);
    }
}