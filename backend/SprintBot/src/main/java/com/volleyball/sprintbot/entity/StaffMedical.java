package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff_medical")
@DiscriminatorValue("STAFF_MEDICAL")
public class StaffMedical extends Utilisateur {

    @Size(max = 100)
    @Column(name = "specialite", length = 100)
    private String specialite;

    @Size(max = 50)
    @Column(name = "numero_licence", length = 50)
    private String numeroLicence;

    // Relations
    @OneToMany(mappedBy = "staffMedical", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonneesSante> donneesSanteGerees = new ArrayList<>();

    // Constructeurs
    public StaffMedical() {
        super();
        this.setRole("STAFF_MEDICAL");
    }

    public StaffMedical(String nom, String prenom, String email, String motDePasse, String specialite) {
        super(nom, prenom, email, motDePasse);
        this.specialite = specialite;
        this.setRole("STAFF_MEDICAL");
    }

    // Méthodes métier
    public void faireDemandeAdministrative() {
        // Logique pour faire une demande administrative
    }

    public void accederPlanning() {
        // Logique pour accéder au planning
    }

    public void gererRendezVousMedicaux() {
        // Logique pour gérer les rendez-vous médicaux
    }

    public void consulterDossiersMedicaux() {
        // Logique pour consulter les dossiers médicaux
    }

    public void ajouterDonneesSante(DonneesSante donneesSante) {
        this.donneesSanteGerees.add(donneesSante);
        donneesSante.setStaffMedical(this);
    }

    public void retirerDonneesSante(DonneesSante donneesSante) {
        this.donneesSanteGerees.remove(donneesSante);
        donneesSante.setStaffMedical(null);
    }

    // Getters et Setters
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getNumeroLicence() { return numeroLicence; }
    public void setNumeroLicence(String numeroLicence) { this.numeroLicence = numeroLicence; }

    public List<DonneesSante> getDonneesSanteGerees() { return donneesSanteGerees; }
    public void setDonneesSanteGerees(List<DonneesSante> donneesSanteGerees) {
        this.donneesSanteGerees = donneesSanteGerees;
    }
}
