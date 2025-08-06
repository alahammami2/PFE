package com.sprintbot.planningperformance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "objectifs_individuels", schema = "planning_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectifIndividuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du joueur est obligatoire")
    @Column(name = "joueur_id", nullable = false)
    private Long joueurId;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le type d'objectif est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_objectif", nullable = false)
    private TypeObjectif typeObjectif;

    @NotNull(message = "La date de création est obligatoire")
    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'EN_COURS'")
    private StatutObjectif statut = StatutObjectif.EN_COURS;

    @Min(value = 0, message = "La progression doit être entre 0 et 100")
    @Max(value = 100, message = "La progression doit être entre 0 et 100")
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer progression = 0;

    @NotNull(message = "L'ID du coach est obligatoire")
    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "date_atteinte")
    private LocalDate dateAtteinte;

    @Column(name = "critere_reussite", columnDefinition = "TEXT")
    private String critereReussite;



    // Méthodes de cycle de vie
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (progression == null) {
            progression = 0;
        }
        if (statut == null) {
            statut = StatutObjectif.EN_COURS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public boolean estEchu() {
        return dateEcheance != null && LocalDate.now().isAfter(dateEcheance);
    }

    public boolean estAtteint() {
        return statut == StatutObjectif.ATTEINT;
    }

    public int getJoursRestants() {
        if (dateEcheance == null) {
            return -1; // Pas d'échéance
        }
        return (int) LocalDate.now().until(dateEcheance).getDays();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeObjectif getTypeObjectif() { return typeObjectif; }
    public void setTypeObjectif(TypeObjectif typeObjectif) { this.typeObjectif = typeObjectif; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }

    public StatutObjectif getStatut() { return statut; }
    public void setStatut(StatutObjectif statut) { this.statut = statut; }

    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDate getDateAtteinte() { return dateAtteinte; }
    public void setDateAtteinte(LocalDate dateAtteinte) { this.dateAtteinte = dateAtteinte; }

    public String getCritereReussite() { return critereReussite; }
    public void setCritereReussite(String critereReussite) { this.critereReussite = critereReussite; }

    // Méthodes d'alias pour compatibilité avec DTO
    public TypeObjectif getType() { return typeObjectif; }
    public void setType(TypeObjectif type) { this.typeObjectif = type; }

    public String getNotes() { return commentaires; }
    public void setNotes(String notes) { this.commentaires = notes; }

    public LocalDateTime getDateModification() { return updatedAt; }
    public void setDateModification(LocalDateTime dateModification) { this.updatedAt = dateModification; }

    // Enums
    public enum TypeObjectif {
        TECHNIQUE, PHYSIQUE, MENTAL, TACTIQUE
    }

    public enum StatutObjectif {
        EN_COURS, ATTEINT, ABANDONNE, REPORTE, ECHU
    }
}
