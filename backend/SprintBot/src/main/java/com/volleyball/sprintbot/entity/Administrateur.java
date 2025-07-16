package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administrateurs")
@DiscriminatorValue("ADMINISTRATEUR")
public class Administrateur extends Utilisateur {

    // Relations
    @OneToMany(mappedBy = "administrateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DemandeAdministrative> demandesAdministratives = new ArrayList<>();

    // Constructeurs
    public Administrateur() {
        super();
        this.setRole("ADMINISTRATEUR");
    }

    public Administrateur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("ADMINISTRATEUR");
    }

    // Méthodes métier
    public void gererUtilisateurs() {
        // Logique pour gérer les utilisateurs
    }

    public void consulterAbsences() {
        // Logique pour consulter les absences
    }

    public void consulterPerformances() {
        // Logique pour consulter les performances
    }

    public void accederPlanning() {
        // Logique pour accéder au planning
    }

    public void consulterFinances() {
        // Logique pour consulter les finances
    }

    public void gererDemandesAdministratives() {
        // Logique pour gérer les demandes administratives
    }

    public void parametrerDroitsAcces() {
        // Logique pour paramétrer les droits d'accès
    }

    public void validerDemandesAdministratives() {
        // Logique pour valider les demandes administratives
    }

    public void ajouterDemandeAdministrative(DemandeAdministrative demande) {
        this.demandesAdministratives.add(demande);
        demande.setAdministrateur(this);
    }

    // Getters et Setters
    public List<DemandeAdministrative> getDemandesAdministratives() { 
        return demandesAdministratives; 
    }
    
    public void setDemandesAdministratives(List<DemandeAdministrative> demandesAdministratives) { 
        this.demandesAdministratives = demandesAdministratives; 
    }
}
