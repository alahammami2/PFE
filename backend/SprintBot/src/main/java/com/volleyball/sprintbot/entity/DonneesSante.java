package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "donnees_sante")
public class DonneesSante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID des données santé est obligatoire")
    @Size(max = 100)
    @Column(name = "id_sante", length = 100)
    private String idSante;

    @Column(name = "bilan", columnDefinition = "TEXT")
    private String bilan;

    @Column(name = "blessures", columnDefinition = "TEXT")
    private String blessures;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "id_medical", length = 100)
    private String idMedical;

    @Column(name = "id_joueur", length = 100)
    private String idJoueur;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_medical_id")
    private StaffMedical staffMedical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    // Constructeurs
    public DonneesSante() {
        this.date = LocalDate.now();
    }

    public DonneesSante(String idSante, String bilan, Joueur joueur, StaffMedical staffMedical) {
        this();
        this.idSante = idSante;
        this.bilan = bilan;
        this.joueur = joueur;
        this.staffMedical = staffMedical;
    }

    // Méthodes métier
    public void consulter() {
        // Logique pour consulter les données santé
    }

    public void mettreAJour() {
        // Logique pour mettre à jour les données santé
    }

    public void ajouterBlessure(String nouvelleBlessure) {
        if (this.blessures == null || this.blessures.isEmpty()) {
            this.blessures = nouvelleBlessure;
        } else {
            this.blessures += "; " + nouvelleBlessure;
        }
    }

    public boolean aBlessures() {
        return this.blessures != null && !this.blessures.trim().isEmpty();
    }

    public boolean estRecente() {
        return this.date.isAfter(LocalDate.now().minusDays(30));
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdSante() { return idSante; }
    public void setIdSante(String idSante) { this.idSante = idSante; }

    public String getBilan() { return bilan; }
    public void setBilan(String bilan) { this.bilan = bilan; }

    public String getBlessures() { return blessures; }
    public void setBlessures(String blessures) { this.blessures = blessures; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getIdMedical() { return idMedical; }
    public void setIdMedical(String idMedical) { this.idMedical = idMedical; }

    public String getIdJoueur() { return idJoueur; }
    public void setIdJoueur(String idJoueur) { this.idJoueur = idJoueur; }

    public StaffMedical getStaffMedical() { return staffMedical; }
    public void setStaffMedical(StaffMedical staffMedical) { this.staffMedical = staffMedical; }

    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }
}

