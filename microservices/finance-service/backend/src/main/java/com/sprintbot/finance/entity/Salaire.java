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
 * Entité représentant un salaire
 * Gère la paie du personnel de l'équipe
 */
@Entity
@Table(name = "salaires", schema = "finance")
@EntityListeners(AuditingEntityListener.class)
public class Salaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'employé est obligatoire")
    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @NotNull(message = "La période est obligatoire")
    @Column(name = "periode", nullable = false)
    private LocalDate periode; // Premier jour du mois de paie

    @NotNull(message = "Le salaire brut est obligatoire")
    @DecimalMin(value = "0.0", message = "Le salaire brut doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Format de salaire invalide")
    @Column(name = "salaire_brut", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaireBrut;

    @DecimalMin(value = "0.0", message = "Les primes doivent être positives")
    @Digits(integer = 10, fraction = 2, message = "Format de primes invalide")
    @Column(name = "primes", precision = 12, scale = 2)
    private BigDecimal primes = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Les bonus doivent être positifs")
    @Digits(integer = 10, fraction = 2, message = "Format de bonus invalide")
    @Column(name = "bonus", precision = 12, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Les déductions doivent être positives")
    @Digits(integer = 10, fraction = 2, message = "Format de déductions invalide")
    @Column(name = "deductions", precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Les cotisations sociales doivent être positives")
    @Digits(integer = 10, fraction = 2, message = "Format de cotisations invalide")
    @Column(name = "cotisations_sociales", precision = 12, scale = 2)
    private BigDecimal cotisationsSociales = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "L'impôt doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Format d'impôt invalide")
    @Column(name = "impot", precision = 12, scale = 2)
    private BigDecimal impot = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Le salaire net doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Format de salaire net invalide")
    @Column(name = "salaire_net", precision = 12, scale = 2)
    private BigDecimal salaireNet;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutSalaire statut = StatutSalaire.CALCULE;

    @Column(name = "date_calcul")
    private LocalDateTime dateCalcul;

    @Column(name = "date_validation")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateValidation;

    @Column(name = "date_versement")
    private LocalDate dateVersement;

    @Column(name = "validateur_id")
    private Long validateurId;

    @Column(name = "heures_travaillees")
    private Integer heuresTravaillees;

    @Column(name = "heures_supplementaires")
    private Integer heuresSupplementaires = 0;

    @Column(name = "jours_conges")
    private Integer joursConges = 0;

    @Column(name = "jours_absence")
    private Integer joursAbsence = 0;

    @Size(max = 255, message = "Le fichier fiche de paie ne peut pas dépasser 255 caractères")
    @Column(name = "fiche_paie", length = 255)
    private String fichePaie;

    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    @Column(name = "notes", length = 1000)
    private String notes;

    // Relations
    @OneToMany(mappedBy = "salaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ElementSalaire> elements = new ArrayList<>();

    // Audit
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public Salaire() {}

    public Salaire(Long employeId, LocalDate periode, BigDecimal salaireBrut) {
        this.employeId = employeId;
        this.periode = periode;
        this.salaireBrut = salaireBrut;
        this.statut = StatutSalaire.CALCULE;
        this.dateCalcul = LocalDateTime.now();
    }

    // Méthodes métier
    public void calculerSalaireNet() {
        BigDecimal totalBrut = salaireBrut.add(primes).add(bonus);
        BigDecimal totalDeductions = deductions.add(cotisationsSociales).add(impot);
        this.salaireNet = totalBrut.subtract(totalDeductions);
        
        if (this.salaireNet.compareTo(BigDecimal.ZERO) < 0) {
            this.salaireNet = BigDecimal.ZERO;
        }
    }

    public void valider(Long validateurId) {
        if (this.statut != StatutSalaire.CALCULE) {
            throw new IllegalStateException("Seuls les salaires calculés peuvent être validés");
        }
        
        this.statut = StatutSalaire.VALIDE;
        this.validateurId = validateurId;
        this.dateValidation = LocalDateTime.now();
    }

    public void marquerCommePaye(LocalDate dateVersement) {
        if (this.statut != StatutSalaire.VALIDE) {
            throw new IllegalStateException("Seuls les salaires validés peuvent être payés");
        }
        
        this.statut = StatutSalaire.PAYE;
        this.dateVersement = dateVersement;
    }

    public BigDecimal getTotalBrut() {
        return salaireBrut.add(primes).add(bonus);
    }

    public BigDecimal getTotalDeductions() {
        return deductions.add(cotisationsSociales).add(impot);
    }

    public boolean isCalcule() {
        return statut == StatutSalaire.CALCULE;
    }

    public boolean isValide() {
        return statut == StatutSalaire.VALIDE;
    }

    public boolean isPaye() {
        return statut == StatutSalaire.PAYE;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public LocalDate getPeriode() { return periode; }
    public void setPeriode(LocalDate periode) { this.periode = periode; }

    public BigDecimal getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(BigDecimal salaireBrut) { this.salaireBrut = salaireBrut; }

    public BigDecimal getPrimes() { return primes; }
    public void setPrimes(BigDecimal primes) { this.primes = primes; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }

    public BigDecimal getCotisationsSociales() { return cotisationsSociales; }
    public void setCotisationsSociales(BigDecimal cotisationsSociales) { this.cotisationsSociales = cotisationsSociales; }

    public BigDecimal getImpot() { return impot; }
    public void setImpot(BigDecimal impot) { this.impot = impot; }

    public BigDecimal getSalaireNet() { return salaireNet; }
    public void setSalaireNet(BigDecimal salaireNet) { this.salaireNet = salaireNet; }

    public StatutSalaire getStatut() { return statut; }
    public void setStatut(StatutSalaire statut) { this.statut = statut; }

    public LocalDateTime getDateCalcul() { return dateCalcul; }
    public void setDateCalcul(LocalDateTime dateCalcul) { this.dateCalcul = dateCalcul; }

    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }

    public LocalDate getDateVersement() { return dateVersement; }
    public void setDateVersement(LocalDate dateVersement) { this.dateVersement = dateVersement; }

    public Long getValidateurId() { return validateurId; }
    public void setValidateurId(Long validateurId) { this.validateurId = validateurId; }

    public Integer getHeuresTravaillees() { return heuresTravaillees; }
    public void setHeuresTravaillees(Integer heuresTravaillees) { this.heuresTravaillees = heuresTravaillees; }

    public Integer getHeuresSupplementaires() { return heuresSupplementaires; }
    public void setHeuresSupplementaires(Integer heuresSupplementaires) { this.heuresSupplementaires = heuresSupplementaires; }

    public Integer getJoursConges() { return joursConges; }
    public void setJoursConges(Integer joursConges) { this.joursConges = joursConges; }

    public Integer getJoursAbsence() { return joursAbsence; }
    public void setJoursAbsence(Integer joursAbsence) { this.joursAbsence = joursAbsence; }

    public String getFichePaie() { return fichePaie; }
    public void setFichePaie(String fichePaie) { this.fichePaie = fichePaie; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<ElementSalaire> getElements() { return elements; }
    public void setElements(List<ElementSalaire> elements) { this.elements = elements; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // Enum
    public enum StatutSalaire {
        CALCULE, VALIDE, PAYE, ANNULE
    }
}
