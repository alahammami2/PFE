package com.sprintbot.planningperformance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "absences", schema = "planning_performance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"entrainement_id", "joueur_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrainement_id", nullable = false)
    @NotNull(message = "L'entraînement est obligatoire")
    private Entrainement entrainement;

    @NotNull(message = "L'ID du joueur est obligatoire")
    @Column(name = "joueur_id", nullable = false)
    private Long joueurId;

    @NotNull(message = "Le motif est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotifAbsence motif;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean justifiee = false;

    @Size(max = 500, message = "L'URL du justificatif ne peut pas dépasser 500 caractères")
    @Column(name = "justificatif_url")
    private String justificatifUrl;

    @Column(name = "date_declaration")
    private LocalDateTime dateDeclaration;

    @NotNull(message = "L'ID du déclarant est obligatoire")
    @Column(name = "declarant_id", nullable = false)
    private Long declarantId;



    // Méthodes de cycle de vie
    @PrePersist
    protected void onCreate() {
        if (dateDeclaration == null) {
            dateDeclaration = LocalDateTime.now();
        }
        if (justifiee == null) {
            justifiee = false;
        }
    }

    // Méthodes utilitaires
    public boolean estJustifiee() {
        return justifiee != null && justifiee;
    }

    public boolean aJustificatif() {
        return justificatifUrl != null && !justificatifUrl.trim().isEmpty();
    }



    // Enum
    public enum MotifAbsence {
        MALADIE, BLESSURE, PERSONNEL, PROFESSIONNEL, AUTRE
    }
}
