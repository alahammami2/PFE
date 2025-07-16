package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Budget;
import com.volleyball.sprintbot.entity.Equipe;
import com.volleyball.sprintbot.entity.ResponsableFinancier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Recherche par équipe
    List<Budget> findByEquipe(Equipe equipe);

    // Recherche par responsable financier
    List<Budget> findByResponsableFinancier(ResponsableFinancier responsableFinancier);

    // Recherche par montant supérieur ou égal
    List<Budget> findByMontantGreaterThanEqual(Double montantMin);

    // Recherche par montant inférieur ou égal
    List<Budget> findByMontantLessThanEqual(Double montantMax);

    // Recherche par montant dans une fourchette
    List<Budget> findByMontantBetween(Double montantMin, Double montantMax);

    // Compter les budgets par équipe
    long countByEquipe(Equipe equipe);

    // Compter les budgets par responsable financier
    long countByResponsableFinancier(ResponsableFinancier responsableFinancier);

    // Requête personnalisée pour obtenir le montant total des budgets
    @Query("SELECT SUM(b.montant) FROM Budget b")
    Double findMontantTotalBudgets();

    // Requête personnalisée pour obtenir le montant total des budgets par équipe
    @Query("SELECT SUM(b.montant) FROM Budget b WHERE b.equipe = :equipe")
    Double findMontantTotalBudgetsByEquipe(@Param("equipe") Equipe equipe);

    // Requête personnalisée pour obtenir les budgets avec leurs dépenses
    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.depenses")
    List<Budget> findBudgetsAvecDepenses();

    // Requête personnalisée pour obtenir les budgets dépassés
    @Query("SELECT b FROM Budget b WHERE " +
           "(SELECT COALESCE(SUM(d.montant), 0) FROM Depense d WHERE d.budget = b) > b.montant")
    List<Budget> findBudgetsDepasses();

    // Requête personnalisée pour obtenir les budgets avec utilisation
    @Query("SELECT b, " +
           "(SELECT COALESCE(SUM(d.montant), 0) FROM Depense d WHERE d.budget = b) as totalDepenses " +
           "FROM Budget b")
    List<Object[]> findBudgetsAvecUtilisation();

    // Requête personnalisée pour obtenir les statistiques budgétaires par équipe
    @Query("SELECT b.equipe, SUM(b.montant) as budgetTotal, " +
           "(SELECT COALESCE(SUM(d.montant), 0) FROM Depense d WHERE d.budget IN " +
           "(SELECT b2 FROM Budget b2 WHERE b2.equipe = b.equipe)) as depensesTotal " +
           "FROM Budget b GROUP BY b.equipe")
    List<Object[]> findStatistiquesBudgetairesParEquipe();
}
