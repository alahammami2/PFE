package com.sprintbot.planningperformance.repository;

import com.sprintbot.planningperformance.entity.ObjectifIndividuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjectifIndividuelRepository extends JpaRepository<ObjectifIndividuel, Long> {

    // Recherche par joueur
    List<ObjectifIndividuel> findByJoueurIdOrderByDateCreationDesc(Long joueurId);

    // Recherche par coach
    List<ObjectifIndividuel> findByCoachIdOrderByDateCreationDesc(Long coachId);

    // Recherche par type d'objectif
    List<ObjectifIndividuel> findByTypeObjectifOrderByDateCreationDesc(
            ObjectifIndividuel.TypeObjectif typeObjectif);

    // Recherche par statut
    List<ObjectifIndividuel> findByStatutOrderByDateCreationDesc(
            ObjectifIndividuel.StatutObjectif statut);

    // Objectifs d'un joueur par statut
    List<ObjectifIndividuel> findByJoueurIdAndStatutOrderByDateCreationDesc(
            Long joueurId, ObjectifIndividuel.StatutObjectif statut);

    // Objectifs en cours d'un joueur
    @Query("SELECT o FROM ObjectifIndividuel o WHERE o.joueurId = :joueurId " +
           "AND o.statut = 'EN_COURS' " +
           "ORDER BY o.dateEcheance ASC NULLS LAST")
    List<ObjectifIndividuel> getObjectifsEnCours(@Param("joueurId") Long joueurId);

    // Objectifs échus
    @Query("SELECT o FROM ObjectifIndividuel o WHERE o.dateEcheance < :dateActuelle " +
           "AND o.statut = 'EN_COURS' " +
           "ORDER BY o.dateEcheance ASC")
    List<ObjectifIndividuel> getObjectifsEchus(@Param("dateActuelle") LocalDate dateActuelle);

    // Objectifs proches de l'échéance
    @Query("SELECT o FROM ObjectifIndividuel o WHERE o.dateEcheance BETWEEN :dateActuelle AND :dateLimite " +
           "AND o.statut = 'EN_COURS' " +
           "ORDER BY o.dateEcheance ASC")
    List<ObjectifIndividuel> getObjectifsProchesEcheance(
            @Param("dateActuelle") LocalDate dateActuelle,
            @Param("dateLimite") LocalDate dateLimite);

    // Statistiques d'objectifs par joueur
    @Query("SELECT o.statut, COUNT(o) FROM ObjectifIndividuel o " +
           "WHERE o.joueurId = :joueurId " +
           "GROUP BY o.statut")
    List<Object[]> getStatistiquesObjectifsJoueur(@Param("joueurId") Long joueurId);

    // Progression moyenne par type d'objectif
    @Query("SELECT o.typeObjectif, AVG(o.progression) FROM ObjectifIndividuel o " +
           "WHERE o.joueurId = :joueurId " +
           "GROUP BY o.typeObjectif")
    List<Object[]> getProgressionMoyenneParType(@Param("joueurId") Long joueurId);

    // Objectifs les plus avancés
    @Query("SELECT o FROM ObjectifIndividuel o WHERE o.joueurId = :joueurId " +
           "AND o.statut = 'EN_COURS' " +
           "ORDER BY o.progression DESC " +
           "LIMIT :limite")
    List<ObjectifIndividuel> getObjectifsLesPlusAvances(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);

    // Objectifs par coach et période
    @Query("SELECT o FROM ObjectifIndividuel o WHERE o.coachId = :coachId " +
           "AND o.dateCreation BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY o.dateCreation DESC")
    List<ObjectifIndividuel> getObjectifsCoachPeriode(
            @Param("coachId") Long coachId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Taux de réussite des objectifs d'un joueur
    @Query("SELECT " +
           "COUNT(CASE WHEN o.statut = 'ATTEINT' THEN 1 END) * 100.0 / COUNT(o) " +
           "FROM ObjectifIndividuel o " +
           "WHERE o.joueurId = :joueurId " +
           "AND o.dateCreation BETWEEN :dateDebut AND :dateFin")
    Double getTauxReussiteObjectifs(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Objectifs récents d'un joueur
    @Query("SELECT o FROM ObjectifIndividuel o WHERE o.joueurId = :joueurId " +
           "ORDER BY o.dateCreation DESC " +
           "LIMIT :limite")
    List<ObjectifIndividuel> getObjectifsRecents(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);

    // Recherche par mots-clés
    @Query("SELECT o FROM ObjectifIndividuel o WHERE " +
           "LOWER(o.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(o.description) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "ORDER BY o.dateCreation DESC")
    List<ObjectifIndividuel> rechercherParMotsCles(@Param("motCle") String motCle);
}
