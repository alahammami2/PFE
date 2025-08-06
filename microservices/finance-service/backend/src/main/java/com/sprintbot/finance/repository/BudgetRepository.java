package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Budget
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Trouve tous les budgets actifs
     */
    List<Budget> findByStatutOrderByDateDebutDesc(Budget.StatutBudget statut);

    /**
     * Trouve les budgets par période
     */
    @Query("SELECT b FROM Budget b WHERE b.dateDebut <= :dateFin AND b.dateFin >= :dateDebut ORDER BY b.dateDebut DESC")
    List<Budget> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les budgets actifs pour une période donnée
     */
    @Query("SELECT b FROM Budget b WHERE b.statut = 'ACTIF' AND b.dateDebut <= :dateFin AND b.dateFin >= :dateDebut ORDER BY b.dateDebut DESC")
    List<Budget> findBudgetsActifsPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les budgets par type de période
     */
    List<Budget> findByPeriodeBudgetAndStatutOrderByDateDebutDesc(Budget.PeriodeBudget periodeBudget, Budget.StatutBudget statut);

    /**
     * Trouve les budgets avec alerte de dépassement
     */
    @Query("SELECT b FROM Budget b WHERE b.statut = 'ACTIF' AND (b.montantUtilise * 100.0 / b.montantTotal) >= b.seuilAlerte")
    List<Budget> findBudgetsAvecAlerte();

    /**
     * Trouve les budgets proches de l'expiration
     */
    @Query("SELECT b FROM Budget b WHERE b.statut = 'ACTIF' AND b.dateFin BETWEEN :dateDebut AND :dateFin")
    List<Budget> findBudgetsProchesExpiration(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les budgets avec auto-renouvellement activé
     */
    List<Budget> findByAutoRenouvellementTrueAndStatut(Budget.StatutBudget statut);

    /**
     * Calcule le montant total des budgets actifs
     */
    @Query("SELECT COALESCE(SUM(b.montantTotal), 0) FROM Budget b WHERE b.statut = 'ACTIF'")
    BigDecimal calculerMontantTotalBudgetsActifs();

    /**
     * Calcule le montant utilisé total des budgets actifs
     */
    @Query("SELECT COALESCE(SUM(b.montantUtilise), 0) FROM Budget b WHERE b.statut = 'ACTIF'")
    BigDecimal calculerMontantUtiliseTotalBudgetsActifs();

    /**
     * Trouve les budgets par nom (recherche partielle)
     */
    @Query("SELECT b FROM Budget b WHERE LOWER(b.nom) LIKE LOWER(CONCAT('%', :nom, '%')) ORDER BY b.dateCreation DESC")
    Page<Budget> findByNomContainingIgnoreCase(@Param("nom") String nom, Pageable pageable);

    /**
     * Trouve le budget actif pour une période spécifique
     */
    @Query("SELECT b FROM Budget b WHERE b.statut = 'ACTIF' AND :date BETWEEN b.dateDebut AND b.dateFin")
    Optional<Budget> findBudgetActifPourDate(@Param("date") LocalDate date);

    /**
     * Statistiques des budgets par statut
     */
    @Query("SELECT b.statut, COUNT(b), COALESCE(SUM(b.montantTotal), 0), COALESCE(SUM(b.montantUtilise), 0) " +
           "FROM Budget b GROUP BY b.statut")
    List<Object[]> getStatistiquesParStatut();

    /**
     * Trouve les budgets nécessitant une attention (seuil dépassé ou expiration proche)
     */
    @Query("SELECT b FROM Budget b WHERE b.statut = 'ACTIF' AND " +
           "((b.montantUtilise * 100.0 / b.montantTotal) >= b.seuilAlerte OR " +
           "b.dateFin BETWEEN CURRENT_DATE AND :dateLimite)")
    List<Budget> findBudgetsNecessitantAttention(@Param("dateLimite") LocalDate dateLimite);

    /**
     * Trouve les budgets par montant minimum
     */
    List<Budget> findByMontantTotalGreaterThanEqualOrderByMontantTotalDesc(BigDecimal montantMinimum);

    /**
     * Vérifie l'existence d'un budget actif pour une période
     */
    @Query("SELECT COUNT(b) > 0 FROM Budget b WHERE b.statut = 'ACTIF' AND " +
           "((b.dateDebut BETWEEN :dateDebut AND :dateFin) OR " +
           "(b.dateFin BETWEEN :dateDebut AND :dateFin) OR " +
           "(b.dateDebut <= :dateDebut AND b.dateFin >= :dateFin))")
    boolean existeBudgetActifPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les budgets avec le pourcentage d'utilisation le plus élevé
     */
    @Query("SELECT b FROM Budget b WHERE b.statut = 'ACTIF' AND b.montantTotal > 0 " +
           "ORDER BY (b.montantUtilise * 100.0 / b.montantTotal) DESC")
    List<Budget> findBudgetsParPourcentageUtilisation(Pageable pageable);

    /**
     * Recherche avancée de budgets
     */
    @Query("SELECT b FROM Budget b WHERE " +
           "(:nom IS NULL OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:statut IS NULL OR b.statut = :statut) AND " +
           "(:periodeBudget IS NULL OR b.periodeBudget = :periodeBudget) AND " +
           "(:dateDebut IS NULL OR b.dateDebut >= :dateDebut) AND " +
           "(:dateFin IS NULL OR b.dateFin <= :dateFin) AND " +
           "(:montantMin IS NULL OR b.montantTotal >= :montantMin) AND " +
           "(:montantMax IS NULL OR b.montantTotal <= :montantMax) " +
           "ORDER BY b.dateCreation DESC")
    Page<Budget> rechercheAvancee(
            @Param("nom") String nom,
            @Param("statut") Budget.StatutBudget statut,
            @Param("periodeBudget") Budget.PeriodeBudget periodeBudget,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable);
}
