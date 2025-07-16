package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "joueurs")
@DiscriminatorValue("JOUEUR")
public class Joueur extends Utilisateur {

    @Column(name = "taille")
    @DecimalMin(value = "1.0", message = "La taille doit être supérieure à 1.0m")
    @DecimalMax(value = "2.5", message = "La taille doit être inférieure à 2.5m")
    private Float taille;

    @Column(name = "poids")
    @DecimalMin(value = "30.0", message = "Le poids doit être supérieur à 30kg")
    @DecimalMax(value = "200.0", message = "Le poids doit être inférieur à 200kg")
    private Float poids;

    @Size(max = 50)
    @Column(name = "poste", length = 50)
    private String poste;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Size(max = 50)
    @Column(name = "statut", length = 50)
    private String statut;

    @Size(max = 50)
    @Column(name = "bloc", length = 50)
    private String bloc;

    @Size(max = 20)
    @Column(name = "numero_maillot", length = 20)
    private String numeroMaillot;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @OneToMany(mappedBy = "joueur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Absence> absences = new ArrayList<>();

    @OneToMany(mappedBy = "joueur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Performance> performances = new ArrayList<>();

    @OneToMany(mappedBy = "joueur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonneesSante> donneesSante = new ArrayList<>();

    // Constructeurs
    public Joueur() {
        super();
        this.setRole("JOUEUR");
    }

    public Joueur(String nom, String prenom, String email, String motDePasse, String poste) {
        super(nom, prenom, email, motDePasse);
        this.poste = poste;
        this.setRole("JOUEUR");
    }

    // Méthodes métier
    public void faireDemandePerfomance() {
        // Logique pour faire une demande de performance
    }

    public void faireDemandeMedical() {
        // Logique pour faire une demande médicale
    }

    public void accederPlanning() {
        // Logique pour accéder au planning
    }

    public void consulterDonneesSante() {
        // Logique pour consulter les données de santé
    }

    public void ajouterAutorisation(String autorisation) {
        // Logique pour ajouter une autorisation
    }

    public void ajouterAbsence(Absence absence) {
        this.absences.add(absence);
        absence.setJoueur(this);
    }

    public void ajouterPerformance(Performance performance) {
        this.performances.add(performance);
        performance.setJoueur(this);
    }

    public int getAge() {
        if (this.dateNaissance != null) {
            return LocalDate.now().getYear() - this.dateNaissance.getYear();
        }
        return 0;
    }

    public double getIMC() {
        if (this.taille != null && this.poids != null && this.taille > 0) {
            return this.poids / (this.taille * this.taille);
        }
        return 0.0;
    }

    // Getters et Setters
    public Float getTaille() { return taille; }
    public void setTaille(Float taille) { this.taille = taille; }

    public Float getPoids() { return poids; }
    public void setPoids(Float poids) { this.poids = poids; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getBloc() { return bloc; }
    public void setBloc(String bloc) { this.bloc = bloc; }

    public String getNumeroMaillot() { return numeroMaillot; }
    public void setNumeroMaillot(String numeroMaillot) { this.numeroMaillot = numeroMaillot; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public List<Absence> getAbsences() { return absences; }
    public void setAbsences(List<Absence> absences) { this.absences = absences; }

    public List<Performance> getPerformances() { return performances; }
    public void setPerformances(List<Performance> performances) { this.performances = performances; }

    public List<DonneesSante> getDonneesSante() { return donneesSante; }
    public void setDonneesSante(List<DonneesSante> donneesSante) { this.donneesSante = donneesSante; }
}
