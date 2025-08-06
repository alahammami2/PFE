package com.sprintbot.planningperformance.repository;

import com.sprintbot.planningperformance.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // Recherche par joueur
    List<Participation> findByJoueurIdOrderByIdDesc(Long joueurId);

    // Recherche par entraînement
    List<Participation> findByEntrainementIdOrderByJoueurId(Long entrainementId);

    // Recherche par joueur et entraînement
    Optional<Participation> findByEntrainementIdAndJoueurId(Long entrainementId, Long joueurId);

    // Recherche par statut de participation
    List<Participation> findByStatutParticipationOrderByIdDesc(
            Participation.StatutParticipation statutParticipation);

    // Participations d'un joueur pour une période
    @Query("SELECT p FROM Participation p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateEntrainement DESC")
    List<Participation> findParticipationsJoueurPeriode(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Participations par statut pour un entraînement
    @Query("SELECT p FROM Participation p WHERE p.entrainement.id = :entrainementId " +
           "AND p.statutParticipation = :statut")
    List<Participation> findByEntrainementAndStatut(
            @Param("entrainementId") Long entrainementId,
            @Param("statut") Participation.StatutParticipation statut);

    // Nombre de participations par statut pour un entraînement
    @Query("SELECT p.statutParticipation, COUNT(p) FROM Participation p " +
           "WHERE p.entrainement.id = :entrainementId " +
           "GROUP BY p.statutParticipation")
    List<Object[]> countParticipationsParStatut(@Param("entrainementId") Long entrainementId);

    // Taux de présence d'un joueur
    @Query("SELECT " +
           "COUNT(CASE WHEN p.statutParticipation = 'PRESENT' THEN 1 END) * 100.0 / COUNT(p) " +
           "FROM Participation p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin")
    Double getTauxPresenceJoueur(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Joueurs les plus assidus
    @Query("SELECT p.joueurId, COUNT(CASE WHEN p.statutParticipation = 'PRESENT' THEN 1 END) as presences " +
           "FROM Participation p JOIN p.entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY p.joueurId " +
           "ORDER BY presences DESC")
    List<Object[]> getJoueursLesPlussidus(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Statistiques de participation par mois
    @Query("SELECT EXTRACT(MONTH FROM e.dateEntrainement) as mois, " +
           "EXTRACT(YEAR FROM e.dateEntrainement) as annee, " +
           "COUNT(p) as totalParticipations, " +
           "COUNT(CASE WHEN p.statutParticipation = 'PRESENT' THEN 1 END) as presences " +
           "FROM Participation p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "GROUP BY EXTRACT(YEAR FROM e.dateEntrainement), EXTRACT(MONTH FROM e.dateEntrainement) " +
           "ORDER BY annee DESC, mois DESC")
    List<Object[]> getStatistiquesParticipationParMois(@Param("joueurId") Long joueurId);

    // Vérifier si un joueur est inscrit à un entraînement
    @Query("SELECT COUNT(p) > 0 FROM Participation p " +
           "WHERE p.entrainement.id = :entrainementId AND p.joueurId = :joueurId")
    boolean isJoueurInscrit(@Param("entrainementId") Long entrainementId, @Param("joueurId") Long joueurId);

    // Nombre de places prises pour un entraînement
    @Query("SELECT COUNT(p) FROM Participation p " +
           "WHERE p.entrainement.id = :entrainementId " +
           "AND p.statutParticipation IN ('INSCRIT', 'PRESENT')")
    Long countPlacesPrises(@Param("entrainementId") Long entrainementId);

    // Participations récentes d'un joueur
    @Query("SELECT p FROM Participation p JOIN p.entrainement e " +
           "WHERE p.joueurId = :joueurId " +
           "ORDER BY e.dateEntrainement DESC, e.heureDebut DESC " +
           "LIMIT :limite")
    List<Participation> findParticipationsRecentes(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);
}
