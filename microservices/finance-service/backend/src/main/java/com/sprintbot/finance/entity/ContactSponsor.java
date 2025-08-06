package com.sprintbot.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entité représentant un contact chez un sponsor
 */
@Entity
@Table(name = "contacts_sponsor", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class ContactSponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Size(max = 100, message = "Le poste ne peut pas dépasser 100 caractères")
    @Column(name = "poste", length = 100)
    private String poste;

    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Size(max = 20, message = "Le mobile ne peut pas dépasser 20 caractères")
    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "contact_principal")
    private Boolean contactPrincipal = false;

    @Column(name = "actif")
    private Boolean actif = true;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    @Column(name = "notes", length = 500)
    private String notes;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public ContactSponsor() {}

    public ContactSponsor(String nom, String prenom, String email, Sponsor sponsor) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.sponsor = sponsor;
    }

    // Méthodes métier
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public Boolean getContactPrincipal() { return contactPrincipal; }
    public void setContactPrincipal(Boolean contactPrincipal) { this.contactPrincipal = contactPrincipal; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Sponsor getSponsor() { return sponsor; }
    public void setSponsor(Sponsor sponsor) { this.sponsor = sponsor; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
