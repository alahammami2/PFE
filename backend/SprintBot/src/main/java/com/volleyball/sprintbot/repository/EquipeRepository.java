package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    // Recherche par nom d'équipe
    Optional<Equipe> findByNomEquipe(String nomEquipe);

    // Recherche par entraîneur
    List<Equipe> findByEntraineur(String entraineur);

    // Recherche par nom d'équipe contenant (insensible à la casse)
    List<Equipe> findByNomEquipeContainingIgnoreCase(String nomEquipe);

    // Vérifier si une équipe existe par nom
    boolean existsByNomEquipe(String nomEquipe);

    // Requête personnalisée pour obtenir les équipes avec leurs joueurs actifs
    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.joueurs j WHERE j.actif = true OR j IS NULL")
    List<Equipe> findEquipesAvecJoueursActifs();

    // Requête personnalisée pour obtenir les équipes avec un nombre minimum de joueurs
    @Query("SELECT e FROM Equipe e WHERE SIZE(e.joueurs) >= :nombreMinJoueurs")
    List<Equipe> findEquipesAvecNombreMinJoueurs(@Param("nombreMinJoueurs") int nombreMinJoueurs);

    // Requête personnalisée pour obtenir les équipes avec leurs coaches
    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.coaches")
    List<Equipe> findEquipesAvecCoaches();

    // Requête personnalisée pour obtenir les équipes avec budget
    @Query("SELECT e FROM Equipe e WHERE EXISTS (SELECT b FROM Budget b WHERE b.equipe = e)")
    List<Equipe> findEquipesAvecBudget();
}
