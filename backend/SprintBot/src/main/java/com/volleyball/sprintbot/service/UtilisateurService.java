package com.volleyball.sprintbot.service;

import com.volleyball.sprintbot.dto.UpdateProfilRequest;
import com.volleyball.sprintbot.entity.Utilisateur;
import com.volleyball.sprintbot.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
    }

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur updateProfil(Long id, UpdateProfilRequest request) {
        Utilisateur utilisateur = findById(id);
        
        // Vérifier si l'email existe déjà pour un autre utilisateur
        Optional<Utilisateur> existingUser = utilisateurRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new RuntimeException("Cet email est déjà utilisé par un autre utilisateur");
        }
        
        // Mettre à jour les informations
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public List<Utilisateur> findByRole(String role) {
        return utilisateurRepository.findByRole(role);
    }

    public boolean existsByEmail(String email) {
        return utilisateurRepository.findByEmail(email).isPresent();
    }
}