package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "evenements")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID de l'événement est obligatoire")
    @Size(max = 100)
    @Column(name = "id_evenement", length = 100)
    private String idEvenement;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "heure_fin")
    private LocalTime heureFin;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "lieu", length = 200)
    private String lieu;

    @Column(name = "id_performance", length = 100)
    private String idPerformance;

    @Column(name = "id_planning", length = 100)
    private String idPlanning;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planning_id")
    private Planning planning;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    // Constructeurs
    public Evenement() {}

    public Evenement(String idEvenement, LocalDate date, LocalTime heureDebut, String type, String lieu) {
        this.idEvenement = idEvenement;
        this.date = date;
        this.heureDebut = heureDebut;
        this.type = type;
        this.lieu = lieu;
    }

    // Méthodes métier
    public void consulter() {
        // Logique pour consulter l'événement
    }

    public void filtrer() {
        // Logique pour filtrer les événements
    }

    public void mettreAJour() {
        // Logique pour mettre à jour l'événement
    }

    public boolean estPasse() {
        return this.date.isBefore(LocalDate.now()) || 
               (this.date.equals(LocalDate.now()) && 
                this.heureFin != null && 
                this.heureFin.isBefore(LocalTime.now()));
    }

    public boolean estEnCours() {
        if (!this.date.equals(LocalDate.now())) {
            return false;
        }
        LocalTime maintenant = LocalTime.now();
        return (this.heureDebut == null || !maintenant.isBefore(this.heureDebut)) &&
               (this.heureFin == null || !maintenant.isAfter(this.heureFin));
    }

    public long getDureeEnMinutes() {
        if (this.heureDebut != null && this.heureFin != null) {
            return java.time.Duration.between(this.heureDebut, this.heureFin).toMinutes();
        }
        return 0;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdEvenement() { return idEvenement; }
    public void setIdEvenement(String idEvenement) { this.idEvenement = idEvenement; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Planning getPlanning() { return planning; }
    public void setPlanning(Planning planning) { this.planning = planning; }

    public Performance getPerformance() { return performance; }
    public void setPerformance(Performance performance) { this.performance = performance; }
}

