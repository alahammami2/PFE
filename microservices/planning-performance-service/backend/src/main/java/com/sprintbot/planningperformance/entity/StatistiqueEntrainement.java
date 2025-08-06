package com.sprintbot.planningperformance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistiques_entrainement", schema = "planning_performance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"joueur_id", "mois", "annee"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueEntrainement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du joueur est obligatoire")
    @Column(name = "joueur_id", nullable = false)
    private Long joueurId;

    @NotNull(message = "Le mois est obligatoire")
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    @Column(nullable = false)
    private Integer mois;

    @NotNull(message = "L'année est obligatoire")
    @Column(nullable = false)
    private Integer annee;

    @Column(name = "nombre_entrainements_planifies", columnDefinition = "INTEGER DEFAULT 0")
    private Integer nombreEntrainementsPlanifies = 0;

    @Column(name = "nombre_entrainements_presents", columnDefinition = "INTEGER DEFAULT 0")
    private Integer nombreEntrainementsPresents = 0;

    @Column(name = "nombre_absences", columnDefinition = "INTEGER DEFAULT 0")
    private Integer nombreAbsences = 0;

    @Column(name = "taux_presence", precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 0.0")
    private BigDecimal tauxPresence = BigDecimal.ZERO;

    @Column(name = "moyenne_performance", precision = 3, scale = 1, columnDefinition = "DECIMAL(3,1) DEFAULT 0.0")
    private BigDecimal moyennePerformance = BigDecimal.ZERO;

    @Column(name = "progression_mensuelle", precision = 3, scale = 1, columnDefinition = "DECIMAL(3,1) DEFAULT 0.0")
    private BigDecimal progressionMensuelle = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    // Méthodes de cycle de vie
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (nombreEntrainementsPlanifies == null) nombreEntrainementsPlanifies = 0;
        if (nombreEntrainementsPresents == null) nombreEntrainementsPresents = 0;
        if (nombreAbsences == null) nombreAbsences = 0;
        if (tauxPresence == null) tauxPresence = BigDecimal.ZERO;
        if (moyennePerformance == null) moyennePerformance = BigDecimal.ZERO;
        if (progressionMensuelle == null) progressionMensuelle = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public void calculerTauxPresence() {
        if (nombreEntrainementsPlanifies > 0) {
            tauxPresence = BigDecimal.valueOf(nombreEntrainementsPresents)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(nombreEntrainementsPlanifies), 2, java.math.RoundingMode.HALF_UP);
        } else {
            tauxPresence = BigDecimal.ZERO;
        }
    }

    public void incrementerEntrainementsPlanifies() {
        nombreEntrainementsPlanifies++;
    }

    public void incrementerEntrainementsPresents() {
        nombreEntrainementsPresents++;
        calculerTauxPresence();
    }

    public void incrementerAbsences() {
        nombreAbsences++;
    }

    public boolean estBonneAssiduité() {
        return tauxPresence.compareTo(BigDecimal.valueOf(80)) >= 0;
    }

    public boolean estEnProgression() {
        return progressionMensuelle.compareTo(BigDecimal.ZERO) > 0;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Integer getNombreEntrainementsPlanifies() { return nombreEntrainementsPlanifies; }
    public void setNombreEntrainementsPlanifies(Integer nombreEntrainementsPlanifies) { 
        this.nombreEntrainementsPlanifies = nombreEntrainementsPlanifies; 
    }

    public Integer getNombreEntrainementsPresents() { return nombreEntrainementsPresents; }
    public void setNombreEntrainementsPresents(Integer nombreEntrainementsPresents) { 
        this.nombreEntrainementsPresents = nombreEntrainementsPresents; 
    }

    public Integer getNombreAbsences() { return nombreAbsences; }
    public void setNombreAbsences(Integer nombreAbsences) { this.nombreAbsences = nombreAbsences; }

    public BigDecimal getTauxPresence() { return tauxPresence; }
    public void setTauxPresence(BigDecimal tauxPresence) { this.tauxPresence = tauxPresence; }

    public BigDecimal getMoyennePerformance() { return moyennePerformance; }
    public void setMoyennePerformance(BigDecimal moyennePerformance) { this.moyennePerformance = moyennePerformance; }

    public BigDecimal getProgressionMensuelle() { return progressionMensuelle; }
    public void setProgressionMensuelle(BigDecimal progressionMensuelle) { this.progressionMensuelle = progressionMensuelle; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Méthodes d'alias pour compatibilité avec DTO et Service
    public Integer getNombreEntrainements() { return nombreEntrainementsPlanifies; }
    public void setNombreEntrainements(Integer nombreEntrainements) { this.nombreEntrainementsPlanifies = nombreEntrainements; }

    public Integer getNombrePresences() { return nombreEntrainementsPresents; }
    public void setNombrePresences(Integer nombrePresences) { this.nombreEntrainementsPresents = nombrePresences; }

    public BigDecimal getPerformanceMoyenne() { return moyennePerformance; }
    public void setPerformanceMoyenne(BigDecimal performanceMoyenne) { this.moyennePerformance = performanceMoyenne; }

    public BigDecimal getNoteTechniqueMoyenne() { return BigDecimal.ZERO; } // Valeur par défaut
    public void setNoteTechniqueMoyenne(BigDecimal noteTechniqueMoyenne) { /* Non utilisé pour l'instant */ }

    public BigDecimal getNotePhysiqueMoyenne() { return BigDecimal.ZERO; } // Valeur par défaut
    public void setNotePhysiqueMoyenne(BigDecimal notePhysiqueMoyenne) { /* Non utilisé pour l'instant */ }

    public BigDecimal getNoteMentaleMoyenne() { return BigDecimal.ZERO; } // Valeur par défaut
    public void setNoteMentaleMoyenne(BigDecimal noteMentaleMoyenne) { /* Non utilisé pour l'instant */ }

    public Integer getNombreObjectifsAtteints() { return 0; } // Valeur par défaut
    public void setNombreObjectifsAtteints(Integer nombreObjectifsAtteints) { /* Non utilisé pour l'instant */ }

    public LocalDateTime getDateCalcul() { return updatedAt; }
    public void setDateCalcul(LocalDateTime dateCalcul) { this.updatedAt = dateCalcul; }
}
