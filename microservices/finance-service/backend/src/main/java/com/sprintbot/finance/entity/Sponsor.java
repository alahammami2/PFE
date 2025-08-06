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
 * Entité représentant un sponsor ou partenaire
 * Gère les contrats de sponsoring et partenariats
 */
@Entity
@Table(name = "sponsors", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du sponsor est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Le type de partenariat est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_partenariat", nullable = false, length = 30)
    private TypePartenariat typePartenariat;

    @NotNull(message = "Le montant du contrat est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant du contrat doit être positif")
    @Digits(integer = 12, fraction = 2, message = "Format de montant invalide")
    @Column(name = "montant_contrat", nullable = false, precision = 14, scale = 2)
    private BigDecimal montantContrat;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutSponsor statut = StatutSponsor.ACTIF;

    @Size(max = 200, message = "Le secteur d'activité ne peut pas dépasser 200 caractères")
    @Column(name = "secteur_activite", length = 200)
    private String secteurActivite;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    @Column(name = "adresse", length = 500)
    private String adresse;

    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    @Column(name = "ville", length = 100)
    private String ville;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Size(max = 100, message = "Le pays ne peut pas dépasser 100 caractères")
    @Column(name = "pays", length = 100)
    private String pays;

    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Size(max = 255, message = "Le site web ne peut pas dépasser 255 caractères")
    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Size(max = 255, message = "Le logo ne peut pas dépasser 255 caractères")
    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "auto_renouvellement")
    private Boolean autoRenouvellement = false;

    @Column(name = "duree_renouvellement_mois")
    private Integer dureeRenouvellementMois;

    @Column(name = "notification_renouvellement_jours")
    private Integer notificationRenouvellementJours = 30;

    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "montant_verse", precision = 14, scale = 2)
    private BigDecimal montantVerse = BigDecimal.ZERO;

    @Column(name = "montant_restant", precision = 14, scale = 2)
    private BigDecimal montantRestant;

    // Relations
    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContratSponsoring> contrats = new ArrayList<>();

    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContactSponsor> contacts = new ArrayList<>();

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public Sponsor() {}

    public Sponsor(String nom, TypePartenariat typePartenariat, BigDecimal montantContrat,
                   LocalDate dateDebut, LocalDate dateFin) {
        this.nom = nom;
        this.typePartenariat = typePartenariat;
        this.montantContrat = montantContrat;
        this.montantRestant = montantContrat;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = StatutSponsor.ACTIF;
    }

    // Méthodes métier
    public void enregistrerPaiement(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (montant.compareTo(this.montantRestant) > 0) {
            throw new IllegalArgumentException("Le montant dépasse le montant restant du contrat");
        }
        
        this.montantVerse = this.montantVerse.add(montant);
        this.montantRestant = this.montantContrat.subtract(this.montantVerse);
    }

    public BigDecimal getPourcentageVerse() {
        if (montantContrat.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montantVerse.divide(montantContrat, 4, BigDecimal.ROUND_HALF_UP)
                          .multiply(new BigDecimal("100"));
    }

    public boolean isContratTermine() {
        return montantVerse.compareTo(montantContrat) >= 0;
    }

    public boolean isExpire() {
        return LocalDate.now().isAfter(dateFin);
    }

    public boolean isActif() {
        return statut == StatutSponsor.ACTIF && !isExpire();
    }

    public boolean isRenouvellementProche() {
        if (notificationRenouvellementJours == null) {
            return false;
        }
        LocalDate dateNotification = dateFin.minusDays(notificationRenouvellementJours);
        return LocalDate.now().isAfter(dateNotification) && !isExpire();
    }

    public LocalDate getDateRenouvellementSuggere() {
        if (dureeRenouvellementMois == null) {
            return dateFin.plusYears(1); // Par défaut 1 an
        }
        return dateFin.plusMonths(dureeRenouvellementMois);
    }

    public void renouveler(BigDecimal nouveauMontant, LocalDate nouvelleDateFin) {
        if (nouveauMontant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le nouveau montant doit être positif");
        }
        
        if (nouvelleDateFin.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La nouvelle date de fin doit être future");
        }
        
        this.montantContrat = nouveauMontant;
        this.montantVerse = BigDecimal.ZERO;
        this.montantRestant = nouveauMontant;
        this.dateDebut = LocalDate.now();
        this.dateFin = nouvelleDateFin;
        this.statut = StatutSponsor.ACTIF;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypePartenariat getTypePartenariat() { return typePartenariat; }
    public void setTypePartenariat(TypePartenariat typePartenariat) { this.typePartenariat = typePartenariat; }

    public BigDecimal getMontantContrat() { return montantContrat; }
    public void setMontantContrat(BigDecimal montantContrat) { 
        this.montantContrat = montantContrat;
        this.montantRestant = montantContrat.subtract(montantVerse);
    }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public StatutSponsor getStatut() { return statut; }
    public void setStatut(StatutSponsor statut) { this.statut = statut; }

    public String getSecteurActivite() { return secteurActivite; }
    public void setSecteurActivite(String secteurActivite) { this.secteurActivite = secteurActivite; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getSiteWeb() { return siteWeb; }
    public void setSiteWeb(String siteWeb) { this.siteWeb = siteWeb; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Boolean getAutoRenouvellement() { return autoRenouvellement; }
    public void setAutoRenouvellement(Boolean autoRenouvellement) { this.autoRenouvellement = autoRenouvellement; }

    public Integer getDureeRenouvellementMois() { return dureeRenouvellementMois; }
    public void setDureeRenouvellementMois(Integer dureeRenouvellementMois) { this.dureeRenouvellementMois = dureeRenouvellementMois; }

    public Integer getNotificationRenouvellementJours() { return notificationRenouvellementJours; }
    public void setNotificationRenouvellementJours(Integer notificationRenouvellementJours) { this.notificationRenouvellementJours = notificationRenouvellementJours; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getMontantVerse() { return montantVerse; }
    public void setMontantVerse(BigDecimal montantVerse) { 
        this.montantVerse = montantVerse;
        this.montantRestant = montantContrat.subtract(montantVerse);
    }

    public BigDecimal getMontantRestant() { return montantRestant; }
    public void setMontantRestant(BigDecimal montantRestant) { this.montantRestant = montantRestant; }

    public List<ContratSponsoring> getContrats() { return contrats; }
    public void setContrats(List<ContratSponsoring> contrats) { this.contrats = contrats; }

    public List<ContactSponsor> getContacts() { return contacts; }
    public void setContacts(List<ContactSponsor> contacts) { this.contacts = contacts; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enums
    public enum TypePartenariat {
        PRINCIPAL("Sponsor Principal"),
        OFFICIEL("Partenaire Officiel"),
        TECHNIQUE("Partenaire Technique"),
        MEDIA("Partenaire Média"),
        INSTITUTIONNEL("Partenaire Institutionnel"),
        FOURNISSEUR("Fournisseur");

        private final String libelle;

        TypePartenariat(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutSponsor {
        ACTIF, EXPIRE, SUSPENDU, RESILIE
    }
}
