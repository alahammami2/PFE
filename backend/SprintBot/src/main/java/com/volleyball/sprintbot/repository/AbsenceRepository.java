package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Absence;
import com.volleyball.sprintbot.entity.Joueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // Recherche par joueur
    List<Absence> findByJoueur(Joueur joueur);

    // Recherche par statut
    List<Absence> findByStatut(String statut);

    // Recherche par date de début
    List<Absence> findByDateDebut(LocalDate dateDebut);

    // Recherche par période
    List<Absence> findByDateDebutBetween(LocalDate dateDebut, LocalDate dateFin);

    // Recherche des absences en cours
    @Query("SELECT a FROM Absence a WHERE a.dateDebut <= CURRENT_DATE AND " +
           "(a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE)")
    List<Absence> findAbsencesEnCours();

    // Recherche des absences en cours pour un joueur
    @Query("SELECT a FROM Absence a WHERE a.joueur = :joueur AND a.dateDebut <= CURRENT_DATE AND " +
           "(a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE)")
    List<Absence> findAbsencesEnCoursPourJoueur(@Param("joueur") Joueur joueur);

    // Recherche des absences futures
    @Query("SELECT a FROM Absence a WHERE a.dateDebut > CURRENT_DATE")
    List<Absence> findAbsencesFutures();

    // Recherche des absences passées
    @Query("SELECT a FROM Absence a WHERE a.dateFin < CURRENT_DATE")
    List<Absence> findAbsencesPassees();

    // Recherche par joueur et statut
    List<Absence> findByJoueurAndStatut(Joueur joueur, String statut);

    // Compter les absences par joueur
    long countByJoueur(Joueur joueur);

    // Compter les absences par statut
    long countByStatut(String statut);

    // Requête personnalisée pour obtenir les absences longues (plus de X jours)
    // TODO: Implémenter avec une requête native ou une méthode de service
    // @Query("SELECT a FROM Absence a WHERE DATEDIFF(COALESCE(a.dateFin, CURRENT_DATE), a.dateDebut) >= :nombreJours")
    // List<Absence> findAbsencesLongues(@Param("nombreJours") int nombreJours);

    // Requête personnalisée pour obtenir les joueurs avec le plus d'absences
    @Query("SELECT a.joueur, COUNT(a) as nombreAbsences FROM Absence a " +
           "GROUP BY a.joueur ORDER BY nombreAbsences DESC")
    List<Object[]> findJoueursAvecPlusAbsences();
}
