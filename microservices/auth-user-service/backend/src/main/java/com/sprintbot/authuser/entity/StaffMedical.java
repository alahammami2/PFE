package com.sprintbot.authuser.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entité représentant un membre du staff médical
 * Hérite de Utilisateur avec des propriétés spécifiques au personnel médical
 */
@Entity
@Table(name = "staff_medical")
@DiscriminatorValue("STAFF_MEDICAL")
public class StaffMedical extends Utilisateur {

    @Size(max = 100, message = "La spécialité ne peut pas dépasser 100 caractères")
    @Column(name = "specialite", length = 100)
    private String specialite;

    @Size(max = 50, message = "Le numéro de licence ne peut pas dépasser 50 caractères")
    @Column(name = "numero_licence", length = 50, unique = true)
    private String numeroLicence;

    @Size(max = 100, message = "La formation ne peut pas dépasser 100 caractères")
    @Column(name = "formation", length = 100)
    private String formation;

    @Column(name = "experience_annees")
    private Integer experienceAnnees;

    @Column(name = "date_certification")
    private LocalDate dateCertification;

    @Column(name = "date_expiration_licence")
    private LocalDate dateExpirationLicence;

    @Size(max = 20, message = "Le type de staff ne peut pas dépasser 20 caractères")
    @Column(name = "type_staff", length = 20)
    private String typeStaff; // MEDECIN, KINESITHERAPEUTE, INFIRMIER, NUTRITIONNISTE

    @Column(name = "peut_prescrire")
    private Boolean peutPrescrire = false;

    @Column(name = "peut_diagnostiquer")
    private Boolean peutDiagnostiquer = false;

    @Column(name = "disponible_urgence")
    private Boolean disponibleUrgence = true;

    @Size(max = 20, message = "Le numéro d'urgence ne peut pas dépasser 20 caractères")
    @Column(name = "numero_urgence", length = 20)
    private String numeroUrgence;

    @Size(max = 500, message = "Les qualifications ne peuvent pas dépasser 500 caractères")
    @Column(name = "qualifications", length = 500)
    private String qualifications;

    @Column(name = "salaire")
    private Double salaire;

    // Constructeurs
    public StaffMedical() {
        super();
        this.setRole("STAFF_MEDICAL");
        this.disponibleUrgence = true;
        this.peutPrescrire = false;
        this.peutDiagnostiquer = false;
    }

    public StaffMedical(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse);
        this.setRole("STAFF_MEDICAL");
        this.disponibleUrgence = true;
        this.peutPrescrire = false;
        this.peutDiagnostiquer = false;
    }

    public StaffMedical(String nom, String prenom, String email, String motDePasse, String specialite) {
        this(nom, prenom, email, motDePasse);
        this.specialite = specialite;
    }

    // Méthodes métier spécifiques au staff médical
    public boolean isMedecin() {
        return "MEDECIN".equals(typeStaff);
    }

    public boolean isKinesitherapeute() {
        return "KINESITHERAPEUTE".equals(typeStaff);
    }

    public boolean isInfirmier() {
        return "INFIRMIER".equals(typeStaff);
    }

    public boolean isNutritionniste() {
        return "NUTRITIONNISTE".equals(typeStaff);
    }

    public boolean isLicenceValide() {
        if (dateExpirationLicence == null) return true;
        return LocalDate.now().isBefore(dateExpirationLicence);
    }

    public boolean isDisponiblePourUrgence() {
        return disponibleUrgence != null && disponibleUrgence && isActif() && isLicenceValide();
    }

    public boolean peutEffectuerDiagnostic() {
        return peutDiagnostiquer != null && peutDiagnostiquer && isLicenceValide();
    }

    public boolean peutPrescrireTraitement() {
        return peutPrescrire != null && peutPrescrire && isLicenceValide();
    }

    public int getJoursAvantExpirationLicence() {
        if (dateExpirationLicence == null) return Integer.MAX_VALUE;
        return (int) LocalDate.now().until(dateExpirationLicence).getDays();
    }

    public void renouvelerLicence(LocalDate nouvelleDate) {
        this.dateExpirationLicence = nouvelleDate;
    }

    // Getters et Setters
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getNumeroLicence() { return numeroLicence; }
    public void setNumeroLicence(String numeroLicence) { this.numeroLicence = numeroLicence; }

    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }

    public Integer getExperienceAnnees() { return experienceAnnees; }
    public void setExperienceAnnees(Integer experienceAnnees) { this.experienceAnnees = experienceAnnees; }

    public LocalDate getDateCertification() { return dateCertification; }
    public void setDateCertification(LocalDate dateCertification) { this.dateCertification = dateCertification; }

    public LocalDate getDateExpirationLicence() { return dateExpirationLicence; }
    public void setDateExpirationLicence(LocalDate dateExpirationLicence) { this.dateExpirationLicence = dateExpirationLicence; }

    public String getTypeStaff() { return typeStaff; }
    public void setTypeStaff(String typeStaff) { this.typeStaff = typeStaff; }

    public Boolean getPeutPrescrire() { return peutPrescrire; }
    public void setPeutPrescrire(Boolean peutPrescrire) { this.peutPrescrire = peutPrescrire; }

    public Boolean getPeutDiagnostiquer() { return peutDiagnostiquer; }
    public void setPeutDiagnostiquer(Boolean peutDiagnostiquer) { this.peutDiagnostiquer = peutDiagnostiquer; }

    public Boolean getDisponibleUrgence() { return disponibleUrgence; }
    public void setDisponibleUrgence(Boolean disponibleUrgence) { this.disponibleUrgence = disponibleUrgence; }

    public String getNumeroUrgence() { return numeroUrgence; }
    public void setNumeroUrgence(String numeroUrgence) { this.numeroUrgence = numeroUrgence; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public Double getSalaire() { return salaire; }
    public void setSalaire(Double salaire) { this.salaire = salaire; }

    @Override
    public String toString() {
        return "StaffMedical{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", specialite='" + specialite + '\'' +
                ", typeStaff='" + typeStaff + '\'' +
                ", numeroLicence='" + numeroLicence + '\'' +
                '}';
    }
}
