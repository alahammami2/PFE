package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "demandes_administratives")
public class DemandeAdministrative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID de la demande est obligatoire")
    @Size(max = 100)
    @Column(name = "id_demande", nullable = false, unique = true, length = 100)
    private String idDemande;

    @Size(max = 100)
    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "date_soumission", nullable = false)
    private LocalDate dateSoumission;

    @Size(max = 50)
    @Column(name = "statut", length = 50)
    private String statut;

    @Column(name = "id_admin", length = 100)
    private String idAdmin;

    @Column(name = "id_joueur", length = 100)
    private String idJoueur;

    @Column(name = "id_coach", length = 100)
    private String idCoach;

    @Column(name = "id_medical", length = 100)
    private String idMedical;

    @Column(name = "id_financier", length = 100)
    private String idFinancier;

    // Relations multiples selon qui fait la demande
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_medical_id")
    private StaffMedical staffMedical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_financier_id")
    private ResponsableFinancier responsableFinancier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    // Constructeurs
    public DemandeAdministrative() {
        this.dateSoumission = LocalDate.now();
        this.statut = "EN_ATTENTE";
    }

    public DemandeAdministrative(String idDemande, String type, String details, Administrateur administrateur) {
        this();
        this.idDemande = idDemande;
        this.type = type;
        this.details = details;
        this.administrateur = administrateur;
    }

    // Méthodes métier
    public void soumettre() {
        this.dateSoumission = LocalDate.now();
        this.statut = "EN_ATTENTE";
    }

    public void consulter() {
        // Logique pour consulter la demande
    }

    public void valider() {
        this.statut = "VALIDEE";
    }

    public void rejeter() {
        this.statut = "REJETEE";
    }

    public void traiter() {
        this.statut = "EN_TRAITEMENT";
    }

    public boolean estEnAttente() {
        return "EN_ATTENTE".equals(this.statut);
    }

    public boolean estValidee() {
        return "VALIDEE".equals(this.statut);
    }

    public boolean estRejetee() {
        return "REJETEE".equals(this.statut);
    }

    public long getNombreJoursDepuisSoumission() {
        return this.dateSoumission.until(LocalDate.now()).getDays();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdDemande() { return idDemande; }
    public void setIdDemande(String idDemande) { this.idDemande = idDemande; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDate getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDate dateSoumission) { this.dateSoumission = dateSoumission; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Administrateur getAdministrateur() { return administrateur; }
    public void setAdministrateur(Administrateur administrateur) { this.administrateur = administrateur; }

    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }

    public Coach getCoach() { return coach; }
    public void setCoach(Coach coach) { this.coach = coach; }

    public StaffMedical getStaffMedical() { return staffMedical; }
    public void setStaffMedical(StaffMedical staffMedical) { this.staffMedical = staffMedical; }

    public ResponsableFinancier getResponsableFinancier() { return responsableFinancier; }
    public void setResponsableFinancier(ResponsableFinancier responsableFinancier) { this.responsableFinancier = responsableFinancier; }
}

