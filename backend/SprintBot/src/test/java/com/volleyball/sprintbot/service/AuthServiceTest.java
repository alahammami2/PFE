package com.volleyball.sprintbot.service;

import com.volleyball.sprintbot.entity.Utilisateur;
import com.volleyball.sprintbot.entity.Joueur;
import com.volleyball.sprintbot.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        utilisateur = new Joueur();
        utilisateur.setId(1L);
        utilisateur.setEmail("test@example.com");
        utilisateur.setMotDePasse("hashedPassword");
        utilisateur.setRole("JOUEUR");
    }

    @Test
    void testAuthenticateSuccess() {
        // Given
        when(utilisateurRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", "hashedPassword"))
            .thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class)))
            .thenReturn(utilisateur);

        // When
        Optional<Utilisateur> result = authService.authenticate("test@example.com", "password123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(utilisateurRepository).save(utilisateur);
    }

    @Test
    void testAuthenticateFailure() {
        // Given
        when(utilisateurRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
            .thenReturn(false);

        // When
        Optional<Utilisateur> result = authService.authenticate("test@example.com", "wrongPassword");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void testAuthenticateUserNotFound() {
        // Given
        when(utilisateurRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());

        // When
        Optional<Utilisateur> result = authService.authenticate("notfound@example.com", "password123");

        // Then
        assertFalse(result.isPresent());
    }
}