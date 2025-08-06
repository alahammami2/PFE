package com.sprintbot.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une catégorie de transaction
 * Permet de classifier les transactions par type
 */
@Entity
@Table(name = "categories_transaction", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class CategorieTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100, unique = true)
    private String nom;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Le type de transaction est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_transaction", nullable = false, length = 20)
    private TypeTransaction typeTransaction;

    @Size(max = 20, message = "La couleur ne peut pas dépasser 20 caractères")
    @Column(name = "couleur", length = 20)
    private String couleur; // Pour l'affichage graphique

    @Size(max = 50, message = "L'icône ne peut pas dépasser 50 caractères")
    @Column(name = "icone", length = 50)
    private String icone; // Pour l'affichage dans l'interface

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "ordre_affichage")
    private Integer ordreAffichage = 0;

    @Column(name = "validation_requise")
    private Boolean validationRequise = false;

    @Column(name = "piece_jointe_obligatoire")
    private Boolean pieceJointeObligatoire = false;

    // Relations
    @OneToMany(mappedBy = "categorieTransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public CategorieTransaction() {}

    public CategorieTransaction(String nom, String description, TypeTransaction typeTransaction) {
        this.nom = nom;
        this.description = description;
        this.typeTransaction = typeTransaction;
    }

    // Méthodes métier
    public boolean isRecette() {
        return typeTransaction == TypeTransaction.RECETTE;
    }

    public boolean isDepense() {
        return typeTransaction == TypeTransaction.DEPENSE;
    }

    public boolean isValidationRequise() {
        return validationRequise != null && validationRequise;
    }

    public boolean isPieceJointeObligatoire() {
        return pieceJointeObligatoire != null && pieceJointeObligatoire;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeTransaction getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(TypeTransaction typeTransaction) { this.typeTransaction = typeTransaction; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Integer getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }

    public Boolean getValidationRequise() { return validationRequise; }
    public void setValidationRequise(Boolean validationRequise) { this.validationRequise = validationRequise; }

    public Boolean getPieceJointeObligatoire() { return pieceJointeObligatoire; }
    public void setPieceJointeObligatoire(Boolean pieceJointeObligatoire) { this.pieceJointeObligatoire = pieceJointeObligatoire; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enum
    public enum TypeTransaction {
        RECETTE("Recette"),
        DEPENSE("Dépense");

        private final String libelle;

        TypeTransaction(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}
