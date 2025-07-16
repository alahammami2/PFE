package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.DonneesSante;
import com.volleyball.sprintbot.entity.Joueur;
import com.volleyball.sprintbot.entity.StaffMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonneesSanteRepository extends JpaRepository<DonneesSante, Long> {

    // Recherche par joueur
    List<DonneesSante> findByJoueur(Joueur joueur);

    // Recherche par staff médical
    List<DonneesSante> findByStaffMedical(StaffMedical staffMedical);

    // Recherche par date
    List<DonneesSante> findByDate(LocalDate date);

    // Recherche par période
    List<DonneesSante> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);

    // Recherche par cote
    List<DonneesSante> findByCote(String cote);

    // Recherche des données avec blessures
    @Query("SELECT d FROM DonneesSante d WHERE d.blessures IS NOT NULL AND d.blessures != ''")
    List<DonneesSante> findDonneesAvecBlessures();

    // Recherche des données récentes (derniers 30 jours)
    @Query("SELECT d FROM DonneesSante d WHERE d.date >= :dateDebut ORDER BY d.date DESC")
    List<DonneesSante> findDonneesRecentes(@Param("dateDebut") LocalDate dateDebut);

    // Recherche par joueur et période
    List<DonneesSante> findByJoueurAndDateBetween(Joueur joueur, LocalDate dateDebut, LocalDate dateFin);

    // Recherche par staff médical et période
    List<DonneesSante> findByStaffMedicalAndDateBetween(StaffMedical staffMedical, LocalDate dateDebut, LocalDate dateFin);

    // Compter les données par joueur
    long countByJoueur(Joueur joueur);

    // Compter les données par staff médical
    long countByStaffMedical(StaffMedical staffMedical);

    // Requête personnalisée pour obtenir les dernières données par joueur
    @Query("SELECT d FROM DonneesSante d WHERE d.joueur = :joueur ORDER BY d.date DESC")
    List<DonneesSante> findDernieresDonneesByJoueur(@Param("joueur") Joueur joueur);

    // Requête personnalisée pour obtenir les joueurs avec blessures récentes
    @Query("SELECT DISTINCT d.joueur FROM DonneesSante d WHERE d.blessures IS NOT NULL AND d.blessures != '' AND d.date >= :dateDebut")
    List<Joueur> findJoueursAvecBlessuresRecentes(@Param("dateDebut") LocalDate dateDebut);

    // Requête personnalisée pour obtenir les statistiques de santé par équipe
    @Query("SELECT j.equipe, COUNT(d), " +
           "SUM(CASE WHEN d.blessures IS NOT NULL AND d.blessures != '' THEN 1 ELSE 0 END) as nombreBlessures " +
           "FROM DonneesSante d JOIN d.joueur j " +
           "WHERE d.date >= :dateDebut " +
           "GROUP BY j.equipe")
    List<Object[]> findStatistiquesSanteParEquipe(@Param("dateDebut") LocalDate dateDebut);

    // Requête personnalisée pour obtenir l'évolution de la santé d'un joueur
    @Query("SELECT d FROM DonneesSante d WHERE d.joueur = :joueur AND d.date >= :dateDebut ORDER BY d.date ASC")
    List<DonneesSante> findEvolutionSanteJoueur(@Param("joueur") Joueur joueur, @Param("dateDebut") LocalDate dateDebut);
}
