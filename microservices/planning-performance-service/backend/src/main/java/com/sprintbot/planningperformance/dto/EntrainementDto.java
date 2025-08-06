package com.sprintbot.planningperformance.dto;

import com.sprintbot.planningperformance.entity.Entrainement;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class EntrainementDto {
    
    private Long id;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date doit être dans le futur ou aujourd'hui")
    private LocalDate date;
    
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;
    
    @NotNull(message = "Le type d'entraînement est obligatoire")
    private Entrainement.TypeEntrainement type;
    
    @NotBlank(message = "Le lieu est obligatoire")
    @Size(max = 200, message = "Le lieu ne peut pas dépasser 200 caractères")
    private String lieu;
    
    @NotNull(message = "L'ID du coach est obligatoire")
    private Long coachId;
    
    @Min(value = 1, message = "Le nombre maximum de participants doit être au moins 1")
    @Max(value = 50, message = "Le nombre maximum de participants ne peut pas dépasser 50")
    private Integer nombreMaxParticipants;
    
    private Entrainement.StatutEntrainement statut;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;
    
    // Informations calculées (lecture seule)
    private Integer nombreInscrits;
    private Integer nombrePresents;
    private Integer nombreAbsents;
    private Boolean placesDisponibles;
    
    // Constructeurs
    public EntrainementDto() {}
    
    public EntrainementDto(Entrainement entrainement) {
        this.id = entrainement.getId();
        this.titre = entrainement.getTitre();
        this.description = entrainement.getDescription();
        this.date = entrainement.getDate();
        this.heureDebut = entrainement.getHeureDebut();
        this.heureFin = entrainement.getHeureFin();
        this.type = entrainement.getType();
        this.lieu = entrainement.getLieu();
        this.coachId = entrainement.getCoachId();
        this.nombreMaxParticipants = entrainement.getNombreMaxParticipants();
        this.statut = entrainement.getStatut();
        this.notes = entrainement.getNotes();
        
        // Calculs basés sur les participations
        if (entrainement.getParticipations() != null) {
            this.nombreInscrits = entrainement.getParticipations().size();
            this.nombrePresents = (int) entrainement.getParticipations().stream()
                .filter(p -> p.getStatut() == com.sprintbot.planningperformance.entity.Participation.StatutParticipation.PRESENT)
                .count();
            this.nombreAbsents = (int) entrainement.getParticipations().stream()
                .filter(p -> p.getStatut() == com.sprintbot.planningperformance.entity.Participation.StatutParticipation.ABSENT)
                .count();
            this.placesDisponibles = this.nombreInscrits < this.nombreMaxParticipants;
        }
    }
    
    // Méthode pour convertir vers l'entité
    public Entrainement toEntity() {
        Entrainement entrainement = new Entrainement();
        entrainement.setId(this.id);
        entrainement.setTitre(this.titre);
        entrainement.setDescription(this.description);
        entrainement.setDateEntrainement(this.date);
        entrainement.setHeureDebut(this.heureDebut);
        entrainement.setHeureFin(this.heureFin);
        entrainement.setTypeEntrainement(this.type);
        entrainement.setLieu(this.lieu);
        entrainement.setCoachId(this.coachId);
        entrainement.setNombreMaxParticipants(this.nombreMaxParticipants);
        entrainement.setStatut(this.statut != null ? this.statut : Entrainement.StatutEntrainement.PLANIFIE);
        entrainement.setNotes(this.notes);
        return entrainement;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    
    public Entrainement.TypeEntrainement getType() { return type; }
    public void setType(Entrainement.TypeEntrainement type) { this.type = type; }
    
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    
    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
    
    public Integer getNombreMaxParticipants() { return nombreMaxParticipants; }
    public void setNombreMaxParticipants(Integer nombreMaxParticipants) { this.nombreMaxParticipants = nombreMaxParticipants; }
    
    public Entrainement.StatutEntrainement getStatut() { return statut; }
    public void setStatut(Entrainement.StatutEntrainement statut) { this.statut = statut; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Integer getNombreInscrits() { return nombreInscrits; }
    public void setNombreInscrits(Integer nombreInscrits) { this.nombreInscrits = nombreInscrits; }
    
    public Integer getNombrePresents() { return nombrePresents; }
    public void setNombrePresents(Integer nombrePresents) { this.nombrePresents = nombrePresents; }
    
    public Integer getNombreAbsents() { return nombreAbsents; }
    public void setNombreAbsents(Integer nombreAbsents) { this.nombreAbsents = nombreAbsents; }
    
    public Boolean getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(Boolean placesDisponibles) { this.placesDisponibles = placesDisponibles; }
}
