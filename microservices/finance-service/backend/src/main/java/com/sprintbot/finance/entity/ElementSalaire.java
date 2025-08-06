package com.sprintbot.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant un élément de salaire
 * Détaille les composants d'un salaire (primes, déductions, etc.)
 */
@Entity
@Table(name = "elements_salaire", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class ElementSalaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 200, message = "Le libellé ne peut pas dépasser 200 caractères")
    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    @NotNull(message = "Le type d'élément est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_element", nullable = false, length = 20)
    private TypeElement typeElement;

    @NotNull(message = "Le montant est obligatoire")
    @Digits(integer = 10, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "quantite")
    private BigDecimal quantite;

    @Column(name = "taux", precision = 5, scale = 2)
    private BigDecimal taux;

    @Column(name = "base_calcul", precision = 12, scale = 2)
    private BigDecimal baseCalcul;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "obligatoire")
    private Boolean obligatoire = false;

    @Column(name = "imposable")
    private Boolean imposable = true;

    @Column(name = "cotisable")
    private Boolean cotisable = true;

    @Column(name = "ordre_affichage")
    private Integer ordreAffichage = 0;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salaire_id", nullable = false)
    private Salaire salaire;

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public ElementSalaire() {}

    public ElementSalaire(String libelle, TypeElement typeElement, BigDecimal montant, Salaire salaire) {
        this.libelle = libelle;
        this.typeElement = typeElement;
        this.montant = montant;
        this.salaire = salaire;
    }

    // Méthodes métier
    public boolean isGain() {
        return typeElement == TypeElement.SALAIRE_BASE || 
               typeElement == TypeElement.PRIME || 
               typeElement == TypeElement.BONUS ||
               typeElement == TypeElement.HEURES_SUPPLEMENTAIRES ||
               typeElement == TypeElement.INDEMNITE;
    }

    public boolean isDeduction() {
        return typeElement == TypeElement.COTISATION_SOCIALE || 
               typeElement == TypeElement.IMPOT || 
               typeElement == TypeElement.DEDUCTION ||
               typeElement == TypeElement.AVANCE ||
               typeElement == TypeElement.RETENUE;
    }

    public BigDecimal calculerMontant() {
        if (quantite != null && taux != null && baseCalcul != null) {
            return baseCalcul.multiply(taux).multiply(quantite).divide(new BigDecimal("100"));
        } else if (quantite != null && taux != null) {
            return quantite.multiply(taux);
        }
        return montant;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public TypeElement getTypeElement() { return typeElement; }
    public void setTypeElement(TypeElement typeElement) { this.typeElement = typeElement; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }

    public BigDecimal getTaux() { return taux; }
    public void setTaux(BigDecimal taux) { this.taux = taux; }

    public BigDecimal getBaseCalcul() { return baseCalcul; }
    public void setBaseCalcul(BigDecimal baseCalcul) { this.baseCalcul = baseCalcul; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getObligatoire() { return obligatoire; }
    public void setObligatoire(Boolean obligatoire) { this.obligatoire = obligatoire; }

    public Boolean getImposable() { return imposable; }
    public void setImposable(Boolean imposable) { this.imposable = imposable; }

    public Boolean getCotisable() { return cotisable; }
    public void setCotisable(Boolean cotisable) { this.cotisable = cotisable; }

    public Integer getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }

    public Salaire getSalaire() { return salaire; }
    public void setSalaire(Salaire salaire) { this.salaire = salaire; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enum
    public enum TypeElement {
        SALAIRE_BASE("Salaire de base"),
        PRIME("Prime"),
        BONUS("Bonus"),
        HEURES_SUPPLEMENTAIRES("Heures supplémentaires"),
        INDEMNITE("Indemnité"),
        COTISATION_SOCIALE("Cotisation sociale"),
        IMPOT("Impôt"),
        DEDUCTION("Déduction"),
        AVANCE("Avance"),
        RETENUE("Retenue"),
        CONGES_PAYES("Congés payés"),
        MALADIE("Maladie"),
        AUTRE("Autre");

        private final String libelle;

        TypeElement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}
