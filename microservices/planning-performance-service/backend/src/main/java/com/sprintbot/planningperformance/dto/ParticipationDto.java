package com.sprintbot.planningperformance.dto;

import com.sprintbot.planningperformance.entity.Participation;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class ParticipationDto {
    
    private Long id;
    
    @NotNull(message = "L'ID de l'entraînement est obligatoire")
    private Long entrainementId;
    
    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long joueurId;
    
    private Participation.StatutParticipation statut;
    
    private LocalDateTime dateInscription;
    
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;
    
    // Informations de l'entraînement (pour l'affichage)
    private String titrEntrainement;
    private String lieuEntrainement;
    private java.time.LocalDate dateEntrainement;
    private java.time.LocalTime heureDebutEntrainement;
    private java.time.LocalTime heureFinEntrainement;
    
    // Constructeurs
    public ParticipationDto() {}
    
    public ParticipationDto(Participation participation) {
        this.id = participation.getId();
        this.entrainementId = participation.getEntrainement().getId();
        this.joueurId = participation.getJoueurId();
        this.statut = participation.getStatutParticipation();
        this.dateInscription = participation.getDateInscription();
        this.commentaire = participation.getCommentaire();
        
        // Informations de l'entraînement
        if (participation.getEntrainement() != null) {
            this.titrEntrainement = participation.getEntrainement().getTitre();
            this.lieuEntrainement = participation.getEntrainement().getLieu();
            this.dateEntrainement = participation.getEntrainement().getDateEntrainement();
            this.heureDebutEntrainement = participation.getEntrainement().getHeureDebut();
            this.heureFinEntrainement = participation.getEntrainement().getHeureFin();
        }
    }
    
    // Méthode pour convertir vers l'entité
    public Participation toEntity() {
        Participation participation = new Participation();
        participation.setId(this.id);
        participation.setJoueurId(this.joueurId);
        participation.setStatutParticipation(this.statut != null ? this.statut : Participation.StatutParticipation.INSCRIT);
        participation.setDateInscription(this.dateInscription != null ? this.dateInscription : LocalDateTime.now());
        participation.setCommentaire(this.commentaire);
        return participation;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEntrainementId() { return entrainementId; }
    public void setEntrainementId(Long entrainementId) { this.entrainementId = entrainementId; }
    
    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }
    
    public Participation.StatutParticipation getStatut() { return statut; }
    public void setStatut(Participation.StatutParticipation statut) { this.statut = statut; }
    
    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
    
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    
    public String getTitrEntrainement() { return titrEntrainement; }
    public void setTitrEntrainement(String titrEntrainement) { this.titrEntrainement = titrEntrainement; }
    
    public String getLieuEntrainement() { return lieuEntrainement; }
    public void setLieuEntrainement(String lieuEntrainement) { this.lieuEntrainement = lieuEntrainement; }
    
    public java.time.LocalDate getDateEntrainement() { return dateEntrainement; }
    public void setDateEntrainement(java.time.LocalDate dateEntrainement) { this.dateEntrainement = dateEntrainement; }
    
    public java.time.LocalTime getHeureDebutEntrainement() { return heureDebutEntrainement; }
    public void setHeureDebutEntrainement(java.time.LocalTime heureDebutEntrainement) { this.heureDebutEntrainement = heureDebutEntrainement; }
    
    public java.time.LocalTime getHeureFinEntrainement() { return heureFinEntrainement; }
    public void setHeureFinEntrainement(java.time.LocalTime heureFinEntrainement) { this.heureFinEntrainement = heureFinEntrainement; }
}
