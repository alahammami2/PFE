package com.sprintbot.authuser.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entité représentant un joueur de volley-ball
 * Hérite de Utilisateur avec des propriétés spécifiques aux joueurs
 */
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

    @Size(max = 50, message = "Le poste ne peut pas dépasser 50 caractères")
    @Column(name = "poste", length = 50)
    private String poste;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Size(max = 50, message = "Le statut ne peut pas dépasser 50 caractères")
    @Column(name = "statut", length = 50)
    private String statut;

    @Column(name = "numero_maillot", unique = true)
    private Integer numeroMaillot;

    @Size(max = 100, message = "La nationalité ne peut pas dépasser 100 caractères")
    @Column(name = "nationalite", length = 100)
    private String nationalite;

    @Column(name = "experience_annees")
    private Integer experienceAnnees;

    @Size(max = 20, message = "Le niveau ne peut pas dépasser 20 caractères")
    @Column(name = "niveau", length = 20)
    private String niveau; // DEBUTANT, INTERMEDIAIRE, AVANCE, PROFESSIONNEL

    @Column(name = "main_dominante")
    private String mainDominante; // DROITE, GAUCHE, AMBIDEXTRE

    // Constructeurs
    public Joueur() {
        super();
        this.setRole("JOUEUR");
        this.statut = "ACTIF";
        this.niveau = "DEBUTANT";
    }

    public Joueur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("JOUEUR");
        this.statut = "ACTIF";
        this.niveau = "DEBUTANT";
    }

    // Méthodes métier spécifiques aux joueurs
    public int getAge() {
        if (dateNaissance != null) {
            return LocalDate.now().getYear() - dateNaissance.getYear();
        }
        return 0;
    }

    public float getIMC() {
        if (taille != null && poids != null && taille > 0) {
            return poids / (taille * taille);
        }
        return 0;
    }

    public boolean isDisponible() {
        return "ACTIF".equals(statut) && isActif();
    }

    public void blesser() {
        this.statut = "BLESSE";
    }

    public void guerir() {
        this.statut = "ACTIF";
    }

    public void suspendre() {
        this.statut = "SUSPENDU";
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

    public Integer getNumeroMaillot() { return numeroMaillot; }
    public void setNumeroMaillot(Integer numeroMaillot) { this.numeroMaillot = numeroMaillot; }

    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }

    public Integer getExperienceAnnees() { return experienceAnnees; }
    public void setExperienceAnnees(Integer experienceAnnees) { this.experienceAnnees = experienceAnnees; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getMainDominante() { return mainDominante; }
    public void setMainDominante(String mainDominante) { this.mainDominante = mainDominante; }

    @Override
    public String toString() {
        return "Joueur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", poste='" + poste + '\'' +
                ", numeroMaillot=" + numeroMaillot +
                ", statut='" + statut + '\'' +
                '}';
    }
}
