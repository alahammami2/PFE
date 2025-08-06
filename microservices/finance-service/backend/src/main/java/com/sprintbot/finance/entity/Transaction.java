package com.sprintbot.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une transaction financière
 * Gère les recettes et dépenses de l'équipe
 */
@Entity
@Table(name = "transactions", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La référence est obligatoire")
    @Size(max = 50, message = "La référence ne peut pas dépasser 50 caractères")
    @Column(name = "reference", nullable = false, length = 50, unique = true)
    private String reference;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant", nullable = false, precision = 14, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "Le type de transaction est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_transaction", nullable = false, length = 20)
    private TypeTransaction typeTransaction;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @NotNull(message = "La date de transaction est obligatoire")
    @Column(name = "date_transaction", nullable = false)
    private LocalDate dateTransaction;

    @Column(name = "date_comptabilisation")
    private LocalDate dateComptabilisation;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutTransaction statut = StatutTransaction.EN_ATTENTE;

    @NotNull(message = "L'utilisateur est obligatoire")
    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "validateur_id")
    private Long validateurId;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Size(max = 255, message = "Le nom de la pièce jointe ne peut pas dépasser 255 caractères")
    @Column(name = "piece_jointe", length = 255)
    private String pieceJointe;

    @Size(max = 100, message = "Le mode de paiement ne peut pas dépasser 100 caractères")
    @Column(name = "mode_paiement", length = 100)
    private String modePaiement;

    @Size(max = 100, message = "Le bénéficiaire ne peut pas dépasser 100 caractères")
    @Column(name = "beneficiaire", length = 100)
    private String beneficiaire;

    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "recurrente")
    private Boolean recurrente = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequence_recurrence", length = 20)
    private FrequenceRecurrence frequenceRecurrence;

    @Column(name = "tva_applicable")
    private Boolean tvaApplicable = false;

    @Column(name = "taux_tva", precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Column(name = "montant_ht", precision = 14, scale = 2)
    private BigDecimal montantHT;

    @Column(name = "montant_tva", precision = 14, scale = 2)
    private BigDecimal montantTva;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_budget_id")
    private CategorieBudget categorieBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_transaction_id", nullable = false)
    private CategorieTransaction categorieTransaction;

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public Transaction() {}

    public Transaction(String reference, BigDecimal montant, TypeTransaction typeTransaction,
                      String description, LocalDate dateTransaction, Long utilisateurId,
                      CategorieTransaction categorieTransaction) {
        this.reference = reference;
        this.montant = montant;
        this.typeTransaction = typeTransaction;
        this.description = description;
        this.dateTransaction = dateTransaction;
        this.utilisateurId = utilisateurId;
        this.categorieTransaction = categorieTransaction;
        this.statut = StatutTransaction.EN_ATTENTE;
    }

    // Méthodes métier
    public void valider(Long validateurId) {
        if (this.statut != StatutTransaction.EN_ATTENTE) {
            throw new IllegalStateException("Seules les transactions en attente peuvent être validées");
        }
        
        this.statut = StatutTransaction.VALIDEE;
        this.validateurId = validateurId;
        this.dateValidation = LocalDateTime.now();
        this.dateComptabilisation = LocalDate.now();
    }

    public void rejeter(Long validateurId, String motif) {
        if (this.statut != StatutTransaction.EN_ATTENTE) {
            throw new IllegalStateException("Seules les transactions en attente peuvent être rejetées");
        }
        
        this.statut = StatutTransaction.REJETEE;
        this.validateurId = validateurId;
        this.dateValidation = LocalDateTime.now();
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "Rejet: " + motif;
    }

    public void calculerTva() {
        if (tvaApplicable && tauxTva != null) {
            this.montantHT = montant.divide(BigDecimal.ONE.add(tauxTva.divide(new BigDecimal("100"))), 2, BigDecimal.ROUND_HALF_UP);
            this.montantTva = montant.subtract(montantHT);
        } else {
            this.montantHT = montant;
            this.montantTva = BigDecimal.ZERO;
        }
    }

    public boolean isValidee() {
        return statut == StatutTransaction.VALIDEE;
    }

    public boolean isRejetee() {
        return statut == StatutTransaction.REJETEE;
    }

    public boolean isEnAttente() {
        return statut == StatutTransaction.EN_ATTENTE;
    }

    public boolean isRecette() {
        return typeTransaction == TypeTransaction.RECETTE;
    }

    public boolean isDepense() {
        return typeTransaction == TypeTransaction.DEPENSE;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public TypeTransaction getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(TypeTransaction typeTransaction) { this.typeTransaction = typeTransaction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }

    public LocalDate getDateComptabilisation() { return dateComptabilisation; }
    public void setDateComptabilisation(LocalDate dateComptabilisation) { this.dateComptabilisation = dateComptabilisation; }

    public StatutTransaction getStatut() { return statut; }
    public void setStatut(StatutTransaction statut) { this.statut = statut; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public Long getValidateurId() { return validateurId; }
    public void setValidateurId(Long validateurId) { this.validateurId = validateurId; }

    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }

    public String getPieceJointe() { return pieceJointe; }
    public void setPieceJointe(String pieceJointe) { this.pieceJointe = pieceJointe; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public String getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(String beneficiaire) { this.beneficiaire = beneficiaire; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getRecurrente() { return recurrente; }
    public void setRecurrente(Boolean recurrente) { this.recurrente = recurrente; }

    public FrequenceRecurrence getFrequenceRecurrence() { return frequenceRecurrence; }
    public void setFrequenceRecurrence(FrequenceRecurrence frequenceRecurrence) { this.frequenceRecurrence = frequenceRecurrence; }

    public Boolean getTvaApplicable() { return tvaApplicable; }
    public void setTvaApplicable(Boolean tvaApplicable) { this.tvaApplicable = tvaApplicable; }

    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) { this.tauxTva = tauxTva; }

    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }

    public BigDecimal getMontantTva() { return montantTva; }
    public void setMontantTva(BigDecimal montantTva) { this.montantTva = montantTva; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }

    public CategorieBudget getCategorieBudget() { return categorieBudget; }
    public void setCategorieBudget(CategorieBudget categorieBudget) { this.categorieBudget = categorieBudget; }

    public CategorieTransaction getCategorieTransaction() { return categorieTransaction; }
    public void setCategorieTransaction(CategorieTransaction categorieTransaction) { this.categorieTransaction = categorieTransaction; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enums
    public enum TypeTransaction {
        RECETTE, DEPENSE
    }

    public enum StatutTransaction {
        EN_ATTENTE, VALIDEE, REJETEE, ANNULEE
    }

    public enum FrequenceRecurrence {
        HEBDOMADAIRE, MENSUELLE, TRIMESTRIELLE, SEMESTRIELLE, ANNUELLE
    }
}
