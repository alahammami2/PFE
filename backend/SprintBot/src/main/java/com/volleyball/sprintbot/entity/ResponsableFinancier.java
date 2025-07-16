package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "responsables_financiers")
@DiscriminatorValue("RESPONSABLE_FINANCIER")
public class ResponsableFinancier extends Utilisateur {

    // Relations
    @OneToMany(mappedBy = "responsableFinancier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Budget> budgetsGeres = new ArrayList<>();

    @OneToMany(mappedBy = "responsableFinancier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Depense> depensesGerees = new ArrayList<>();

    // Constructeurs
    public ResponsableFinancier() {
        super();
        this.setRole("RESPONSABLE_FINANCIER");
    }

    public ResponsableFinancier(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("RESPONSABLE_FINANCIER");
    }

    // Méthodes métier
    public void consulterAbsences() {
        // Logique pour consulter les absences
    }

    public void gererPaiementsConfirmes() {
        // Logique pour gérer les paiements confirmés
    }

    public void consulterFinances() {
        // Logique pour consulter les finances
    }

    public void gererFinances() {
        // Logique pour gérer les finances
    }

    public void ajouterBudget(Budget budget) {
        this.budgetsGeres.add(budget);
        budget.setResponsableFinancier(this);
    }

    public void retirerBudget(Budget budget) {
        this.budgetsGeres.remove(budget);
        budget.setResponsableFinancier(null);
    }

    public void ajouterDepense(Depense depense) {
        this.depensesGerees.add(depense);
        depense.setResponsableFinancier(this);
    }

    public double getTotalBudgetsGeres() {
        return this.budgetsGeres.stream()
                .mapToDouble(Budget::getMontant)
                .sum();
    }

    public double getTotalDepensesGerees() {
        return this.depensesGerees.stream()
                .mapToDouble(Depense::getMontant)
                .sum();
    }

    // Getters et Setters
    public List<Budget> getBudgetsGeres() { return budgetsGeres; }
    public void setBudgetsGeres(List<Budget> budgetsGeres) { this.budgetsGeres = budgetsGeres; }

    public List<Depense> getDepensesGerees() { return depensesGerees; }
    public void setDepensesGerees(List<Depense> depensesGerees) { this.depensesGerees = depensesGerees; }
}
