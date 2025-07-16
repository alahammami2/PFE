package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories_budget")
public class CategorieBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID de la catégorie est obligatoire")
    @Size(max = 100)
    @Column(name = "id_categorie", nullable = false, unique = true, length = 100)
    private String idCategorie;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100)
    @Column(name = "nom_categorie", nullable = false, length = 100)
    private String nomCategorie;

    @NotNull(message = "Le montant alloué est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant alloué doit être positif")
    @Column(name = "montant_alloue", nullable = false)
    private Double montantAlloue;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToMany(mappedBy = "categorieBudget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Depense> depenses = new ArrayList<>();

    // Constructeurs
    public CategorieBudget() {}

    public CategorieBudget(String idCategorie, String nomCategorie, Double montantAlloue, Budget budget) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        this.montantAlloue = montantAlloue;
        this.budget = budget;
    }

    // Méthodes métier
    public void consulter() {
        // Logique pour consulter la catégorie
    }

    public void mettreAJour() {
        // Logique pour mettre à jour la catégorie
    }

    public void ajouterDepense(Depense depense) {
        this.depenses.add(depense);
        depense.setCategorieBudget(this);
    }

    public double getTotalDepenses() {
        return this.depenses.stream()
                .mapToDouble(Depense::getMontant)
                .sum();
    }

    public double getMontantRestant() {
        return this.montantAlloue - getTotalDepenses();
    }

    public double getPourcentageUtilise() {
        if (this.montantAlloue == 0) return 0;
        return (getTotalDepenses() / this.montantAlloue) * 100;
    }

    public boolean estDepassee() {
        return getTotalDepenses() > this.montantAlloue;
    }

    public boolean peutAccepterDepense(double montantDepense) {
        return getMontantRestant() >= montantDepense;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdCategorie() { return idCategorie; }
    public void setIdCategorie(String idCategorie) { this.idCategorie = idCategorie; }

    public String getNomCategorie() { return nomCategorie; }
    public void setNomCategorie(String nomCategorie) { this.nomCategorie = nomCategorie; }

    public Double getMontantAlloue() { return montantAlloue; }
    public void setMontantAlloue(Double montantAlloue) { this.montantAlloue = montantAlloue; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }

    public List<Depense> getDepenses() { return depenses; }
    public void setDepenses(List<Depense> depenses) { this.depenses = depenses; }
}
