package com.sprintbot.planningperformance.repository;

import com.sprintbot.planningperformance.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // Recherche par joueur
    List<Absence> findByJoueurIdOrderByIdDesc(Long joueurId);

    // Recherche par entraînement
    List<Absence> findByEntrainementIdOrderByDateDeclaration(Long entrainementId);

    // Recherche par joueur et entraînement
    Optional<Absence> findByEntrainementIdAndJoueurId(Long entrainementId, Long joueurId);

    // Recherche par motif
    List<Absence> findByMotifOrderByIdDesc(Absence.MotifAbsence motif);

    // Absences justifiées
    List<Absence> findByJustifieeOrderByIdDesc(Boolean justifiee);

    // Absences d'un joueur pour une période
    @Query("SELECT a FROM Absence a JOIN a.entrainement e " +
           "WHERE a.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateEntrainement DESC")
    List<Absence> findAbsencesJoueurPeriode(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Nombre d'absences par joueur
    @Query("SELECT a.joueurId, COUNT(a) FROM Absence a JOIN a.entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY a.joueurId " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getNombreAbsencesParJoueur(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Absences par motif pour une période
    @Query("SELECT a.motif, COUNT(a) FROM Absence a JOIN a.entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY a.motif " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getAbsencesParMotif(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Taux d'absences justifiées d'un joueur
    @Query("SELECT " +
           "COUNT(CASE WHEN a.justifiee = true THEN 1 END) * 100.0 / COUNT(a) " +
           "FROM Absence a JOIN a.entrainement e " +
           "WHERE a.joueurId = :joueurId " +
           "AND e.dateEntrainement BETWEEN :dateDebut AND :dateFin")
    Double getTauxAbsencesJustifiees(
            @Param("joueurId") Long joueurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // Joueurs avec le plus d'absences
    @Query("SELECT a.joueurId, COUNT(a) as nombreAbsences, " +
           "COUNT(CASE WHEN a.justifiee = true THEN 1 END) as absencesJustifiees " +
           "FROM Absence a JOIN a.entrainement e " +
           "WHERE e.dateEntrainement BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY a.joueurId " +
           "ORDER BY nombreAbsences DESC " +
           "LIMIT :limite")
    List<Object[]> getJoueursAvecPlusAbsences(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("limite") int limite);

    // Absences récentes d'un joueur
    @Query("SELECT a FROM Absence a JOIN a.entrainement e " +
           "WHERE a.joueurId = :joueurId " +
           "ORDER BY e.dateEntrainement DESC, a.dateDeclaration DESC " +
           "LIMIT :limite")
    List<Absence> getAbsencesRecentes(
            @Param("joueurId") Long joueurId,
            @Param("limite") int limite);

    // Absences non justifiées
    @Query("SELECT a FROM Absence a JOIN a.entrainement e " +
           "WHERE a.justifiee = false " +
           "ORDER BY e.dateEntrainement DESC")
    List<Absence> getAbsencesNonJustifiees();

    // Statistiques d'absences par mois
    @Query("SELECT EXTRACT(MONTH FROM e.dateEntrainement) as mois, " +
           "EXTRACT(YEAR FROM e.dateEntrainement) as annee, " +
           "COUNT(a) as totalAbsences, " +
           "COUNT(CASE WHEN a.justifiee = true THEN 1 END) as absencesJustifiees " +
           "FROM Absence a JOIN a.entrainement e " +
           "WHERE a.joueurId = :joueurId " +
           "GROUP BY EXTRACT(YEAR FROM e.dateEntrainement), EXTRACT(MONTH FROM e.dateEntrainement) " +
           "ORDER BY annee DESC, mois DESC")
    List<Object[]> getStatistiquesAbsencesParMois(@Param("joueurId") Long joueurId);

    // Vérifier si un joueur a une absence pour un entraînement
    @Query("SELECT COUNT(a) > 0 FROM Absence a " +
           "WHERE a.entrainement.id = :entrainementId AND a.joueurId = :joueurId")
    boolean hasAbsence(@Param("entrainementId") Long entrainementId, @Param("joueurId") Long joueurId);

    // Absences par type d'entraînement
    @Query("SELECT e.typeEntrainement, COUNT(a) FROM Absence a JOIN a.entrainement e " +
           "WHERE a.joueurId = :joueurId " +
           "GROUP BY e.typeEntrainement " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getAbsencesParTypeEntrainement(@Param("joueurId") Long joueurId);
}
