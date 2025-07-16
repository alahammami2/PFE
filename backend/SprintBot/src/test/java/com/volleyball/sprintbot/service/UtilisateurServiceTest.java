package com.volleyball.sprintbot.service;

import com.volleyball.sprintbot.dto.UpdateProfilRequest;
import com.volleyball.sprintbot.entity.Utilisateur;
import com.volleyball.sprintbot.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setEmail("test@example.com");
        utilisateur.setNom("Doe");
        utilisateur.setPrenom("John");
    }

    @Test
    void testUpdateProfilSuccess() {
        // Given
        UpdateProfilRequest request = new UpdateProfilRequest();
        request.setNom("Smith");
        request.setPrenom("Jane");
        request.setEmail("jane@example.com");
        request.setTelephone("123456789");

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        Utilisateur result = utilisateurService.updateProfil(1L, request);

        // Then
        assertEquals("Smith", result.getNom());
        assertEquals("Jane", result.getPrenom());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("123456789", result.getTelephone());
    }

    @Test
    void testUpdateProfilEmailAlreadyExists() {
        // Given
        UpdateProfilRequest request = new UpdateProfilRequest();
        request.setEmail("existing@example.com");

        Utilisateur existingUser = new Utilisateur();
        existingUser.setId(2L);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            utilisateurService.updateProfil(1L, request);
        });
    }
}