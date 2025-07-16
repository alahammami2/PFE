package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {

    @Id
    @Column(name = "id_rendez_vous", length = 100)
    private String idRendezVous;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "heure_anterieure")
    private LocalTime heureAnterieure;

    @Column(name = "lieu", length = 200)
    private String lieu;

    @Column(name = "id_medical", length = 100)
    private String idMedical;

    @Column(name = "id_joueur", length = 100)
    private String idJoueur;

    @Column(name = "statut", length = 50)
    private String statut = "EN_ATTENTE";

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_medical_id")
    private StaffMedical staffMedical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    // Constructeurs
    public RendezVous() {}

    public RendezVous(String idRendezVous, LocalDate date, LocalTime heureAnterieure, String lieu, String idMedical, String idJoueur) {
        this.idRendezVous = idRendezVous;
        this.date = date;
        this.heureAnterieure = heureAnterieure;
        this.lieu = lieu;
        this.idMedical = idMedical;
        this.idJoueur = idJoueur;
    }

    // Méthodes métier
    public void planifier() {
        this.statut = "PLANIFIE";
    }

    public void confirmer() {
        this.statut = "CONFIRME";
    }

    public void annuler() {
        this.statut = "ANNULE";
    }

    public void terminer() {
        this.statut = "TERMINE";
    }

    public boolean estPasse() {
        return this.date.isBefore(LocalDate.now()) || 
               (this.date.equals(LocalDate.now()) && 
                this.heureAnterieure != null && 
                this.heureAnterieure.isBefore(LocalTime.now()));
    }

    public boolean estAujourdhui() {
        return this.date.equals(LocalDate.now());
    }

    // Getters et Setters
    public String getIdRendezVous() { return idRendezVous; }
    public void setIdRendezVous(String idRendezVous) { this.idRendezVous = idRendezVous; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureAnterieure() { return heureAnterieure; }
    public void setHeureAnterieure(LocalTime heureAnterieure) { this.heureAnterieure = heureAnterieure; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getIdMedical() { return idMedical; }
    public void setIdMedical(String idMedical) { this.idMedical = idMedical; }

    public String getIdJoueur() { return idJoueur; }
    public void setIdJoueur(String idJoueur) { this.idJoueur = idJoueur; }

    public StaffMedical getStaffMedical() { return staffMedical; }
    public void setStaffMedical(StaffMedical staffMedical) { this.staffMedical = staffMedical; }

    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}

