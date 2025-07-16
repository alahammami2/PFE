package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @Column(name = "id_message", length = 100)
    private String idMessage;
    
    @Column(name = "contenu", columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "date_envoi")
    private LocalDate dateEnvoi;
    
    @Column(name = "emetteur_id", length = 100)
    private String emetteurId;
    
    @Column(name = "destinataire_id", length = 100)
    private String destinataireId;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;
}

