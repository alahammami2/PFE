package com.sprintbot.planningperformance.dto;

import com.sprintbot.planningperformance.entity.Absence;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class AbsenceDto {
    
    private Long id;
    
    @NotNull(message = "L'ID de l'entraînement est obligatoire")
    private Long entrainementId;
    
    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long joueurId;
    
    @NotNull(message = "Le motif d'absence est obligatoire")
    private Absence.MotifAbsence motif;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    private Boolean justifiee;
    
    @Size(max = 255, message = "L'URL du justificatif ne peut pas dépasser 255 caractères")
    private String justificatifUrl;
    
    private LocalDateTime dateDeclaration;
    
    // Informations de l'entraînement (pour l'affichage)
    private String titrEntrainement;
    private String lieuEntrainement;
    private java.time.LocalDate dateEntrainement;
    private java.time.LocalTime heureDebutEntrainement;
    private java.time.LocalTime heureFinEntrainement;
    private com.sprintbot.planningperformance.entity.Entrainement.TypeEntrainement typeEntrainement;
    
    // Informations calculées
    private Boolean tardive; // Si déclarée après l'entraînement
    private Integer nombreAbsencesConsecutives;
    
    // Constructeurs
    public AbsenceDto() {}
    
    public AbsenceDto(Absence absence) {
        this.id = absence.getId();
        this.entrainementId = absence.getEntrainement().getId();
        this.joueurId = absence.getJoueurId();
        this.motif = absence.getMotif();
        this.description = absence.getDescription();
        this.justifiee = absence.getJustifiee();
        this.justificatifUrl = absence.getJustificatifUrl();
        this.dateDeclaration = absence.getDateDeclaration();
        
        // Informations de l'entraînement
        if (absence.getEntrainement() != null) {
            this.titrEntrainement = absence.getEntrainement().getTitre();
            this.lieuEntrainement = absence.getEntrainement().getLieu();
            this.dateEntrainement = absence.getEntrainement().getDateEntrainement();
            this.heureDebutEntrainement = absence.getEntrainement().getHeureDebut();
            this.heureFinEntrainement = absence.getEntrainement().getHeureFin();
            this.typeEntrainement = absence.getEntrainement().getTypeEntrainement();
            
            // Calculer si l'absence est tardive
            LocalDateTime dateTimeEntrainement = LocalDateTime.of(
                absence.getEntrainement().getDateEntrainement(),
                absence.getEntrainement().getHeureDebut()
            );
            this.tardive = absence.getDateDeclaration().isAfter(dateTimeEntrainement);
        }
    }
    
    // Méthode pour convertir vers l'entité
    public Absence toEntity() {
        Absence absence = new Absence();
        absence.setId(this.id);
        absence.setJoueurId(this.joueurId);
        absence.setMotif(this.motif);
        absence.setDescription(this.description);
        absence.setJustifiee(this.justifiee != null ? this.justifiee : false);
        absence.setJustificatifUrl(this.justificatifUrl);
        absence.setDateDeclaration(this.dateDeclaration != null ? this.dateDeclaration : LocalDateTime.now());
        return absence;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEntrainementId() { return entrainementId; }
    public void setEntrainementId(Long entrainementId) { this.entrainementId = entrainementId; }
    
    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }
    
    public Absence.MotifAbsence getMotif() { return motif; }
    public void setMotif(Absence.MotifAbsence motif) { this.motif = motif; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getJustifiee() { return justifiee; }
    public void setJustifiee(Boolean justifiee) { this.justifiee = justifiee; }
    
    public String getJustificatifUrl() { return justificatifUrl; }
    public void setJustificatifUrl(String justificatifUrl) { this.justificatifUrl = justificatifUrl; }
    
    public LocalDateTime getDateDeclaration() { return dateDeclaration; }
    public void setDateDeclaration(LocalDateTime dateDeclaration) { this.dateDeclaration = dateDeclaration; }
    
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
    
    public com.sprintbot.planningperformance.entity.Entrainement.TypeEntrainement getTypeEntrainement() { return typeEntrainement; }
    public void setTypeEntrainement(com.sprintbot.planningperformance.entity.Entrainement.TypeEntrainement typeEntrainement) { this.typeEntrainement = typeEntrainement; }
    
    public Boolean getTardive() { return tardive; }
    public void setTardive(Boolean tardive) { this.tardive = tardive; }
    
    public Integer getNombreAbsencesConsecutives() { return nombreAbsencesConsecutives; }
    public void setNombreAbsencesConsecutives(Integer nombreAbsencesConsecutives) { this.nombreAbsencesConsecutives = nombreAbsencesConsecutives; }
}
