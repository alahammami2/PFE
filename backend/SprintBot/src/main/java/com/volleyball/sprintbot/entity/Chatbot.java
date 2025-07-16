package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "chatbots")
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID du chatbot est obligatoire")
    @Size(max = 100)
    @Column(name = "id_chatbot", nullable = false, unique = true, length = 100)
    private String idChatbot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    // Constructeurs
    public Chatbot() {}

    public Chatbot(String idChatbot, Equipe equipe) {
        this.idChatbot = idChatbot;
        this.equipe = equipe;
    }

    // Méthodes métier
    public void repondre() {
        // Logique pour répondre automatiquement
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdChatbot() { return idChatbot; }
    public void setIdChatbot(String idChatbot) { this.idChatbot = idChatbot; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
}
