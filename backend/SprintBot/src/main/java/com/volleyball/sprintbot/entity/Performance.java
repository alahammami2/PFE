package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "performances")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID de la performance est obligatoire")
    @Size(max = 100)
    @Column(name = "id_performance", length = 100)
    private String idPerformance;

    @Column(name = "match_joues")
    private Integer matchJoues;

    @Column(name = "note")
    private Integer note;

    @Column(name = "id_coach", length = 100)
    private String idCoach;

    @Column(name = "id_equipe", length = 100)
    private String idEquipe;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @OneToOne(mappedBy = "performance", fetch = FetchType.LAZY)
    private Evenement evenement;

    // Constructeurs
    public Performance() {}

    public Performance(String idPerformance, Integer matchJoues, Integer note, Coach coach) {
        this.idPerformance = idPerformance;
        this.matchJoues = matchJoues;
        this.note = note;
        this.coach = coach;
    }

    // Méthodes métier
    public void consulter() {
        // Logique pour consulter les performances
    }

    public void filtrer() {
        // Logique pour filtrer les performances
    }

    public void mettreAJour() {
        // Logique pour mettre à jour les performances
    }

    public void calculerMoyenne() {
        // Logique pour calculer la moyenne des performances
    }

    public boolean estBonnePerformance() {
        return this.note != null && this.note >= 7;
    }

    public String getEvaluationTextuelle() {
        if (this.note == null) return "Non évalué";
        if (this.note >= 9) return "Excellent";
        if (this.note >= 7) return "Bien";
        if (this.note >= 5) return "Moyen";
        return "À améliorer";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdPerformance() { return idPerformance; }
    public void setIdPerformance(String idPerformance) { this.idPerformance = idPerformance; }

    public Integer getMatchJoues() { return matchJoues; }
    public void setMatchJoues(Integer matchJoues) { this.matchJoues = matchJoues; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }

    public Coach getCoach() { return coach; }
    public void setCoach(Coach coach) { this.coach = coach; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
}

