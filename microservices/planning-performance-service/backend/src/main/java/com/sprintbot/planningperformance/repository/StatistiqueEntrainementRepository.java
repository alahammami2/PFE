package com.sprintbot.planningperformance.repository;

import com.sprintbot.planningperformance.entity.StatistiqueEntrainement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatistiqueEntrainementRepository extends JpaRepository<StatistiqueEntrainement, Long> {

    // Recherche par joueur
    List<StatistiqueEntrainement> findByJoueurIdOrderByAnneeDescMoisDesc(Long joueurId);

    // Recherche par joueur et période
    Optional<StatistiqueEntrainement> findByJoueurIdAndMoisAndAnnee(Long joueurId, Integer mois, Integer annee);

    // Statistiques d'une année pour un joueur
    List<StatistiqueEntrainement> findByJoueurIdAndAnneeOrderByMoisAsc(Long joueurId, Integer annee);

    // Statistiques d'un mois pour tous les joueurs
    List<StatistiqueEntrainement> findByMoisAndAnneeOrderByTauxPresenceDesc(Integer mois, Integer annee);

    // Joueurs avec le meilleur taux de présence
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE s.mois = :mois AND s.annee = :annee " +
           "ORDER BY s.tauxPresence DESC " +
           "LIMIT :limite")
    List<StatistiqueEntrainement> getJoueursAvecMeilleurTauxPresence(
            @Param("mois") Integer mois,
            @Param("annee") Integer annee,
            @Param("limite") int limite);

    // Joueurs avec les meilleures performances
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE s.mois = :mois AND s.annee = :annee " +
           "ORDER BY s.moyennePerformance DESC " +
           "LIMIT :limite")
    List<StatistiqueEntrainement> getJoueursAvecMeilleuresPerformances(
            @Param("mois") Integer mois,
            @Param("annee") Integer annee,
            @Param("limite") int limite);

    // Évolution annuelle d'un joueur
    @Query("SELECT s.mois, s.tauxPresence, s.moyennePerformance, s.progressionMensuelle " +
           "FROM StatistiqueEntrainement s " +
           "WHERE s.joueurId = :joueurId AND s.annee = :annee " +
           "ORDER BY s.mois ASC")
    List<Object[]> getEvolutionAnnuelleJoueur(@Param("joueurId") Long joueurId, @Param("annee") Integer annee);

    // Moyennes globales par mois
    @Query("SELECT s.mois, s.annee, " +
           "AVG(s.tauxPresence) as moyenneTauxPresence, " +
           "AVG(s.moyennePerformance) as moyennePerformanceGlobale " +
           "FROM StatistiqueEntrainement s " +
           "WHERE s.annee = :annee " +
           "GROUP BY s.mois, s.annee " +
           "ORDER BY s.mois ASC")
    List<Object[]> getMoyennesGlobalesParMois(@Param("annee") Integer annee);

    // Joueurs en progression
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE s.mois = :mois AND s.annee = :annee " +
           "AND s.progressionMensuelle > 0 " +
           "ORDER BY s.progressionMensuelle DESC")
    List<StatistiqueEntrainement> getJoueursEnProgression(@Param("mois") Integer mois, @Param("annee") Integer annee);

    // Joueurs en régression
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE s.mois = :mois AND s.annee = :annee " +
           "AND s.progressionMensuelle < 0 " +
           "ORDER BY s.progressionMensuelle ASC")
    List<StatistiqueEntrainement> getJoueursEnRegression(@Param("mois") Integer mois, @Param("annee") Integer annee);

    // Statistiques récentes d'un joueur
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE s.joueurId = :joueurId " +
           "ORDER BY s.annee DESC, s.mois DESC " +
           "LIMIT :limite")
    List<StatistiqueEntrainement> getStatistiquesRecentes(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);

    // Comparaison avec la moyenne de l'équipe
    @Query("SELECT " +
           "(SELECT s1.tauxPresence FROM StatistiqueEntrainement s1 " +
           " WHERE s1.joueurId = :joueurId AND s1.mois = :mois AND s1.annee = :annee) as tauxJoueur, " +
           "AVG(s.tauxPresence) as tauxMoyenEquipe, " +
           "(SELECT s1.moyennePerformance FROM StatistiqueEntrainement s1 " +
           " WHERE s1.joueurId = :joueurId AND s1.mois = :mois AND s1.annee = :annee) as performanceJoueur, " +
           "AVG(s.moyennePerformance) as performanceMoyenneEquipe " +
           "FROM StatistiqueEntrainement s " +
           "WHERE s.mois = :mois AND s.annee = :annee")
    Object[] getComparaisonAvecEquipe(
            @Param("joueurId") Long joueurId,
            @Param("mois") Integer mois,
            @Param("annee") Integer annee);

    // Tendance sur plusieurs mois
    @Query("SELECT s.mois, s.annee, s.moyennePerformance FROM StatistiqueEntrainement s " +
           "WHERE s.joueurId = :joueurId " +
           "ORDER BY s.annee DESC, s.mois DESC " +
           "LIMIT :nombreMois")
    List<Object[]> getTendancePerformance(
            @Param("joueurId") Long joueurId,
            @Param("nombreMois") int nombreMois);

    // Méthodes supplémentaires pour compatibilité avec le service
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE s.joueurId = :joueurId " +
           "AND ((s.annee = :anneeDebut AND s.mois >= :moisDebut) OR " +
           "(s.annee > :anneeDebut AND s.annee < :anneeFin) OR " +
           "(s.annee = :anneeFin AND s.mois <= :moisFin)) " +
           "ORDER BY s.annee ASC, s.mois ASC")
    List<StatistiqueEntrainement> findByJoueurIdAndPeriode(
            @Param("joueurId") Long joueurId,
            @Param("anneeDebut") Integer anneeDebut,
            @Param("moisDebut") Integer moisDebut,
            @Param("anneeFin") Integer anneeFin,
            @Param("moisFin") Integer moisFin);

    // Recherche par mois et année
    List<StatistiqueEntrainement> findByMoisAndAnnee(Integer mois, Integer annee);

    // Recherche par période (pour rapport équipe)
    @Query("SELECT s FROM StatistiqueEntrainement s WHERE " +
           "((s.annee = :anneeDebut AND s.mois >= :moisDebut) OR " +
           "(s.annee > :anneeDebut AND s.annee < :anneeFin) OR " +
           "(s.annee = :anneeFin AND s.mois <= :moisFin)) " +
           "ORDER BY s.annee ASC, s.mois ASC")
    List<StatistiqueEntrainement> findByPeriode(
            @Param("anneeDebut") Integer anneeDebut,
            @Param("moisDebut") Integer moisDebut,
            @Param("anneeFin") Integer anneeFin,
            @Param("moisFin") Integer moisFin);

    // Méthodes pour les statistiques avancées
    List<StatistiqueEntrainement> findByJoueurIdOrderByTauxPresenceDesc(Long joueurId);
    List<StatistiqueEntrainement> findByJoueurIdOrderByAnneeAscMoisAsc(Long joueurId);
    List<StatistiqueEntrainement> findByMoisAndAnneeOrderByTauxPresenceAsc(Integer mois, Integer annee);
}
