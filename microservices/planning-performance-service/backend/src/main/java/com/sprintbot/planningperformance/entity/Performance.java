package com.sprintbot.planningperformance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "performances", schema = "planning_performance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"entrainement_id", "joueur_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Performance {

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

    @DecimalMin(value = "0.0", message = "La note globale doit être entre 0 et 10")
    @DecimalMax(value = "10.0", message = "La note globale doit être entre 0 et 10")
    @Column(name = "note_globale", precision = 3, scale = 1)
    private BigDecimal noteGlobale;

    @DecimalMin(value = "0.0", message = "La note technique doit être entre 0 et 10")
    @DecimalMax(value = "10.0", message = "La note technique doit être entre 0 et 10")
    @Column(name = "note_technique", precision = 3, scale = 1)
    private BigDecimal noteTechnique;

    @DecimalMin(value = "0.0", message = "La note physique doit être entre 0 et 10")
    @DecimalMax(value = "10.0", message = "La note physique doit être entre 0 et 10")
    @Column(name = "note_physique", precision = 3, scale = 1)
    private BigDecimal notePhysique;

    @DecimalMin(value = "0.0", message = "La note mental doit être entre 0 et 10")
    @DecimalMax(value = "10.0", message = "La note mental doit être entre 0 et 10")
    @Column(name = "note_mental", precision = 3, scale = 1)
    private BigDecimal noteMental;

    @Column(name = "commentaire_coach", columnDefinition = "TEXT")
    private String commentaireCoach;

    @DecimalMin(value = "0.0", message = "L'auto-évaluation doit être entre 0 et 10")
    @DecimalMax(value = "10.0", message = "L'auto-évaluation doit être entre 0 et 10")
    @Column(name = "auto_evaluation", precision = 3, scale = 1)
    private BigDecimal autoEvaluation;

    @Column(name = "commentaire_joueur", columnDefinition = "TEXT")
    private String commentaireJoueur;

    @Column(name = "objectifs_atteints", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean objectifsAtteints = false;

    @Column(name = "points_forts", columnDefinition = "TEXT")
    private String pointsForts;

    @Column(name = "points_amelioration", columnDefinition = "TEXT")
    private String pointsAmelioration;

    @NotNull(message = "L'ID de l'évaluateur est obligatoire")
    @Column(name = "evaluateur_id", nullable = false)
    private Long evaluateurId;

    @Column(name = "date_evaluation")
    private LocalDateTime dateEvaluation;



    // Méthodes de cycle de vie
    @PrePersist
    protected void onCreate() {
        if (dateEvaluation == null) {
            dateEvaluation = LocalDateTime.now();
        }
        if (objectifsAtteints == null) {
            objectifsAtteints = false;
        }
    }

    // Méthodes utilitaires
    public BigDecimal calculerMoyenne() {
        if (noteTechnique == null || notePhysique == null || noteMental == null) {
            return null;
        }
        return noteTechnique.add(notePhysique).add(noteMental)
                .divide(BigDecimal.valueOf(3), 1, java.math.RoundingMode.HALF_UP);
    }


}
