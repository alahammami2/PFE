package com.sprintbot.authuser.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entité représentant un responsable financier
 * Hérite de Utilisateur avec des propriétés spécifiques à la gestion financière
 */
@Entity
@Table(name = "responsables_financiers")
@DiscriminatorValue("RESPONSABLE_FINANCIER")
public class ResponsableFinancier extends Utilisateur {

    @Size(max = 100, message = "Le département ne peut pas dépasser 100 caractères")
    @Column(name = "departement", length = 100)
    private String departement;

    @Size(max = 50, message = "Le niveau d'autorisation ne peut pas dépasser 50 caractères")
    @Column(name = "niveau_autorisation", length = 50)
    private String niveauAutorisation; // JUNIOR, SENIOR, DIRECTEUR

    @Column(name = "limite_approbation")
    private Double limiteApprobation; // Montant maximum qu'il peut approuver

    @Column(name = "peut_approuver_budgets")
    private Boolean peutApprouverBudgets = false;

    @Column(name = "peut_voir_salaires")
    private Boolean peutVoirSalaires = false;

    @Column(name = "peut_modifier_tarifs")
    private Boolean peutModifierTarifs = false;

    @Column(name = "peut_generer_rapports")
    private Boolean peutGenererRapports = true;

    @Size(max = 100, message = "La formation ne peut pas dépasser 100 caractères")
    @Column(name = "formation", length = 100)
    private String formation;

    @Size(max = 50, message = "La certification ne peut pas dépasser 50 caractères")
    @Column(name = "certification", length = 50)
    private String certification;

    @Column(name = "experience_annees")
    private Integer experienceAnnees;

    @Column(name = "date_nomination")
    private LocalDate dateNomination;

    @Column(name = "salaire")
    private Double salaire;

    @Size(max = 500, message = "Les responsabilités ne peuvent pas dépasser 500 caractères")
    @Column(name = "responsabilites", length = 500)
    private String responsabilites;

    @Column(name = "actif_financier")
    private Boolean actifFinancier = true;

    // Constructeurs
    public ResponsableFinancier() {
        super();
        this.setRole("RESPONSABLE_FINANCIER");
        this.niveauAutorisation = "JUNIOR";
        this.dateNomination = LocalDate.now();
        this.peutGenererRapports = true;
        this.actifFinancier = true;
        this.limiteApprobation = 1000.0; // Limite par défaut
    }

    public ResponsableFinancier(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("RESPONSABLE_FINANCIER");
        this.niveauAutorisation = "JUNIOR";
        this.dateNomination = LocalDate.now();
        this.peutGenererRapports = true;
        this.actifFinancier = true;
        this.limiteApprobation = 1000.0;
    }

    // Méthodes métier spécifiques aux responsables financiers
    public boolean isJunior() {
        return "JUNIOR".equals(niveauAutorisation);
    }

    public boolean isSenior() {
        return "SENIOR".equals(niveauAutorisation);
    }

    public boolean isDirecteur() {
        return "DIRECTEUR".equals(niveauAutorisation);
    }

    public boolean peutApprouverMontant(Double montant) {
        if (!isActifFinancier() || montant == null || limiteApprobation == null) {
            return false;
        }
        return montant <= limiteApprobation;
    }

    public boolean isActifFinancier() {
        return actifFinancier != null && actifFinancier && isActif();
    }

    public boolean peutAccederAux(String ressource) {
        if (!isActifFinancier()) return false;
        
        switch (ressource.toUpperCase()) {
            case "BUDGETS":
                return peutApprouverBudgets != null && peutApprouverBudgets;
            case "SALAIRES":
                return peutVoirSalaires != null && peutVoirSalaires;
            case "TARIFS":
                return peutModifierTarifs != null && peutModifierTarifs;
            case "RAPPORTS":
                return peutGenererRapports != null && peutGenererRapports;
            default:
                return false;
        }
    }

    public void promouvoirSenior() {
        this.niveauAutorisation = "SENIOR";
        this.limiteApprobation = 10000.0;
        this.peutApprouverBudgets = true;
        this.peutVoirSalaires = true;
    }

    public void promouvoirDirecteur() {
        this.niveauAutorisation = "DIRECTEUR";
        this.limiteApprobation = 100000.0;
        this.peutApprouverBudgets = true;
        this.peutVoirSalaires = true;
        this.peutModifierTarifs = true;
    }

    public void suspendreActiviteFinanciere() {
        this.actifFinancier = false;
    }

    public void reprendreActiviteFinanciere() {
        this.actifFinancier = true;
    }

    // Getters et Setters
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getNiveauAutorisation() { return niveauAutorisation; }
    public void setNiveauAutorisation(String niveauAutorisation) { this.niveauAutorisation = niveauAutorisation; }

    public Double getLimiteApprobation() { return limiteApprobation; }
    public void setLimiteApprobation(Double limiteApprobation) { this.limiteApprobation = limiteApprobation; }

    public Boolean getPeutApprouverBudgets() { return peutApprouverBudgets; }
    public void setPeutApprouverBudgets(Boolean peutApprouverBudgets) { this.peutApprouverBudgets = peutApprouverBudgets; }

    public Boolean getPeutVoirSalaires() { return peutVoirSalaires; }
    public void setPeutVoirSalaires(Boolean peutVoirSalaires) { this.peutVoirSalaires = peutVoirSalaires; }

    public Boolean getPeutModifierTarifs() { return peutModifierTarifs; }
    public void setPeutModifierTarifs(Boolean peutModifierTarifs) { this.peutModifierTarifs = peutModifierTarifs; }

    public Boolean getPeutGenererRapports() { return peutGenererRapports; }
    public void setPeutGenererRapports(Boolean peutGenererRapports) { this.peutGenererRapports = peutGenererRapports; }

    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }

    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    public Integer getExperienceAnnees() { return experienceAnnees; }
    public void setExperienceAnnees(Integer experienceAnnees) { this.experienceAnnees = experienceAnnees; }

    public LocalDate getDateNomination() { return dateNomination; }
    public void setDateNomination(LocalDate dateNomination) { this.dateNomination = dateNomination; }

    public Double getSalaire() { return salaire; }
    public void setSalaire(Double salaire) { this.salaire = salaire; }

    public String getResponsabilites() { return responsabilites; }
    public void setResponsabilites(String responsabilites) { this.responsabilites = responsabilites; }

    public Boolean getActifFinancier() { return actifFinancier; }
    public void setActifFinancier(Boolean actifFinancier) { this.actifFinancier = actifFinancier; }

    @Override
    public String toString() {
        return "ResponsableFinancier{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", niveauAutorisation='" + niveauAutorisation + '\'' +
                ", departement='" + departement + '\'' +
                ", limiteApprobation=" + limiteApprobation +
                '}';
    }
}
