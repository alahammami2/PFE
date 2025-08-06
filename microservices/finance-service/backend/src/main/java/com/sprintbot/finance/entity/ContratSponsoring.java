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
 * Entité représentant un contrat de sponsoring
 * Détaille les termes et conditions d'un partenariat
 */
@Entity
@Table(name = "contrats_sponsoring", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class ContratSponsoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de contrat est obligatoire")
    @Size(max = 50, message = "Le numéro ne peut pas dépasser 50 caractères")
    @Column(name = "numero_contrat", nullable = false, length = 50, unique = true)
    private String numeroContrat;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant", nullable = false, precision = 14, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "La date de signature est obligatoire")
    @Column(name = "date_signature", nullable = false)
    private LocalDate dateSignature;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutContrat statut = StatutContrat.ACTIF;

    @Size(max = 2000, message = "Les termes ne peuvent pas dépasser 2000 caractères")
    @Column(name = "termes_conditions", length = 2000)
    private String termesConditions;

    @Size(max = 1000, message = "Les contreparties ne peuvent pas dépasser 1000 caractères")
    @Column(name = "contreparties", length = 1000)
    private String contreparties;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalite_paiement", length = 20)
    private ModalitePaiement modalitePaiement = ModalitePaiement.UNIQUE;

    @Column(name = "nombre_echeances")
    private Integer nombreEcheances;

    @Column(name = "auto_renouvellement")
    private Boolean autoRenouvellement = false;

    @Column(name = "duree_renouvellement_mois")
    private Integer dureeRenouvellementMois;

    @Size(max = 255, message = "Le fichier contrat ne peut pas dépasser 255 caractères")
    @Column(name = "fichier_contrat", length = 255)
    private String fichierContrat;

    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    @Column(name = "notes", length = 1000)
    private String notes;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaiementSponsor> paiements = new ArrayList<>();

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public ContratSponsoring() {}

    public ContratSponsoring(String numeroContrat, BigDecimal montant, LocalDate dateSignature,
                           LocalDate dateDebut, LocalDate dateFin, Sponsor sponsor) {
        this.numeroContrat = numeroContrat;
        this.montant = montant;
        this.dateSignature = dateSignature;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.sponsor = sponsor;
        this.statut = StatutContrat.ACTIF;
    }

    // Méthodes métier
    public boolean isActif() {
        return statut == StatutContrat.ACTIF && !isExpire();
    }

    public boolean isExpire() {
        return LocalDate.now().isAfter(dateFin);
    }

    public boolean isRenouvellementProche(int joursAvant) {
        LocalDate dateNotification = dateFin.minusDays(joursAvant);
        return LocalDate.now().isAfter(dateNotification) && !isExpire();
    }

    public BigDecimal getMontantVerse() {
        return paiements.stream()
                .filter(p -> p.getStatut() == PaiementSponsor.StatutPaiement.EFFECTUE)
                .map(PaiementSponsor::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontantRestant() {
        return montant.subtract(getMontantVerse());
    }

    public boolean isTermine() {
        return getMontantVerse().compareTo(montant) >= 0;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroContrat() { return numeroContrat; }
    public void setNumeroContrat(String numeroContrat) { this.numeroContrat = numeroContrat; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDate getDateSignature() { return dateSignature; }
    public void setDateSignature(LocalDate dateSignature) { this.dateSignature = dateSignature; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public StatutContrat getStatut() { return statut; }
    public void setStatut(StatutContrat statut) { this.statut = statut; }

    public String getTermesConditions() { return termesConditions; }
    public void setTermesConditions(String termesConditions) { this.termesConditions = termesConditions; }

    public String getContreparties() { return contreparties; }
    public void setContreparties(String contreparties) { this.contreparties = contreparties; }

    public ModalitePaiement getModalitePaiement() { return modalitePaiement; }
    public void setModalitePaiement(ModalitePaiement modalitePaiement) { this.modalitePaiement = modalitePaiement; }

    public Integer getNombreEcheances() { return nombreEcheances; }
    public void setNombreEcheances(Integer nombreEcheances) { this.nombreEcheances = nombreEcheances; }

    public Boolean getAutoRenouvellement() { return autoRenouvellement; }
    public void setAutoRenouvellement(Boolean autoRenouvellement) { this.autoRenouvellement = autoRenouvellement; }

    public Integer getDureeRenouvellementMois() { return dureeRenouvellementMois; }
    public void setDureeRenouvellementMois(Integer dureeRenouvellementMois) { this.dureeRenouvellementMois = dureeRenouvellementMois; }

    public String getFichierContrat() { return fichierContrat; }
    public void setFichierContrat(String fichierContrat) { this.fichierContrat = fichierContrat; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Sponsor getSponsor() { return sponsor; }
    public void setSponsor(Sponsor sponsor) { this.sponsor = sponsor; }

    public List<PaiementSponsor> getPaiements() { return paiements; }
    public void setPaiements(List<PaiementSponsor> paiements) { this.paiements = paiements; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enums
    public enum StatutContrat {
        ACTIF, EXPIRE, SUSPENDU, RESILIE, TERMINE
    }

    public enum ModalitePaiement {
        UNIQUE("Paiement unique"),
        MENSUEL("Mensuel"),
        TRIMESTRIEL("Trimestriel"),
        SEMESTRIEL("Semestriel"),
        ANNUEL("Annuel");

        private final String libelle;

        ModalitePaiement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}
