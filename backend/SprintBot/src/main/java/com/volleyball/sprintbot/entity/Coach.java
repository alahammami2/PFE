package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coaches")
@DiscriminatorValue("COACH")
public class Coach extends Utilisateur {

    @Size(max = 100)
    @Column(name = "specialite", length = 100)
    private String specialite;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Planning> plannings = new ArrayList<>();

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Performance> performancesGerees = new ArrayList<>();

    // Constructeurs
    public Coach() {
        super();
        this.setRole("COACH");
    }

    public Coach(String nom, String prenom, String email, String motDePasse, String specialite) {
        super(nom, prenom, email, motDePasse);
        this.specialite = specialite;
        this.setRole("COACH");
    }

    // Méthodes métier
    public void planifierEntrainements() {
        // Logique pour planifier les entraînements
    }

    public void consulterPerformances() {
        // Logique pour consulter les performances
    }

    public void lancerPlanificationOptionnelle() {
        // Logique pour lancer une planification optionnelle
    }

    public void consulterDonneesSante() {
        // Logique pour consulter les données de santé
    }

    public void gererPerformances() {
        // Logique pour gérer les performances
    }

    public void verifierDisponibilites() {
        // Logique pour vérifier les disponibilités
    }

    public void ajouterPlanning(Planning planning) {
        this.plannings.add(planning);
        planning.setCoach(this);
    }

    public void ajouterPerformanceGeree(Performance performance) {
        this.performancesGerees.add(performance);
        performance.setCoach(this);
    }

    // Getters et Setters
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public List<Planning> getPlannings() { return plannings; }
    public void setPlannings(List<Planning> plannings) { this.plannings = plannings; }

    public List<Performance> getPerformancesGerees() { return performancesGerees; }
    public void setPerformancesGerees(List<Performance> performancesGerees) { this.performancesGerees = performancesGerees; }
}
