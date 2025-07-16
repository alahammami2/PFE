package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Joueur;
import com.volleyball.sprintbot.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    // Recherche par équipe
    List<Joueur> findByEquipe(Equipe equipe);

    // Recherche par poste
    List<Joueur> findByPoste(String poste);

    // Recherche par statut
    List<Joueur> findByStatut(String statut);

    // Recherche par numéro de maillot
    Optional<Joueur> findByNumeroMaillot(String numeroMaillot);

    // Recherche des joueurs actifs
    List<Joueur> findByActifTrue();

    // Recherche des joueurs par équipe et actifs
    List<Joueur> findByEquipeAndActifTrue(Equipe equipe);

    // Recherche par poste et équipe
    List<Joueur> findByPosteAndEquipe(String poste, Equipe equipe);

    // Recherche par tranche d'âge (date de naissance)
    List<Joueur> findByDateNaissanceBetween(LocalDate dateDebut, LocalDate dateFin);

    // Recherche par taille minimum
    List<Joueur> findByTailleGreaterThanEqual(Float tailleMin);

    // Recherche par poids dans une fourchette
    List<Joueur> findByPoidsBetween(Float poidsMin, Float poidsMax);

    // Vérifier si un numéro de maillot est déjà utilisé dans une équipe
    boolean existsByNumeroMaillotAndEquipe(String numeroMaillot, Equipe equipe);

    // Compter les joueurs par équipe
    long countByEquipe(Equipe equipe);

    // Compter les joueurs actifs par équipe
    long countByEquipeAndActifTrue(Equipe equipe);

    // Requête personnalisée pour obtenir les joueurs avec leurs performances
    @Query("SELECT j FROM Joueur j LEFT JOIN FETCH j.performances WHERE j.equipe = :equipe")
    List<Joueur> findJoueursAvecPerformances(@Param("equipe") Equipe equipe);

    // Requête personnalisée pour obtenir les joueurs sans absence récente
    @Query("SELECT j FROM Joueur j WHERE j NOT IN " +
           "(SELECT a.joueur FROM Absence a WHERE a.dateDebut <= CURRENT_DATE AND " +
           "(a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE))")
    List<Joueur> findJoueursSansAbsenceEnCours();

    // Requête personnalisée pour obtenir les joueurs par âge
    @Query("SELECT j FROM Joueur j WHERE YEAR(CURRENT_DATE) - YEAR(j.dateNaissance) BETWEEN :ageMin AND :ageMax")
    List<Joueur> findJoueursByAge(@Param("ageMin") int ageMin, @Param("ageMax") int ageMax);

    // Requête personnalisée pour obtenir les meilleurs joueurs (note moyenne > seuil)
    @Query("SELECT j FROM Joueur j WHERE " +
           "(SELECT AVG(p.noteInteger) FROM Performance p WHERE p.joueur = j) > :seuilNote")
    List<Joueur> findMeilleursJoueurs(@Param("seuilNote") double seuilNote);
}
