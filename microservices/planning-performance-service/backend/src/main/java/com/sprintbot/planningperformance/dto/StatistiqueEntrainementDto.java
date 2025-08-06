package com.sprintbot.planningperformance.dto;

import com.sprintbot.planningperformance.entity.StatistiqueEntrainement;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StatistiqueEntrainementDto {
    
    private Long id;
    
    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long joueurId;
    
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    private int mois;
    
    @Min(value = 2020, message = "L'année doit être valide")
    private int annee;
    
    @Min(value = 0, message = "Le nombre d'entraînements doit être positif")
    private int nombreEntrainements;
    
    @Min(value = 0, message = "Le nombre de présences doit être positif")
    private int nombrePresences;
    
    @Min(value = 0, message = "Le nombre d'absences doit être positif")
    private int nombreAbsences;
    
    @DecimalMin(value = "0.0", message = "Le taux de présence doit être positif")
    @DecimalMax(value = "100.0", message = "Le taux de présence ne peut pas dépasser 100%")
    @Digits(integer = 3, fraction = 2, message = "Le taux de présence doit avoir au maximum 3 chiffres avant et 2 après la virgule")
    private BigDecimal tauxPresence;
    
    @DecimalMin(value = "0.0", message = "La performance moyenne doit être positive")
    @DecimalMax(value = "10.0", message = "La performance moyenne ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La performance moyenne doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal performanceMoyenne;
    
    @DecimalMin(value = "0.0", message = "La note technique moyenne doit être positive")
    @DecimalMax(value = "10.0", message = "La note technique moyenne ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note technique moyenne doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal noteTechniqueMoyenne;
    
    @DecimalMin(value = "0.0", message = "La note physique moyenne doit être positive")
    @DecimalMax(value = "10.0", message = "La note physique moyenne ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note physique moyenne doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal notePhysiqueMoyenne;
    
    @DecimalMin(value = "0.0", message = "La note mentale moyenne doit être positive")
    @DecimalMax(value = "10.0", message = "La note mentale moyenne ne peut pas dépasser 10")
    @Digits(integer = 2, fraction = 2, message = "La note mentale moyenne doit avoir au maximum 2 chiffres avant et 2 après la virgule")
    private BigDecimal noteMentaleMoyenne;
    
    @Min(value = 0, message = "Le nombre d'objectifs atteints doit être positif")
    private int nombreObjectifsAtteints;
    
    private LocalDateTime dateCalcul;
    
    // Informations calculées pour l'affichage
    private String nomMois;
    private BigDecimal evolutionPerformance; // Par rapport au mois précédent
    private BigDecimal evolutionPresence; // Par rapport au mois précédent
    private String tendance; // "PROGRESSION", "STABLE", "REGRESSION"
    private String profil; // "EXCELLENT", "BON", "MOYEN", "FAIBLE"
    
    // Comparaison avec l'équipe
    private BigDecimal performanceMoyenneEquipe;
    private BigDecimal tauxPresenceMoyenEquipe;
    private Integer classementPerformance;
    private Integer classementPresence;
    
    // Constructeurs
    public StatistiqueEntrainementDto() {}
    
    public StatistiqueEntrainementDto(StatistiqueEntrainement statistique) {
        this.id = statistique.getId();
        this.joueurId = statistique.getJoueurId();
        this.mois = statistique.getMois();
        this.annee = statistique.getAnnee();
        this.nombreEntrainements = statistique.getNombreEntrainements();
        this.nombrePresences = statistique.getNombrePresences();
        this.nombreAbsences = statistique.getNombreAbsences();
        this.tauxPresence = statistique.getTauxPresence();
        this.performanceMoyenne = statistique.getPerformanceMoyenne();
        this.noteTechniqueMoyenne = statistique.getNoteTechniqueMoyenne();
        this.notePhysiqueMoyenne = statistique.getNotePhysiqueMoyenne();
        this.noteMentaleMoyenne = statistique.getNoteMentaleMoyenne();
        this.nombreObjectifsAtteints = statistique.getNombreObjectifsAtteints();
        this.dateCalcul = statistique.getDateCalcul();
        
        // Calculs supplémentaires
        this.nomMois = getNomMois(this.mois);
        this.profil = calculateProfil(statistique);
    }
    
    // Méthode pour convertir vers l'entité
    public StatistiqueEntrainement toEntity() {
        StatistiqueEntrainement statistique = new StatistiqueEntrainement();
        statistique.setId(this.id);
        statistique.setJoueurId(this.joueurId);
        statistique.setMois(this.mois);
        statistique.setAnnee(this.annee);
        statistique.setNombreEntrainements(this.nombreEntrainements);
        statistique.setNombrePresences(this.nombrePresences);
        statistique.setNombreAbsences(this.nombreAbsences);
        statistique.setTauxPresence(this.tauxPresence);
        statistique.setPerformanceMoyenne(this.performanceMoyenne);
        statistique.setNoteTechniqueMoyenne(this.noteTechniqueMoyenne);
        statistique.setNotePhysiqueMoyenne(this.notePhysiqueMoyenne);
        statistique.setNoteMentaleMoyenne(this.noteMentaleMoyenne);
        statistique.setNombreObjectifsAtteints(this.nombreObjectifsAtteints);
        statistique.setDateCalcul(this.dateCalcul != null ? this.dateCalcul : LocalDateTime.now());
        return statistique;
    }
    
    // Méthodes utilitaires
    private String getNomMois(int mois) {
        String[] noms = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };
        return (mois >= 1 && mois <= 12) ? noms[mois - 1] : "Inconnu";
    }
    
    private String calculateProfil(StatistiqueEntrainement statistique) {
        if (statistique.getPerformanceMoyenne() == null || statistique.getTauxPresence() == null) {
            return "INDETERMINE";
        }
        
        double performance = statistique.getPerformanceMoyenne().doubleValue();
        double presence = statistique.getTauxPresence().doubleValue();
        
        if (performance >= 8.0 && presence >= 90.0) return "EXCELLENT";
        if (performance >= 6.5 && presence >= 80.0) return "BON";
        if (performance >= 5.0 && presence >= 70.0) return "MOYEN";
        return "FAIBLE";
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }
    
    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }
    
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    
    public int getNombreEntrainements() { return nombreEntrainements; }
    public void setNombreEntrainements(int nombreEntrainements) { this.nombreEntrainements = nombreEntrainements; }
    
    public int getNombrePresences() { return nombrePresences; }
    public void setNombrePresences(int nombrePresences) { this.nombrePresences = nombrePresences; }
    
    public int getNombreAbsences() { return nombreAbsences; }
    public void setNombreAbsences(int nombreAbsences) { this.nombreAbsences = nombreAbsences; }
    
    public BigDecimal getTauxPresence() { return tauxPresence; }
    public void setTauxPresence(BigDecimal tauxPresence) { this.tauxPresence = tauxPresence; }
    
    public BigDecimal getPerformanceMoyenne() { return performanceMoyenne; }
    public void setPerformanceMoyenne(BigDecimal performanceMoyenne) { this.performanceMoyenne = performanceMoyenne; }
    
    public BigDecimal getNoteTechniqueMoyenne() { return noteTechniqueMoyenne; }
    public void setNoteTechniqueMoyenne(BigDecimal noteTechniqueMoyenne) { this.noteTechniqueMoyenne = noteTechniqueMoyenne; }
    
    public BigDecimal getNotePhysiqueMoyenne() { return notePhysiqueMoyenne; }
    public void setNotePhysiqueMoyenne(BigDecimal notePhysiqueMoyenne) { this.notePhysiqueMoyenne = notePhysiqueMoyenne; }
    
    public BigDecimal getNoteMentaleMoyenne() { return noteMentaleMoyenne; }
    public void setNoteMentaleMoyenne(BigDecimal noteMentaleMoyenne) { this.noteMentaleMoyenne = noteMentaleMoyenne; }
    
    public int getNombreObjectifsAtteints() { return nombreObjectifsAtteints; }
    public void setNombreObjectifsAtteints(int nombreObjectifsAtteints) { this.nombreObjectifsAtteints = nombreObjectifsAtteints; }
    
    public LocalDateTime getDateCalcul() { return dateCalcul; }
    public void setDateCalcul(LocalDateTime dateCalcul) { this.dateCalcul = dateCalcul; }
    
    public String getNomMois() { return nomMois; }
    public void setNomMois(String nomMois) { this.nomMois = nomMois; }
    
    public BigDecimal getEvolutionPerformance() { return evolutionPerformance; }
    public void setEvolutionPerformance(BigDecimal evolutionPerformance) { this.evolutionPerformance = evolutionPerformance; }
    
    public BigDecimal getEvolutionPresence() { return evolutionPresence; }
    public void setEvolutionPresence(BigDecimal evolutionPresence) { this.evolutionPresence = evolutionPresence; }
    
    public String getTendance() { return tendance; }
    public void setTendance(String tendance) { this.tendance = tendance; }
    
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    
    public BigDecimal getPerformanceMoyenneEquipe() { return performanceMoyenneEquipe; }
    public void setPerformanceMoyenneEquipe(BigDecimal performanceMoyenneEquipe) { this.performanceMoyenneEquipe = performanceMoyenneEquipe; }
    
    public BigDecimal getTauxPresenceMoyenEquipe() { return tauxPresenceMoyenEquipe; }
    public void setTauxPresenceMoyenEquipe(BigDecimal tauxPresenceMoyenEquipe) { this.tauxPresenceMoyenEquipe = tauxPresenceMoyenEquipe; }
    
    public Integer getClassementPerformance() { return classementPerformance; }
    public void setClassementPerformance(Integer classementPerformance) { this.classementPerformance = classementPerformance; }
    
    public Integer getClassementPresence() { return classementPresence; }
    public void setClassementPresence(Integer classementPresence) { this.classementPresence = classementPresence; }
}
