package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.CategorieBudget;
import com.sprintbot.finance.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository pour l'entité CategorieBudget
 */
@Repository
public interface CategorieBudgetRepository extends JpaRepository<CategorieBudget, Long> {

    /**
     * Trouve les catégories par budget
     */
    List<CategorieBudget> findByBudgetOrderByPrioriteDescNomAsc(Budget budget);

    /**
     * Trouve les catégories actives par budget
     */
    List<CategorieBudget> findByBudgetAndActiveTrueOrderByPrioriteDescNomAsc(Budget budget);

    /**
     * Trouve les catégories par type
     */
    List<CategorieBudget> findByTypeCategorieOrderByNomAsc(CategorieBudget.TypeCategorie typeCategorie);

    /**
     * Trouve les catégories avec alerte de dépassement
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.active = true AND " +
           "(cb.montantUtilise * 100.0 / cb.montantAlloue) >= cb.seuilAlerte")
    List<CategorieBudget> findCategoriesAvecAlerte();

    /**
     * Trouve les catégories par budget avec alerte
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.budget = :budget AND cb.active = true AND " +
           "(cb.montantUtilise * 100.0 / cb.montantAlloue) >= cb.seuilAlerte")
    List<CategorieBudget> findCategoriesAvecAlerteParBudget(@Param("budget") Budget budget);

    /**
     * Calcule le montant total alloué pour un budget
     */
    @Query("SELECT COALESCE(SUM(cb.montantAlloue), 0) FROM CategorieBudget cb WHERE cb.budget = :budget AND cb.active = true")
    BigDecimal calculerMontantTotalAlloue(@Param("budget") Budget budget);

    /**
     * Calcule le montant total utilisé pour un budget
     */
    @Query("SELECT COALESCE(SUM(cb.montantUtilise), 0) FROM CategorieBudget cb WHERE cb.budget = :budget AND cb.active = true")
    BigDecimal calculerMontantTotalUtilise(@Param("budget") Budget budget);

    /**
     * Trouve les catégories par priorité
     */
    List<CategorieBudget> findByBudgetAndActiveTrueOrderByPrioriteDesc(Budget budget);

    /**
     * Trouve les catégories avec montant disponible
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.budget = :budget AND cb.active = true AND " +
           "cb.montantRestant > 0 ORDER BY cb.priorite DESC")
    List<CategorieBudget> findCategoriesAvecMontantDisponible(@Param("budget") Budget budget);

    /**
     * Statistiques par type de catégorie
     */
    @Query("SELECT cb.typeCategorie, COUNT(cb), COALESCE(SUM(cb.montantAlloue), 0), COALESCE(SUM(cb.montantUtilise), 0) " +
           "FROM CategorieBudget cb WHERE cb.active = true GROUP BY cb.typeCategorie")
    List<Object[]> getStatistiquesParType();

    /**
     * Trouve les catégories par nom (recherche partielle)
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.budget = :budget AND " +
           "LOWER(cb.nom) LIKE LOWER(CONCAT('%', :nom, '%')) ORDER BY cb.priorite DESC")
    List<CategorieBudget> findByBudgetAndNomContainingIgnoreCase(@Param("budget") Budget budget, @Param("nom") String nom);

    /**
     * Vérifie l'existence d'une catégorie par nom dans un budget
     */
    boolean existsByBudgetAndNomIgnoreCase(Budget budget, String nom);

    /**
     * Trouve les catégories dépassant le seuil d'alerte
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.active = true AND cb.montantAlloue > 0 AND " +
           "(cb.montantUtilise * 100.0 / cb.montantAlloue) > :pourcentage")
    List<CategorieBudget> findCategoriesDepassantSeuil(@Param("pourcentage") BigDecimal pourcentage);

    /**
     * Trouve les catégories les plus utilisées
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.active = true AND cb.montantAlloue > 0 " +
           "ORDER BY (cb.montantUtilise * 100.0 / cb.montantAlloue) DESC")
    List<CategorieBudget> findCategoriesParPourcentageUtilisation(Pageable pageable);

    /**
     * Recherche avancée de catégories
     */
    @Query("SELECT cb FROM CategorieBudget cb WHERE " +
           "(:budget IS NULL OR cb.budget = :budget) AND " +
           "(:nom IS NULL OR LOWER(cb.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:typeCategorie IS NULL OR cb.typeCategorie = :typeCategorie) AND " +
           "(:active IS NULL OR cb.active = :active) AND " +
           "(:montantMin IS NULL OR cb.montantAlloue >= :montantMin) AND " +
           "(:montantMax IS NULL OR cb.montantAlloue <= :montantMax) " +
           "ORDER BY cb.priorite DESC, cb.nom ASC")
    Page<CategorieBudget> rechercheAvancee(
            @Param("budget") Budget budget,
            @Param("nom") String nom,
            @Param("typeCategorie") CategorieBudget.TypeCategorie typeCategorie,
            @Param("active") Boolean active,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable);

    /**
     * Trouve les catégories par couleur (pour l'affichage graphique)
     */
    List<CategorieBudget> findByBudgetAndCouleurAndActiveTrueOrderByPrioriteDesc(Budget budget, String couleur);

    /**
     * Compte le nombre de catégories actives par budget
     */
    long countByBudgetAndActiveTrue(Budget budget);

    /**
     * Trouve la catégorie avec la priorité la plus élevée pour un budget
     */
    CategorieBudget findFirstByBudgetAndActiveTrueOrderByPrioriteDesc(Budget budget);
}
