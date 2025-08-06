package com.sprintbot.authuser.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entité représentant un entraîneur/coach
 * Hérite de Utilisateur avec des propriétés spécifiques aux coaches
 */
@Entity
@Table(name = "coaches")
@DiscriminatorValue("COACH")
public class Coach extends Utilisateur {

    @Size(max = 100, message = "La spécialité ne peut pas dépasser 100 caractères")
    @Column(name = "specialite", length = 100)
    private String specialite;

    @Column(name = "experience")
    private Integer experience; // Années d'expérience

    @Size(max = 50, message = "Le niveau de certification ne peut pas dépasser 50 caractères")
    @Column(name = "certification", length = 50)
    private String certification;

    @Size(max = 100, message = "La formation ne peut pas dépasser 100 caractères")
    @Column(name = "formation", length = 100)
    private String formation;

    @Column(name = "date_debut_carriere")
    private LocalDate dateDebutCarriere;

    @Size(max = 20, message = "Le type de coach ne peut pas dépasser 20 caractères")
    @Column(name = "type_coach", length = 20)
    private String typeCoach; // PRINCIPAL, ASSISTANT, SPECIALISE

    @Column(name = "salaire")
    private Double salaire;

    @Size(max = 500, message = "La biographie ne peut pas dépasser 500 caractères")
    @Column(name = "biographie", length = 500)
    private String biographie;

    @Column(name = "actif_coaching")
    private Boolean actifCoaching = true;

    // Constructeurs
    public Coach() {
        super();
        this.setRole("COACH");
        this.typeCoach = "ASSISTANT";
        this.actifCoaching = true;
    }

    public Coach(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("COACH");
        this.typeCoach = "ASSISTANT";
        this.actifCoaching = true;
    }

    public Coach(String nom, String prenom, String email, String motDePasse, String specialite) {
        this(nom, prenom, email, motDePasse);
        this.specialite = specialite;
    }

    // Méthodes métier spécifiques aux coaches
    public int getAnneesExperience() {
        if (dateDebutCarriere != null) {
            return LocalDate.now().getYear() - dateDebutCarriere.getYear();
        }
        return experience != null ? experience : 0;
    }

    public boolean isCoachPrincipal() {
        return "PRINCIPAL".equals(typeCoach);
    }

    public boolean isCoachAssistant() {
        return "ASSISTANT".equals(typeCoach);
    }

    public boolean isCoachSpecialise() {
        return "SPECIALISE".equals(typeCoach);
    }

    public boolean isDisponiblePourCoaching() {
        return actifCoaching != null && actifCoaching && isActif();
    }

    public void promouvoirCoachPrincipal() {
        this.typeCoach = "PRINCIPAL";
    }

    public void retrograderCoachAssistant() {
        this.typeCoach = "ASSISTANT";
    }

    public void suspendreCoaching() {
        this.actifCoaching = false;
    }

    public void reprendreCoaching() {
        this.actifCoaching = true;
    }

    // Getters et Setters
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }

    public LocalDate getDateDebutCarriere() { return dateDebutCarriere; }
    public void setDateDebutCarriere(LocalDate dateDebutCarriere) { this.dateDebutCarriere = dateDebutCarriere; }

    public String getTypeCoach() { return typeCoach; }
    public void setTypeCoach(String typeCoach) { this.typeCoach = typeCoach; }

    public Double getSalaire() { return salaire; }
    public void setSalaire(Double salaire) { this.salaire = salaire; }

    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) { this.biographie = biographie; }

    public Boolean getActifCoaching() { return actifCoaching; }
    public void setActifCoaching(Boolean actifCoaching) { this.actifCoaching = actifCoaching; }

    @Override
    public String toString() {
        return "Coach{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", specialite='" + specialite + '\'' +
                ", typeCoach='" + typeCoach + '\'' +
                ", experience=" + experience +
                '}';
    }
}
