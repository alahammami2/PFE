package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "depenses")
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID de la dépense est obligatoire")
    @Size(max = 100)
    @Column(name = "id_depense", nullable = false, unique = true, length = 100)
    private String idDepense;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255)
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant doit être positif")
    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "date_depense")
    private LocalDate dateDepense;

    @Size(max = 50)
    @Column(name = "statut", length = 50)
    private String statut;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_budget_id")
    private CategorieBudget categorieBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_financier_id")
    private ResponsableFinancier responsableFinancier;

    // Constructeurs
    public Depense() {
        this.dateDepense = LocalDate.now();
        this.statut = "EN_ATTENTE";
    }

    public Depense(String idDepense, String description, Double montant, Budget budget, CategorieBudget categorieBudget) {
        this();
        this.idDepense = idDepense;
        this.description = description;
        this.montant = montant;
        this.budget = budget;
        this.categorieBudget = categorieBudget;
    }

    // Méthodes métier
    public void approuver() {
        this.statut = "APPROUVEE";
    }

    public void rejeter() {
        this.statut = "REJETEE";
    }

    public void payer() {
        this.statut = "PAYEE";
    }

    public boolean estApprouvee() {
        return "APPROUVEE".equals(this.statut);
    }

    public boolean estPayee() {
        return "PAYEE".equals(this.statut);
    }

    public boolean estRejetee() {
        return "REJETEE".equals(this.statut);
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdDepense() { return idDepense; }
    public void setIdDepense(String idDepense) { this.idDepense = idDepense; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public LocalDate getDateDepense() { return dateDepense; }
    public void setDateDepense(LocalDate dateDepense) { this.dateDepense = dateDepense; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }

    public CategorieBudget getCategorieBudget() { return categorieBudget; }
    public void setCategorieBudget(CategorieBudget categorieBudget) { this.categorieBudget = categorieBudget; }

    public ResponsableFinancier getResponsableFinancier() { return responsableFinancier; }
    public void setResponsableFinancier(ResponsableFinancier responsableFinancier) { this.responsableFinancier = responsableFinancier; }
}

