package com.sprintbot.authuser.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entité représentant un administrateur du système
 * Hérite de Utilisateur avec des propriétés spécifiques aux administrateurs
 */
@Entity
@Table(name = "administrateurs")
@DiscriminatorValue("ADMINISTRATEUR")
public class Administrateur extends Utilisateur {

    @Size(max = 100, message = "Le département ne peut pas dépasser 100 caractères")
    @Column(name = "departement", length = 100)
    private String departement;

    @Size(max = 50, message = "Le niveau d'accès ne peut pas dépasser 50 caractères")
    @Column(name = "niveau_acces", length = 50)
    private String niveauAcces; // SUPER_ADMIN, ADMIN, MODERATEUR

    @Column(name = "date_nomination")
    private LocalDate dateNomination;

    @Size(max = 200, message = "Les permissions ne peuvent pas dépasser 200 caractères")
    @Column(name = "permissions", length = 200)
    private String permissions; // JSON ou liste séparée par virgules

    @Column(name = "peut_gerer_utilisateurs")
    private Boolean peutGererUtilisateurs = true;

    @Column(name = "peut_gerer_finances")
    private Boolean peutGererFinances = false;

    @Column(name = "peut_gerer_planning")
    private Boolean peutGererPlanning = true;

    @Column(name = "peut_voir_rapports")
    private Boolean peutVoirRapports = true;

    @Column(name = "peut_modifier_systeme")
    private Boolean peutModifierSysteme = false;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "derniere_action")
    private LocalDate derniereAction;

    // Constructeurs
    public Administrateur() {
        super();
        this.setRole("ADMINISTRATEUR");
        this.niveauAcces = "ADMIN";
        this.dateNomination = LocalDate.now();
        this.peutGererUtilisateurs = true;
        this.peutGererPlanning = true;
        this.peutVoirRapports = true;
    }

    public Administrateur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("ADMINISTRATEUR");
        this.niveauAcces = "ADMIN";
        this.dateNomination = LocalDate.now();
        this.peutGererUtilisateurs = true;
        this.peutGererPlanning = true;
        this.peutVoirRapports = true;
    }

    // Méthodes métier spécifiques aux administrateurs
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(niveauAcces);
    }

    public boolean isAdmin() {
        return "ADMIN".equals(niveauAcces);
    }

    public boolean isModerator() {
        return "MODERATEUR".equals(niveauAcces);
    }

    public boolean peutAccederAux(String ressource) {
        if (!isActif()) return false;
        
        switch (ressource.toUpperCase()) {
            case "UTILISATEURS":
                return peutGererUtilisateurs != null && peutGererUtilisateurs;
            case "FINANCES":
                return peutGererFinances != null && peutGererFinances;
            case "PLANNING":
                return peutGererPlanning != null && peutGererPlanning;
            case "RAPPORTS":
                return peutVoirRapports != null && peutVoirRapports;
            case "SYSTEME":
                return peutModifierSysteme != null && peutModifierSysteme;
            default:
                return false;
        }
    }

    public void promouvoirSuperAdmin() {
        this.niveauAcces = "SUPER_ADMIN";
        this.peutGererUtilisateurs = true;
        this.peutGererFinances = true;
        this.peutGererPlanning = true;
        this.peutVoirRapports = true;
        this.peutModifierSysteme = true;
    }

    public void retrograderModerator() {
        this.niveauAcces = "MODERATEUR";
        this.peutGererFinances = false;
        this.peutModifierSysteme = false;
    }

    public void enregistrerAction() {
        this.derniereAction = LocalDate.now();
    }

    // Getters et Setters
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getNiveauAcces() { return niveauAcces; }
    public void setNiveauAcces(String niveauAcces) { this.niveauAcces = niveauAcces; }

    public LocalDate getDateNomination() { return dateNomination; }
    public void setDateNomination(LocalDate dateNomination) { this.dateNomination = dateNomination; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }

    public Boolean getPeutGererUtilisateurs() { return peutGererUtilisateurs; }
    public void setPeutGererUtilisateurs(Boolean peutGererUtilisateurs) { this.peutGererUtilisateurs = peutGererUtilisateurs; }

    public Boolean getPeutGererFinances() { return peutGererFinances; }
    public void setPeutGererFinances(Boolean peutGererFinances) { this.peutGererFinances = peutGererFinances; }

    public Boolean getPeutGererPlanning() { return peutGererPlanning; }
    public void setPeutGererPlanning(Boolean peutGererPlanning) { this.peutGererPlanning = peutGererPlanning; }

    public Boolean getPeutVoirRapports() { return peutVoirRapports; }
    public void setPeutVoirRapports(Boolean peutVoirRapports) { this.peutVoirRapports = peutVoirRapports; }

    public Boolean getPeutModifierSysteme() { return peutModifierSysteme; }
    public void setPeutModifierSysteme(Boolean peutModifierSysteme) { this.peutModifierSysteme = peutModifierSysteme; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getDerniereAction() { return derniereAction; }
    public void setDerniereAction(LocalDate derniereAction) { this.derniereAction = derniereAction; }

    @Override
    public String toString() {
        return "Administrateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", niveauAcces='" + niveauAcces + '\'' +
                ", departement='" + departement + '\'' +
                '}';
    }
}
