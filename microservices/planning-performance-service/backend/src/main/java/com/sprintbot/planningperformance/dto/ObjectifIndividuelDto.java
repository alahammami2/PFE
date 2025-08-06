package com.sprintbot.planningperformance.dto;

import com.sprintbot.planningperformance.entity.ObjectifIndividuel;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ObjectifIndividuelDto {
    
    private Long id;
    
    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long joueurId;
    
    @NotNull(message = "L'ID du coach est obligatoire")
    private Long coachId;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @NotNull(message = "Le type d'objectif est obligatoire")
    private ObjectifIndividuel.TypeObjectif type;
    
    @NotNull(message = "La date d'échéance est obligatoire")
    @Future(message = "La date d'échéance doit être dans le futur")
    private LocalDate dateEcheance;
    
    @DecimalMin(value = "0.0", message = "La progression doit être positive")
    @DecimalMax(value = "100.0", message = "La progression ne peut pas dépasser 100%")
    @Digits(integer = 3, fraction = 2, message = "La progression doit avoir au maximum 3 chiffres avant et 2 après la virgule")
    private BigDecimal progression;
    
    private ObjectifIndividuel.StatutObjectif statut;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;
    
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    // Informations calculées
    private Integer joursRestants;
    private Boolean procheEcheance; // Si échéance dans moins de 7 jours
    private Boolean echu;
    private String tendanceProgression; // "RAPIDE", "NORMALE", "LENTE"
    
    // Constructeurs
    public ObjectifIndividuelDto() {}
    
    public ObjectifIndividuelDto(ObjectifIndividuel objectif) {
        this.id = objectif.getId();
        this.joueurId = objectif.getJoueurId();
        this.coachId = objectif.getCoachId();
        this.titre = objectif.getTitre();
        this.description = objectif.getDescription();
        this.type = objectif.getType();
        this.dateEcheance = objectif.getDateEcheance();
        this.progression = objectif.getProgression() != null ? BigDecimal.valueOf(objectif.getProgression()) : BigDecimal.ZERO;
        this.statut = objectif.getStatut();
        this.notes = objectif.getNotes();
        this.dateCreation = objectif.getDateCreation().atStartOfDay();
        this.dateModification = objectif.getDateModification();
        
        // Calculs
        this.joursRestants = calculateJoursRestants(objectif.getDateEcheance());
        this.procheEcheance = this.joursRestants != null && this.joursRestants <= 7 && this.joursRestants >= 0;
        this.echu = this.joursRestants != null && this.joursRestants < 0;
        this.tendanceProgression = calculateTendanceProgression(objectif);
    }
    
    // Méthode pour convertir vers l'entité
    public ObjectifIndividuel toEntity() {
        ObjectifIndividuel objectif = new ObjectifIndividuel();
        objectif.setId(this.id);
        objectif.setJoueurId(this.joueurId);
        objectif.setCoachId(this.coachId);
        objectif.setTitre(this.titre);
        objectif.setDescription(this.description);
        objectif.setType(this.type);
        objectif.setDateEcheance(this.dateEcheance);
        objectif.setProgression(this.progression != null ? this.progression.intValue() : 0);
        objectif.setStatut(this.statut != null ? this.statut : ObjectifIndividuel.StatutObjectif.EN_COURS);
        objectif.setNotes(this.notes);
        objectif.setDateCreation(this.dateCreation != null ? this.dateCreation.toLocalDate() : LocalDate.now());
        objectif.setDateModification(this.dateModification);
        return objectif;
    }
    
    // Méthodes utilitaires
    private Integer calculateJoursRestants(LocalDate dateEcheance) {
        if (dateEcheance == null) return null;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dateEcheance);
    }
    
    private String calculateTendanceProgression(ObjectifIndividuel objectif) {
        if (objectif.getProgression() == null || objectif.getDateCreation() == null) {
            return "NORMALE";
        }
        
        long joursEcoules = java.time.temporal.ChronoUnit.DAYS.between(
            objectif.getDateCreation(),
            LocalDate.now()
        );
        
        if (joursEcoules == 0) return "NORMALE";
        
        double progressionParJour = objectif.getProgression().doubleValue() / joursEcoules;
        
        if (progressionParJour > 2.0) return "RAPIDE";
        if (progressionParJour < 0.5) return "LENTE";
        return "NORMALE";
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }
    
    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ObjectifIndividuel.TypeObjectif getType() { return type; }
    public void setType(ObjectifIndividuel.TypeObjectif type) { this.type = type; }
    
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    
    public BigDecimal getProgression() { return progression; }
    public void setProgression(BigDecimal progression) { this.progression = progression; }
    
    public ObjectifIndividuel.StatutObjectif getStatut() { return statut; }
    public void setStatut(ObjectifIndividuel.StatutObjectif statut) { this.statut = statut; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    public Integer getJoursRestants() { return joursRestants; }
    public void setJoursRestants(Integer joursRestants) { this.joursRestants = joursRestants; }
    
    public Boolean getProcheEcheance() { return procheEcheance; }
    public void setProcheEcheance(Boolean procheEcheance) { this.procheEcheance = procheEcheance; }
    
    public Boolean getEchu() { return echu; }
    public void setEchu(Boolean echu) { this.echu = echu; }
    
    public String getTendanceProgression() { return tendanceProgression; }
    public void setTendanceProgression(String tendanceProgression) { this.tendanceProgression = tendanceProgression; }
}
