package com.sprintbot.planningperformance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "participations", schema = "planning_performance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"entrainement_id", "joueur_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participation {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_participation", columnDefinition = "VARCHAR(20) DEFAULT 'INSCRIT'")
    private StatutParticipation statutParticipation = StatutParticipation.INSCRIT;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    @Column(columnDefinition = "TEXT")
    private String commentaire;



    // Méthodes de cycle de vie
    @PrePersist
    protected void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
    }

    // Constructeur pour le service
    public Participation(Entrainement entrainement, Long joueurId) {
        this.entrainement = entrainement;
        this.joueurId = joueurId;
        this.statutParticipation = StatutParticipation.INSCRIT;
        this.dateInscription = LocalDateTime.now();
    }

    // Méthodes d'alias pour compatibilité avec DTO
    public StatutParticipation getStatut() { return statutParticipation; }
    public void setStatut(StatutParticipation statut) { this.statutParticipation = statut; }

    // Enum
    public enum StatutParticipation {
        INSCRIT, PRESENT, ABSENT, EXCUSE
    }
}
