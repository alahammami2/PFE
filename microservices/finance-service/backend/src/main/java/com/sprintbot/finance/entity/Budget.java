package com.sprintbot.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un budget
 * Gère la planification et le suivi budgétaire
 */
@Entity
@Table(name = "budgets", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du budget est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant total doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal montantTotal;

    @DecimalMin(value = "0.0", message = "Le montant utilisé doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_utilise", precision = 14, scale = 2)
    private BigDecimal montantUtilise = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Le montant restant doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_restant", precision = 14, scale = 2)
    private BigDecimal montantRestant;

    @NotNull(message = "La période est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "periode", nullable = false, length = 20)
    private PeriodeBudget periode;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutBudget statut = StatutBudget.ACTIF;

    @NotNull(message = "L'utilisateur créateur est obligatoire")
    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "seuil_alerte", precision = 5, scale = 2)
    private BigDecimal seuilAlerte = new BigDecimal("0.80"); // 80% par défaut

    @Column(name = "alerte_activee")
    private Boolean alerteActivee = true;

    @Column(name = "auto_renouvellement")
    private Boolean autoRenouvellement = false;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Column(name = "notes", length = 500)
    private String notes;

    // Relations
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategorieBudget> categories = new ArrayList<>();

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public Budget() {}

    public Budget(String nom, BigDecimal montantTotal, PeriodeBudget periode, 
                  LocalDate dateDebut, LocalDate dateFin, Long utilisateurId) {
        this.nom = nom;
        this.montantTotal = montantTotal;
        this.montantRestant = montantTotal;
        this.periode = periode;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateurId = utilisateurId;
        this.statut = StatutBudget.ACTIF;
    }

    // Méthodes métier
    public void utiliserMontant(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (montant.compareTo(this.montantRestant) > 0) {
            throw new IllegalArgumentException("Montant insuffisant dans le budget");
        }
        
        this.montantUtilise = this.montantUtilise.add(montant);
        this.montantRestant = this.montantTotal.subtract(this.montantUtilise);
    }

    public void libererMontant(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (montant.compareTo(this.montantUtilise) > 0) {
            throw new IllegalArgumentException("Impossible de libérer plus que le montant utilisé");
        }
        
        this.montantUtilise = this.montantUtilise.subtract(montant);
        this.montantRestant = this.montantTotal.subtract(this.montantUtilise);
    }

    public BigDecimal getPourcentageUtilise() {
        if (montantTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montantUtilise.divide(montantTotal, 4, BigDecimal.ROUND_HALF_UP)
                           .multiply(new BigDecimal("100"));
    }

    public boolean isSeuilAlerteDepasse() {
        return getPourcentageUtilise().compareTo(seuilAlerte.multiply(new BigDecimal("100"))) >= 0;
    }

    public boolean isExpire() {
        return LocalDate.now().isAfter(dateFin);
    }

    public boolean isActif() {
        return statut == StatutBudget.ACTIF && !isExpire();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { 
        this.montantTotal = montantTotal;
        this.montantRestant = montantTotal.subtract(montantUtilise);
    }

    public BigDecimal getMontantUtilise() { return montantUtilise; }
    public void setMontantUtilise(BigDecimal montantUtilise) { 
        this.montantUtilise = montantUtilise;
        this.montantRestant = montantTotal.subtract(montantUtilise);
    }

    public BigDecimal getMontantRestant() { return montantRestant; }
    public void setMontantRestant(BigDecimal montantRestant) { this.montantRestant = montantRestant; }

    public PeriodeBudget getPeriode() { return periode; }
    public void setPeriode(PeriodeBudget periode) { this.periode = periode; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public StatutBudget getStatut() { return statut; }
    public void setStatut(StatutBudget statut) { this.statut = statut; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public BigDecimal getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(BigDecimal seuilAlerte) { this.seuilAlerte = seuilAlerte; }

    public Boolean getAlerteActivee() { return alerteActivee; }
    public void setAlerteActivee(Boolean alerteActivee) { this.alerteActivee = alerteActivee; }

    public Boolean getAutoRenouvellement() { return autoRenouvellement; }
    public void setAutoRenouvellement(Boolean autoRenouvellement) { this.autoRenouvellement = autoRenouvellement; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<CategorieBudget> getCategories() { return categories; }
    public void setCategories(List<CategorieBudget> categories) { this.categories = categories; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enums
    public enum PeriodeBudget {
        MENSUEL, TRIMESTRIEL, SEMESTRIEL, ANNUEL
    }

    public enum StatutBudget {
        ACTIF, CLOTURE, SUSPENDU, EXPIRE
    }
}
