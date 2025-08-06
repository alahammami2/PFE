package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.Transaction;
import com.sprintbot.finance.entity.Budget;
import com.sprintbot.finance.entity.CategorieBudget;
import com.sprintbot.finance.entity.CategorieTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Transaction
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Trouve les transactions par statut
     */
    List<Transaction> findByStatutOrderByDateTransactionDesc(Transaction.StatutTransaction statut);

    /**
     * Trouve les transactions par type
     */
    List<Transaction> findByTypeTransactionOrderByDateTransactionDesc(Transaction.TypeTransaction typeTransaction);

    /**
     * Trouve les transactions par période
     */
    @Query("SELECT t FROM Transaction t WHERE t.dateTransaction BETWEEN :dateDebut AND :dateFin ORDER BY t.dateTransaction DESC")
    List<Transaction> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les transactions par utilisateur
     */
    List<Transaction> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);

    /**
     * Trouve les transactions par budget
     */
    List<Transaction> findByBudgetOrderByDateTransactionDesc(Budget budget);

    /**
     * Trouve les transactions par catégorie de budget
     */
    List<Transaction> findByCategorieBudgetOrderByDateTransactionDesc(CategorieBudget categorieBudget);

    /**
     * Trouve les transactions par catégorie de transaction
     */
    List<Transaction> findByCategorieTransactionOrderByDateTransactionDesc(CategorieTransaction categorieTransaction);

    /**
     * Trouve les transactions en attente de validation
     */
    @Query("SELECT t FROM Transaction t WHERE t.statut = 'EN_ATTENTE' ORDER BY t.dateCreation ASC")
    List<Transaction> findTransactionsEnAttenteValidation();

    /**
     * Trouve les transactions validées par période
     */
    @Query("SELECT t FROM Transaction t WHERE t.statut = 'VALIDEE' AND t.dateComptabilisation BETWEEN :dateDebut AND :dateFin ORDER BY t.dateComptabilisation DESC")
    List<Transaction> findTransactionsValideesPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le total des recettes pour une période
     */
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.typeTransaction = 'RECETTE' AND t.statut = 'VALIDEE' AND t.dateComptabilisation BETWEEN :dateDebut AND :dateFin")
    BigDecimal calculerTotalRecettesPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le total des dépenses pour une période
     */
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.typeTransaction = 'DEPENSE' AND t.statut = 'VALIDEE' AND t.dateComptabilisation BETWEEN :dateDebut AND :dateFin")
    BigDecimal calculerTotalDepensesPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le solde pour une période
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN t.typeTransaction = 'RECETTE' THEN t.montant ELSE -t.montant END), 0) " +
           "FROM Transaction t WHERE t.statut = 'VALIDEE' AND t.dateComptabilisation BETWEEN :dateDebut AND :dateFin")
    BigDecimal calculerSoldePourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les transactions par référence
     */
    List<Transaction> findByReferenceContainingIgnoreCaseOrderByDateCreationDesc(String reference);

    /**
     * Trouve les transactions récurrentes
     */
    List<Transaction> findByRecurrenteTrueOrderByDateTransactionDesc();

    /**
     * Trouve les transactions avec TVA
     */
    List<Transaction> findByTvaApplicableTrueOrderByDateTransactionDesc();

    /**
     * Statistiques par catégorie de transaction
     */
    @Query("SELECT ct.nom, t.typeTransaction, COUNT(t), COALESCE(SUM(t.montant), 0) " +
           "FROM Transaction t JOIN t.categorieTransaction ct WHERE t.statut = 'VALIDEE' " +
           "GROUP BY ct.nom, t.typeTransaction ORDER BY SUM(t.montant) DESC")
    List<Object[]> getStatistiquesParCategorie();

    /**
     * Statistiques mensuelles
     */
    @Query("SELECT YEAR(t.dateComptabilisation), MONTH(t.dateComptabilisation), t.typeTransaction, " +
           "COUNT(t), COALESCE(SUM(t.montant), 0) " +
           "FROM Transaction t WHERE t.statut = 'VALIDEE' " +
           "GROUP BY YEAR(t.dateComptabilisation), MONTH(t.dateComptabilisation), t.typeTransaction " +
           "ORDER BY YEAR(t.dateComptabilisation) DESC, MONTH(t.dateComptabilisation) DESC")
    List<Object[]> getStatistiquesMensuelles();

    /**
     * Trouve les plus grosses transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.statut = 'VALIDEE' ORDER BY t.montant DESC")
    List<Transaction> findPlusGrossesTransactions(Pageable pageable);

    /**
     * Trouve les transactions par mode de paiement
     */
    List<Transaction> findByModePaiementAndStatutOrderByDateTransactionDesc(String modePaiement, Transaction.StatutTransaction statut);

    /**
     * Recherche textuelle dans les transactions
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(LOWER(t.description) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(t.reference) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(t.beneficiaire) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY t.dateCreation DESC")
    Page<Transaction> rechercheTextuelle(@Param("texte") String texte, Pageable pageable);

    /**
     * Recherche avancée de transactions
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:reference IS NULL OR LOWER(t.reference) LIKE LOWER(CONCAT('%', :reference, '%'))) AND " +
           "(:typeTransaction IS NULL OR t.typeTransaction = :typeTransaction) AND " +
           "(:statut IS NULL OR t.statut = :statut) AND " +
           "(:categorieTransaction IS NULL OR t.categorieTransaction = :categorieTransaction) AND " +
           "(:budget IS NULL OR t.budget = :budget) AND " +
           "(:utilisateurId IS NULL OR t.utilisateurId = :utilisateurId) AND " +
           "(:dateDebut IS NULL OR t.dateTransaction >= :dateDebut) AND " +
           "(:dateFin IS NULL OR t.dateTransaction <= :dateFin) AND " +
           "(:montantMin IS NULL OR t.montant >= :montantMin) AND " +
           "(:montantMax IS NULL OR t.montant <= :montantMax) " +
           "ORDER BY t.dateTransaction DESC")
    Page<Transaction> rechercheAvancee(
            @Param("reference") String reference,
            @Param("typeTransaction") Transaction.TypeTransaction typeTransaction,
            @Param("statut") Transaction.StatutTransaction statut,
            @Param("categorieTransaction") CategorieTransaction categorieTransaction,
            @Param("budget") Budget budget,
            @Param("utilisateurId") Long utilisateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable);

    /**
     * Trouve les transactions nécessitant une validation
     */
    @Query("SELECT t FROM Transaction t WHERE t.statut = 'EN_ATTENTE' AND " +
           "t.dateCreation < :dateLimite ORDER BY t.dateCreation ASC")
    List<Transaction> findTransactionsEnAttenteDepuis(@Param("dateLimite") LocalDateTime dateLimite);

    /**
     * Vérifie l'existence d'une transaction par référence
     */
    boolean existsByReference(String reference);

    /**
     * Compte les transactions par statut
     */
    long countByStatut(Transaction.StatutTransaction statut);

    /**
     * Trouve les transactions du jour
     */
    @Query("SELECT t FROM Transaction t WHERE DATE(t.dateTransaction) = CURRENT_DATE ORDER BY t.dateCreation DESC")
    List<Transaction> findTransactionsDuJour();

    /**
     * Calcule le montant total des transactions en attente
     */
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.statut = 'EN_ATTENTE'")
    BigDecimal calculerMontantTotalEnAttente();
}
