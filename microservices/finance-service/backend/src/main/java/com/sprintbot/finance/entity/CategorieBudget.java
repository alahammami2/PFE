package com.sprintbot.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une catégorie de budget
 * Permet de subdiviser un budget en différentes catégories de dépenses
 */
@Entity
@Table(name = "categories_budget", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class CategorieBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Le montant alloué est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant alloué doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_alloue", nullable = false, precision = 14, scale = 2)
    private BigDecimal montantAlloue;

    @DecimalMin(value = "0.0", message = "Le montant utilisé doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_utilise", precision = 14, scale = 2)
    private BigDecimal montantUtilise = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Le montant restant doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_restant", precision = 14, scale = 2)
    private BigDecimal montantRestant;

    @NotNull(message = "Le type de catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_categorie", nullable = false, length = 30)
    private TypeCategorie typeCategorie;

    @Column(name = "priorite")
    private Integer priorite = 1;

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "seuil_alerte", precision = 5, scale = 2)
    private BigDecimal seuilAlerte = new BigDecimal("0.80"); // 80% par défaut

    @Size(max = 20, message = "La couleur ne peut pas dépasser 20 caractères")
    @Column(name = "couleur", length = 20)
    private String couleur; // Pour l'affichage graphique

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @OneToMany(mappedBy = "categorieBudget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public CategorieBudget() {}

    public CategorieBudget(String nom, BigDecimal montantAlloue, TypeCategorie typeCategorie, Budget budget) {
        this.nom = nom;
        this.montantAlloue = montantAlloue;
        this.montantRestant = montantAlloue;
        this.typeCategorie = typeCategorie;
        this.budget = budget;
    }

    // Méthodes métier
    public void utiliserMontant(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (montant.compareTo(this.montantRestant) > 0) {
            throw new IllegalArgumentException("Montant insuffisant dans la catégorie");
        }
        
        this.montantUtilise = this.montantUtilise.add(montant);
        this.montantRestant = this.montantAlloue.subtract(this.montantUtilise);
    }

    public void libererMontant(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (montant.compareTo(this.montantUtilise) > 0) {
            throw new IllegalArgumentException("Impossible de libérer plus que le montant utilisé");
        }
        
        this.montantUtilise = this.montantUtilise.subtract(montant);
        this.montantRestant = this.montantAlloue.subtract(this.montantUtilise);
    }

    public BigDecimal getPourcentageUtilise() {
        if (montantAlloue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montantUtilise.divide(montantAlloue, 4, BigDecimal.ROUND_HALF_UP)
                           .multiply(new BigDecimal("100"));
    }

    public boolean isSeuilAlerteDepasse() {
        return getPourcentageUtilise().compareTo(seuilAlerte.multiply(new BigDecimal("100"))) >= 0;
    }

    public boolean isEpuise() {
        return montantRestant.compareTo(BigDecimal.ZERO) <= 0;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMontantAlloue() { return montantAlloue; }
    public void setMontantAlloue(BigDecimal montantAlloue) { 
        this.montantAlloue = montantAlloue;
        this.montantRestant = montantAlloue.subtract(montantUtilise);
    }

    public BigDecimal getMontantUtilise() { return montantUtilise; }
    public void setMontantUtilise(BigDecimal montantUtilise) { 
        this.montantUtilise = montantUtilise;
        this.montantRestant = montantAlloue.subtract(montantUtilise);
    }

    public BigDecimal getMontantRestant() { return montantRestant; }
    public void setMontantRestant(BigDecimal montantRestant) { this.montantRestant = montantRestant; }

    public TypeCategorie getTypeCategorie() { return typeCategorie; }
    public void setTypeCategorie(TypeCategorie typeCategorie) { this.typeCategorie = typeCategorie; }

    public Integer getPriorite() { return priorite; }
    public void setPriorite(Integer priorite) { this.priorite = priorite; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public BigDecimal getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(BigDecimal seuilAlerte) { this.seuilAlerte = seuilAlerte; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enum
    public enum TypeCategorie {
        EQUIPEMENT("Équipement"),
        DEPLACEMENT("Déplacement"),
        FORMATION("Formation"),
        MEDICAL("Médical"),
        INFRASTRUCTURE("Infrastructure"),
        MARKETING("Marketing"),
        ADMINISTRATION("Administration"),
        SALAIRES("Salaires"),
        SPONSORS("Sponsors"),
        COMPETITION("Compétition"),
        AUTRE("Autre");

        private final String libelle;

        TypeCategorie(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}
