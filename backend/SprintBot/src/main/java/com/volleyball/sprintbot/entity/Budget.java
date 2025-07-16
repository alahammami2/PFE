package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_budget", length = 100)
    private String idBudget;

    @Column(name = "montant")
    private Double montant;

    @Column(name = "id_equipe", length = 100)
    private String idEquipe;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_financier_id")
    private ResponsableFinancier responsableFinancier;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategorieBudget> categories = new ArrayList<>();

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Depense> depenses = new ArrayList<>();

    // Constructeurs
    public Budget() {}

    public Budget(String idBudget, Double montant, String idEquipe, ResponsableFinancier responsableFinancier) {
        this.idBudget = idBudget;
        this.montant = montant;
        this.idEquipe = idEquipe;
        this.responsableFinancier = responsableFinancier;
    }

    // Méthodes métier
    public void consulter() {
        // Logique pour consulter le budget
    }

    public void mettreAJour() {
        // Logique pour mettre à jour le budget
    }

    public void ajouterCategorie(CategorieBudget categorie) {
        this.categories.add(categorie);
        categorie.setBudget(this);
    }

    public void retirerCategorie(CategorieBudget categorie) {
        this.categories.remove(categorie);
        categorie.setBudget(null);
    }

    public void ajouterDepense(Depense depense) {
        this.depenses.add(depense);
        depense.setBudget(this);
    }

    public double getTotalDepenses() {
        return this.depenses.stream()
                .mapToDouble(Depense::getMontant)
                .sum();
    }

    public double getMontantRestant() {
        return this.montant - getTotalDepenses();
    }

    public double getPourcentageUtilise() {
        if (this.montant == 0) return 0;
        return (getTotalDepenses() / this.montant) * 100;
    }

    public boolean estDepasse() {
        return getTotalDepenses() > this.montant;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdBudget() { return idBudget; }
    public void setIdBudget(String idBudget) { this.idBudget = idBudget; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getIdEquipe() { return idEquipe; }
    public void setIdEquipe(String idEquipe) { this.idEquipe = idEquipe; }

    public ResponsableFinancier getResponsableFinancier() { return responsableFinancier; }
    public void setResponsableFinancier(ResponsableFinancier responsableFinancier) { 
        this.responsableFinancier = responsableFinancier; 
    }

    public List<CategorieBudget> getCategories() { return categories; }
    public void setCategories(List<CategorieBudget> categories) { this.categories = categories; }

    public List<Depense> getDepenses() { return depenses; }
    public void setDepenses(List<Depense> depenses) { this.depenses = depenses; }
}


