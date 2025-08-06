package com.sprintbot.planningperformance.repository;

import com.sprintbot.planningperformance.entity.Entrainement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntrainementRepository extends JpaRepository<Entrainement, Long> {

    // Recherche par coach
    List<Entrainement> findByCoachIdOrderByDateEntrainementDesc(Long coachId);

    // Recherche par date
    List<Entrainement> findByDateEntrainementBetweenOrderByDateEntrainementAsc(
            LocalDate dateDebut, LocalDate dateFin);

    // Recherche par type d'entraînement
    List<Entrainement> findByTypeEntrainementOrderByDateEntrainementDesc(
            Entrainement.TypeEntrainement typeEntrainement);

    // Recherche par statut
    List<Entrainement> findByStatutOrderByDateEntrainementDesc(
            Entrainement.StatutEntrainement statut);

    // Entraînements d'un coach pour une période
    @Query("SELECT e FROM Entrainement e WHERE e.coachId = :coachId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateEntrainement ASC, e.heureDebut ASC")
    List<Entrainement> findEntrainementsByCoachAndPeriode(
            @Param("coachId") Long coachId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Entraînements du jour
    @Query("SELECT e FROM Entrainement e WHERE e.dateEntrainement = :date " +
           "ORDER BY e.heureDebut ASC")
    List<Entrainement> findEntrainementsDuJour(@Param("date") LocalDate date);

    // Entraînements de la semaine
    @Query("SELECT e FROM Entrainement e WHERE e.dateEntrainement BETWEEN :lundi AND :dimanche " +
           "ORDER BY e.dateEntrainement ASC, e.heureDebut ASC")
    List<Entrainement> findEntrainementsDeLaSemaine(
            @Param("lundi") LocalDate lundi,
            @Param("dimanche") LocalDate dimanche);

    // Entraînements avec participations d'un joueur
    @Query("SELECT DISTINCT e FROM Entrainement e " +
           "JOIN e.participations p WHERE p.joueurId = :joueurId " +
           "ORDER BY e.dateEntrainement DESC")
    List<Entrainement> findEntrainementsAvecParticipationJoueur(@Param("joueurId") Long joueurId);

    // Entraînements futurs
    @Query("SELECT e FROM Entrainement e WHERE e.dateEntrainement >= :dateActuelle " +
           "AND e.statut = 'PLANIFIE' ORDER BY e.dateEntrainement ASC")
    List<Entrainement> findEntrainementsFuturs(@Param("dateActuelle") LocalDate dateActuelle);

    // Statistiques par type d'entraînement
    @Query("SELECT e.typeEntrainement, COUNT(e) FROM Entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY e.typeEntrainement")
    List<Object[]> getStatistiquesParType(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Nombre d'entraînements par coach
    @Query("SELECT e.coachId, COUNT(e) FROM Entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY e.coachId")
    List<Object[]> getNombreEntrainementsParCoach(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Recherche par mots-clés dans titre ou description
    @Query("SELECT e FROM Entrainement e WHERE " +
           "LOWER(e.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "ORDER BY e.dateEntrainement DESC")
    List<Entrainement> rechercherParMotsCles(@Param("motCle") String motCle);

    // Entraînements avec places disponibles
    @Query("SELECT e FROM Entrainement e WHERE " +
           "e.nombreMaxJoueurs IS NOT NULL AND " +
           "(SELECT COUNT(p) FROM Participation p WHERE p.entrainement = e AND p.statutParticipation = 'INSCRIT') < e.nombreMaxJoueurs " +
           "AND e.dateEntrainement >= :dateActuelle " +
           "ORDER BY e.dateEntrainement ASC")
    List<Entrainement> findEntrainementsAvecPlacesDisponibles(@Param("dateActuelle") LocalDate dateActuelle);
}
