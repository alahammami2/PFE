package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plannings")
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ID du planning est obligatoire")
    @Size(max = 100)
    @Column(name = "id_planning", nullable = false, unique = true, length = 100)
    private String idPlanning;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 200)
    @Column(name = "lieu", length = 200)
    private String lieu;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 50)
    @Column(name = "type_activite", length = 50)
    private String typeActivite;

    @Column(name = "id_coach", length = 100)
    private String idCoach;

    @Column(name = "id_equipe", length = 100)
    private String idEquipe;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @OneToMany(mappedBy = "planning", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evenement> evenements = new ArrayList<>();

    // Constructeurs
    public Planning() {}

    public Planning(String idPlanning, LocalDate date, String lieu, Equipe equipe, Coach coach) {
        this.idPlanning = idPlanning;
        this.date = date;
        this.lieu = lieu;
        this.equipe = equipe;
        this.coach = coach;
    }

    // Méthodes métier
    public void acceder() {
        // Logique pour accéder au planning
    }

    public void consulter() {
        // Logique pour consulter le planning
    }

    public void planifierEntrainements() {
        // Logique pour planifier les entraînements
    }

    public void lancerPlanificationOptionnelle() {
        // Logique pour lancer une planification optionnelle
    }

    public void ajouterEvenement(Evenement evenement) {
        this.evenements.add(evenement);
        evenement.setPlanning(this);
    }

    public void retirerEvenement(Evenement evenement) {
        this.evenements.remove(evenement);
        evenement.setPlanning(null);
    }

    public boolean estPasse() {
        return this.date.isBefore(LocalDate.now());
    }

    public boolean estAujourdhui() {
        return this.date.equals(LocalDate.now());
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdPlanning() { return idPlanning; }
    public void setIdPlanning(String idPlanning) { this.idPlanning = idPlanning; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTypeActivite() { return typeActivite; }
    public void setTypeActivite(String typeActivite) { this.typeActivite = typeActivite; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public Coach getCoach() { return coach; }
    public void setCoach(Coach coach) { this.coach = coach; }

    public List<Evenement> getEvenements() { return evenements; }
    public void setEvenements(List<Evenement> evenements) { this.evenements = evenements; }
}


