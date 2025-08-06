package com.sprintbot.planningperformance.dto;

import com.sprintbot.planningperformance.entity.Performance;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PerformanceDto {
    
    private Long id;
    
    @NotNull(message = "L'ID de l'entraînement est obligatoire")
    private Long entrainementId;
    
    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long joueurId;
    
    @NotNull(message = "L'ID de l'évaluateur est obligatoire")
    private Long evaluateurId;
    
    @DecimalMin(value = "0.0", message = "La note globale doit être positive")
    @DecimalMax(value = "10.0", message = "La note globale ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note globale doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal noteGlobale;
    
    @DecimalMin(value = "0.0", message = "La note technique doit être positive")
    @DecimalMax(value = "10.0", message = "La note technique ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note technique doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal noteTechnique;
    
    @DecimalMin(value = "0.0", message = "La note physique doit être positive")
    @DecimalMax(value = "10.0", message = "La note physique ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note physique doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal notePhysique;
    
    @DecimalMin(value = "0.0", message = "La note mentale doit être positive")
    @DecimalMax(value = "10.0", message = "La note mentale ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note mentale doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal noteMental;
    
    @DecimalMin(value = "0.0", message = "L'auto-évaluation doit être positive")
    @DecimalMax(value = "10.0", message = "L'auto-évaluation ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "L'auto-évaluation doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal autoEvaluation;
    
    @Size(max = 1000, message = "Le commentaire du coach ne peut pas dépasser 1000 caractères")
    private String commentaireCoach;
    
    @Size(max = 500, message = "Le commentaire du joueur ne peut pas dépasser 500 caractères")
    private String commentaireJoueur;
    
    private Boolean objectifsAtteints;
    
    private LocalDateTime dateEvaluation;
    
    // Informations de l'entraînement (pour l'affichage)
    private String titrEntrainement;
    private java.time.LocalDate dateEntrainement;
    private com.sprintbot.planningperformance.entity.Entrainement.TypeEntrainement typeEntrainement;
    
    // Informations calculées
    private BigDecimal moyenneEquipe;
    private String tendance; // "PROGRESSION", "STABLE", "REGRESSION"
    
    // Constructeurs
    public PerformanceDto() {}
    
    public PerformanceDto(Performance performance) {
        this.id = performance.getId();
        this.entrainementId = performance.getEntrainement().getId();
        this.joueurId = performance.getJoueurId();
        this.evaluateurId = performance.getEvaluateurId();
        this.noteGlobale = performance.getNoteGlobale();
        this.noteTechnique = performance.getNoteTechnique();
        this.notePhysique = performance.getNotePhysique();
        this.noteMental = performance.getNoteMental();
        this.autoEvaluation = performance.getAutoEvaluation();
        this.commentaireCoach = performance.getCommentaireCoach();
        this.commentaireJoueur = performance.getCommentaireJoueur();
        this.objectifsAtteints = performance.getObjectifsAtteints();
        this.dateEvaluation = performance.getDateEvaluation();
        
        // Informations de l'entraînement
        if (performance.getEntrainement() != null) {
            this.titrEntrainement = performance.getEntrainement().getTitre();
            this.dateEntrainement = performance.getEntrainement().getDateEntrainement();
            this.typeEntrainement = performance.getEntrainement().getTypeEntrainement();
        }
    }
    
    // Méthode pour convertir vers l'entité
    public Performance toEntity() {
        Performance performance = new Performance();
        performance.setId(this.id);
        performance.setJoueurId(this.joueurId);
        performance.setEvaluateurId(this.evaluateurId);
        performance.setNoteGlobale(this.noteGlobale);
        performance.setNoteTechnique(this.noteTechnique);
        performance.setNotePhysique(this.notePhysique);
        performance.setNoteMental(this.noteMental);
        performance.setAutoEvaluation(this.autoEvaluation);
        performance.setCommentaireCoach(this.commentaireCoach);
        performance.setCommentaireJoueur(this.commentaireJoueur);
        performance.setObjectifsAtteints(this.objectifsAtteints);
        performance.setDateEvaluation(this.dateEvaluation != null ? this.dateEvaluation : LocalDateTime.now());
        return performance;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEntrainementId() { return entrainementId; }
    public void setEntrainementId(Long entrainementId) { this.entrainementId = entrainementId; }
    
    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }
    
    public Long getEvaluateurId() { return evaluateurId; }
    public void setEvaluateurId(Long evaluateurId) { this.evaluateurId = evaluateurId; }
    
    public BigDecimal getNoteGlobale() { return noteGlobale; }
    public void setNoteGlobale(BigDecimal noteGlobale) { this.noteGlobale = noteGlobale; }
    
    public BigDecimal getNoteTechnique() { return noteTechnique; }
    public void setNoteTechnique(BigDecimal noteTechnique) { this.noteTechnique = noteTechnique; }
    
    public BigDecimal getNotePhysique() { return notePhysique; }
    public void setNotePhysique(BigDecimal notePhysique) { this.notePhysique = notePhysique; }
    
    public BigDecimal getNoteMental() { return noteMental; }
    public void setNoteMental(BigDecimal noteMental) { this.noteMental = noteMental; }
    
    public BigDecimal getAutoEvaluation() { return autoEvaluation; }
    public void setAutoEvaluation(BigDecimal autoEvaluation) { this.autoEvaluation = autoEvaluation; }
    
    public String getCommentaireCoach() { return commentaireCoach; }
    public void setCommentaireCoach(String commentaireCoach) { this.commentaireCoach = commentaireCoach; }
    
    public String getCommentaireJoueur() { return commentaireJoueur; }
    public void setCommentaireJoueur(String commentaireJoueur) { this.commentaireJoueur = commentaireJoueur; }
    
    public Boolean getObjectifsAtteints() { return objectifsAtteints; }
    public void setObjectifsAtteints(Boolean objectifsAtteints) { this.objectifsAtteints = objectifsAtteints; }
    
    public LocalDateTime getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDateTime dateEvaluation) { this.dateEvaluation = dateEvaluation; }
    
    public String getTitrEntrainement() { return titrEntrainement; }
    public void setTitrEntrainement(String titrEntrainement) { this.titrEntrainement = titrEntrainement; }
    
    public java.time.LocalDate getDateEntrainement() { return dateEntrainement; }
    public void setDateEntrainement(java.time.LocalDate dateEntrainement) { this.dateEntrainement = dateEntrainement; }
    
    public com.sprintbot.planningperformance.entity.Entrainement.TypeEntrainement getTypeEntrainement() { return typeEntrainement; }
    public void setTypeEntrainement(com.sprintbot.planningperformance.entity.Entrainement.TypeEntrainement typeEntrainement) { this.typeEntrainement = typeEntrainement; }
    
    public BigDecimal getMoyenneEquipe() { return moyenneEquipe; }
    public void setMoyenneEquipe(BigDecimal moyenneEquipe) { this.moyenneEquipe = moyenneEquipe; }
    
    public String getTendance() { return tendance; }
    public void setTendance(String tendance) { this.tendance = tendance; }
}
