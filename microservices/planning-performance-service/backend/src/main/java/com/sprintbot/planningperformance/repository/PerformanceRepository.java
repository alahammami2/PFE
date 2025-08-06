package com.sprintbot.planningperformance.repository;

import com.sprintbot.planningperformance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // Recherche par joueur
    List<Performance> findByJoueurIdOrderByIdDesc(Long joueurId);

    // Recherche par entraînement
    List<Performance> findByEntrainementIdOrderByNoteGlobaleDesc(Long entrainementId);

    // Recherche par joueur et entraînement
    Optional<Performance> findByEntrainementIdAndJoueurId(Long entrainementId, Long joueurId);

    // Recherche par évaluateur
    List<Performance> findByEvaluateurIdOrderByIdDesc(Long evaluateurId);

    // Performances d'un joueur pour une période
    @Query("SELECT p FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateEntrainement DESC")
    List<Performance> findPerformancesJoueurPeriode(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Moyenne des performances d'un joueur
    @Query("SELECT AVG(p.noteGlobale) FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin")
    BigDecimal getMoyennePerformanceJoueur(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Évolution des performances d'un joueur
    @Query("SELECT e.dateEntrainement, p.noteGlobale FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "ORDER BY e.dateEntrainement ASC")
    List<Object[]> getEvolutionPerformanceJoueur(@Param("joueurId") Long joueurId);

    // Meilleures performances d'un joueur
    @Query("SELECT p FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "ORDER BY p.noteGlobale DESC " +
           "LIMIT :limite")
    List<Performance> getMeilleuresPerformances(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);

    // Performances par type d'entraînement
    @Query("SELECT e.typeEntrainement, AVG(p.noteGlobale) FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "GROUP BY e.typeEntrainement")
    List<Object[]> getPerformancesParTypeEntrainement(@Param("joueurId") Long joueurId);

    // Comparaison avec la moyenne de l'équipe
    @Query("SELECT " +
           "(SELECT AVG(p1.noteGlobale) FROM Performance p1 JOIN p1.entrainement e1 " +
           " WHERE p1.joueurId = :joueurId AND e1.dateEntrainement BETWEEN :dateDebut AND :dateFin) as moyenneJoueur, " +
           "AVG(p.noteGlobale) as moyenneEquipe " +
           "FROM Performance p JOIN p.entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin")
    Object[] getComparaisonAvecEquipe(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Top performers pour une période
    @Query("SELECT p.joueurId, AVG(p.noteGlobale) as moyenne FROM Performance p JOIN p.entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY p.joueurId " +
           "ORDER BY moyenne DESC " +
           "LIMIT :limite")
    List<Object[]> getTopPerformers(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("limite") int limite);

    // Performances avec objectifs atteints
    @Query("SELECT p FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId AND p.objectifsAtteints = true " +
           "ORDER BY e.dateEntrainement DESC")
    List<Performance> getPerformancesAvecObjectifsAtteints(@Param("joueurId") Long joueurId);

    // Statistiques détaillées par joueur
    @Query("SELECT " +
           "AVG(p.noteGlobale) as moyenneGlobale, " +
           "AVG(p.noteTechnique) as moyenneTechnique, " +
           "AVG(p.notePhysique) as moyennePhysique, " +
           "AVG(p.noteMental) as moyenneMental, " +
           "COUNT(CASE WHEN p.objectifsAtteints = true THEN 1 END) as objectifsAtteints, " +
           "COUNT(p) as totalEvaluations " +
           "FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin")
    Object[] getStatistiquesDetailleesJoueur(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Performances récentes d'un joueur
    @Query("SELECT p FROM Performance p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "ORDER BY e.dateEntrainement DESC, p.dateEvaluation DESC " +
           "LIMIT :limite")
    List<Performance> getPerformancesRecentes(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);

    // Vérifier si une performance existe
    @Query("SELECT COUNT(p) > 0 FROM Performance p " +
           "WHERE p.entrainement.id = :entrainementId AND p.joueurId = :joueurId")
    boolean existsPerformance(@Param("entrainementId") Long entrainementId, @Param("joueurId") Long joueurId);
}
