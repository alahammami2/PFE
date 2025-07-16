package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Performance;
import com.volleyball.sprintbot.entity.Joueur;
import com.volleyball.sprintbot.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // Recherche par joueur
    List<Performance> findByJoueur(Joueur joueur);

    // Recherche par coach
    List<Performance> findByCoach(Coach coach);

    // Recherche par note supérieure ou égale
    List<Performance> findByNoteIntegerGreaterThanEqual(Integer noteMin);

    // Recherche par note inférieure ou égale
    List<Performance> findByNoteIntegerLessThanEqual(Integer noteMax);

    // Recherche par nombre de matchs joués
    List<Performance> findByMatchsJoues(Integer matchsJoues);

    // Recherche par nombre de matchs joués supérieur
    List<Performance> findByMatchsJouesGreaterThan(Integer matchsJoues);

    // Compter les performances par joueur
    long countByJoueur(Joueur joueur);

    // Compter les performances par coach
    long countByCoach(Coach coach);

    // Requête personnalisée pour obtenir la note moyenne d'un joueur
    @Query("SELECT AVG(p.noteInteger) FROM Performance p WHERE p.joueur = :joueur")
    Double findNoteMoyenneByJoueur(@Param("joueur") Joueur joueur);

    // Requête personnalisée pour obtenir le total des matchs joués par un joueur
    @Query("SELECT SUM(p.matchsJoues) FROM Performance p WHERE p.joueur = :joueur")
    Integer findTotalMatchsJouesByJoueur(@Param("joueur") Joueur joueur);

    // Requête personnalisée pour obtenir les meilleures performances (note >= seuil)
    @Query("SELECT p FROM Performance p WHERE p.noteInteger >= :seuil ORDER BY p.noteInteger DESC")
    List<Performance> findMeilleuresPerformances(@Param("seuil") Integer seuil);

    // Requête personnalisée pour obtenir les performances à améliorer (note < seuil)
    @Query("SELECT p FROM Performance p WHERE p.noteInteger < :seuil ORDER BY p.noteInteger ASC")
    List<Performance> findPerformancesAameliorer(@Param("seuil") Integer seuil);

    // Requête personnalisée pour obtenir les statistiques par équipe
    @Query("SELECT j.equipe, AVG(p.noteInteger), SUM(p.matchsJoues), COUNT(p) " +
           "FROM Performance p JOIN p.joueur j " +
           "GROUP BY j.equipe")
    List<Object[]> findStatistiquesParEquipe();

    // Requête personnalisée pour obtenir le classement des joueurs par note moyenne
    @Query("SELECT p.joueur, AVG(p.noteInteger) as noteMoyenne " +
           "FROM Performance p " +
           "GROUP BY p.joueur " +
           "ORDER BY noteMoyenne DESC")
    List<Object[]> findClassementJoueurs();

    // Requête personnalisée pour obtenir les performances récentes d'un joueur
    @Query("SELECT p FROM Performance p WHERE p.joueur = :joueur " +
           "ORDER BY p.id DESC")
    List<Performance> findPerformancesRecentesByJoueur(@Param("joueur") Joueur joueur);
}
