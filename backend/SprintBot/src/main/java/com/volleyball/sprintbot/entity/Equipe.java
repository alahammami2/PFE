package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipes")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(max = 100)
    @Column(name = "nom_equipe", nullable = false, length = 100)
    private String nomEquipe;

    @Size(max = 50)
    @Column(name = "entraineur", length = 50)
    private String entraineur;

    // Relations
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Joueur> joueurs = new ArrayList<>();

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coach> coaches = new ArrayList<>();

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Planning> plannings = new ArrayList<>();

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Budget> budgets = new ArrayList<>();

    // Constructeurs
    public Equipe() {}

    public Equipe(String nomEquipe, String entraineur) {
        this.nomEquipe = nomEquipe;
        this.entraineur = entraineur;
    }

    // Méthodes métier
    public void ajouterJoueur(Joueur joueur) {
        this.joueurs.add(joueur);
        joueur.setEquipe(this);
    }

    public void retirerJoueur(Joueur joueur) {
        this.joueurs.remove(joueur);
        joueur.setEquipe(null);
    }

    public void ajouterCoach(Coach coach) {
        this.coaches.add(coach);
        coach.setEquipe(this);
    }

    public int getNombreJoueurs() {
        return this.joueurs.size();
    }

    public List<Joueur> getJoueursActifs() {
        return this.joueurs.stream()
                .filter(joueur -> joueur.getActif())
                .toList();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomEquipe() { return nomEquipe; }
    public void setNomEquipe(String nomEquipe) { this.nomEquipe = nomEquipe; }

    public String getEntraineur() { return entraineur; }
    public void setEntraineur(String entraineur) { this.entraineur = entraineur; }

    public List<Joueur> getJoueurs() { return joueurs; }
    public void setJoueurs(List<Joueur> joueurs) { this.joueurs = joueurs; }

    public List<Coach> getCoaches() { return coaches; }
    public void setCoaches(List<Coach> coaches) { this.coaches = coaches; }

    public List<Planning> getPlannings() { return plannings; }
    public void setPlannings(List<Planning> plannings) { this.plannings = plannings; }

    public List<Budget> getBudgets() { return budgets; }
    public void setBudgets(List<Budget> budgets) { this.budgets = budgets; }
}
