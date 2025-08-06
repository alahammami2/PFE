package com.sprintbot.planningperformance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "entrainements", schema = "planning_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrainement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La date d'entraînement est obligatoire")
    @Column(name = "date_entrainement", nullable = false)
    private LocalDate dateEntrainement;

    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Size(max = 255, message = "Le lieu ne peut pas dépasser 255 caractères")
    private String lieu;

    @NotNull(message = "Le type d'entraînement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_entrainement", nullable = false)
    private TypeEntrainement typeEntrainement;

    @Min(value = 1, message = "Le niveau d'intensité doit être entre 1 et 10")
    @Max(value = 10, message = "Le niveau d'intensité doit être entre 1 et 10")
    @Column(name = "niveau_intensite")
    private Integer niveauIntensite;

    @NotNull(message = "L'ID du coach est obligatoire")
    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'PLANIFIE'")
    private StatutEntrainement statut = StatutEntrainement.PLANIFIE;

    @Column(columnDefinition = "TEXT")
    private String objectifs;

    @Column(name = "materiel_requis", columnDefinition = "TEXT")
    private String materielRequis;

    @Column(name = "nombre_max_joueurs")
    private Integer nombreMaxJoueurs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "entrainement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Participation> participations;

    @OneToMany(mappedBy = "entrainement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Performance> performances;

    @OneToMany(mappedBy = "entrainement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Absence> absences;



    // Méthodes de cycle de vie
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes d'alias pour compatibilité avec DTO
    public LocalDate getDate() { return dateEntrainement; }
    public void setDate(LocalDate date) { this.dateEntrainement = date; }

    public TypeEntrainement getType() { return typeEntrainement; }
    public void setType(TypeEntrainement type) { this.typeEntrainement = type; }

    public Integer getNombreMaxParticipants() { return nombreMaxJoueurs; }
    public void setNombreMaxParticipants(Integer nombreMaxParticipants) { this.nombreMaxJoueurs = nombreMaxParticipants; }

    public String getNotes() { return objectifs; }
    public void setNotes(String notes) { this.objectifs = notes; }

    // Enums
    public enum TypeEntrainement {
        PHYSIQUE, TECHNIQUE, TACTIQUE, MATCH
    }

    public enum StatutEntrainement {
        PLANIFIE, EN_COURS, TERMINE, ANNULE
    }
}
