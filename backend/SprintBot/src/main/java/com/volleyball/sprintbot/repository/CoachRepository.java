package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Coach;
import com.volleyball.sprintbot.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {

    // Recherche par équipe
    List<Coach> findByEquipe(Equipe equipe);

    // Recherche par spécialité
    List<Coach> findBySpecialite(String specialite);

    // Recherche des coaches actifs
    List<Coach> findByActifTrue();

    // Recherche par équipe et actifs
    List<Coach> findByEquipeAndActifTrue(Equipe equipe);

    // Recherche par spécialité contenant (insensible à la casse)
    List<Coach> findBySpecialiteContainingIgnoreCase(String specialite);

    // Compter les coaches par équipe
    long countByEquipe(Equipe equipe);

    // Compter les coaches actifs par équipe
    long countByEquipeAndActifTrue(Equipe equipe);

    // Requête personnalisée pour obtenir les coaches avec leurs plannings
    @Query("SELECT c FROM Coach c LEFT JOIN FETCH c.plannings WHERE c.equipe = :equipe")
    List<Coach> findCoachesAvecPlannings(@Param("equipe") Equipe equipe);

    // Requête personnalisée pour obtenir les coaches avec performances gérées
    @Query("SELECT c FROM Coach c LEFT JOIN FETCH c.performancesGerees WHERE c.actif = true")
    List<Coach> findCoachesActifsAvecPerformances();
}
