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
 * Entité représentant un paiement de sponsor
 */
@Entity
@Table(name = "paiements_sponsor", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class PaiementSponsor {

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

    @NotNull(message = "La date prévue est obligatoire")
    @Column(name = "date_prevue", nullable = false)
    private LocalDate datePrevue;

    @Column(name = "date_effective")
    private LocalDate dateEffective;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutPaiement statut = StatutPaiement.PREVU;

    @Size(max = 100, message = "Le mode de paiement ne peut pas dépasser 100 caractères")
    @Column(name = "mode_paiement", length = 100)
    private String modePaiement;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Column(name = "notes", length = 500)
    private String notes;

    @Size(max = 255, message = "La pièce jointe ne peut pas dépasser 255 caractères")
    @Column(name = "piece_jointe", length = 255)
    private String pieceJointe;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private ContratSponsoring contrat;

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public PaiementSponsor() {}

    public PaiementSponsor(String reference, BigDecimal montant, LocalDate datePrevue,
                          Sponsor sponsor, ContratSponsoring contrat) {
        this.reference = reference;
        this.montant = montant;
        this.datePrevue = datePrevue;
        this.sponsor = sponsor;
        this.contrat = contrat;
        this.statut = StatutPaiement.PREVU;
    }

    // Méthodes métier
    public void marquerCommeEffectue(LocalDate dateEffective) {
        this.statut = StatutPaiement.EFFECTUE;
        this.dateEffective = dateEffective;
    }

    public void marquerCommeRetard() {
        if (LocalDate.now().isAfter(datePrevue) && statut == StatutPaiement.PREVU) {
            this.statut = StatutPaiement.EN_RETARD;
        }
    }

    public boolean isEnRetard() {
        return LocalDate.now().isAfter(datePrevue) && statut == StatutPaiement.PREVU;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDate getDatePrevue() { return datePrevue; }
    public void setDatePrevue(LocalDate datePrevue) { this.datePrevue = datePrevue; }

    public LocalDate getDateEffective() { return dateEffective; }
    public void setDateEffective(LocalDate dateEffective) { this.dateEffective = dateEffective; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPieceJointe() { return pieceJointe; }
    public void setPieceJointe(String pieceJointe) { this.pieceJointe = pieceJointe; }

    public Sponsor getSponsor() { return sponsor; }
    public void setSponsor(Sponsor sponsor) { this.sponsor = sponsor; }

    public ContratSponsoring getContrat() { return contrat; }
    public void setContrat(ContratSponsoring contrat) { this.contrat = contrat; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enum
    public enum StatutPaiement {
        PREVU, EFFECTUE, EN_RETARD, ANNULE
    }
}
