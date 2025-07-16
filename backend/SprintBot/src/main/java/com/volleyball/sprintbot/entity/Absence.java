package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID de l'absence est obligatoire")
    @Size(max = 100)
    @Column(name = "id_absence", length = 100)
    private String idAbsence;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "statut", length = 50)
    private String statut;

    @Column(name = "id_joueur", length = 100)
    private String idJoueur;

    @Column(name = "raison", columnDefinition = "TEXT")
    private String raison;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    // Constructeurs
    public Absence() {}

    public Absence(String idAbsence, LocalDate dateDebut, LocalDate dateFin, String statut, Joueur joueur) {
        this.idAbsence = idAbsence;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.joueur = joueur;
    }

    // Méthodes métier
    public void ajouter() {
        // Logique pour ajouter une absence
    }

    public void consulter() {
        // Logique pour consulter une absence
    }

    public void valider() {
        this.statut = "VALIDEE";
    }

    public void rejeter() {
        this.statut = "REJETEE";
    }

    public boolean estEnCours() {
        LocalDate aujourd_hui = LocalDate.now();
        return !aujourd_hui.isBefore(dateDebut) && 
               (dateFin == null || !aujourd_hui.isAfter(dateFin));
    }

    public long getDureeEnJours() {
        if (dateFin != null) {
            return dateDebut.until(dateFin).getDays() + 1;
        }
        return 1; // Absence d'une journée si pas de date de fin
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdAbsence() { return idAbsence; }
    public void setIdAbsence(String idAbsence) { this.idAbsence = idAbsence; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }

    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }
}

