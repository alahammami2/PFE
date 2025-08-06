package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.CategorieTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité CategorieTransaction
 */
@Repository
public interface CategorieTransactionRepository extends JpaRepository<CategorieTransaction, Long> {

    /**
     * Trouve les catégories par type de transaction
     */
    List<CategorieTransaction> findByTypeTransactionAndActifTrueOrderByOrdreAffichageAscNomAsc(CategorieTransaction.TypeTransaction typeTransaction);

    /**
     * Trouve toutes les catégories actives
     */
    List<CategorieTransaction> findByActifTrueOrderByOrdreAffichageAscNomAsc();

    /**
     * Trouve les catégories par nom (recherche partielle)
     */
    @Query("SELECT ct FROM CategorieTransaction ct WHERE LOWER(ct.nom) LIKE LOWER(CONCAT('%', :nom, '%')) ORDER BY ct.ordreAffichage ASC")
    List<CategorieTransaction> findByNomContainingIgnoreCase(@Param("nom") String nom);

    /**
     * Trouve une catégorie par nom exact
     */
    Optional<CategorieTransaction> findByNomIgnoreCase(String nom);

    /**
     * Trouve les catégories nécessitant une validation
     */
    List<CategorieTransaction> findByValidationRequiseTrueAndActifTrueOrderByNomAsc();

    /**
     * Trouve les catégories nécessitant une pièce jointe
     */
    List<CategorieTransaction> findByPieceJointeObligatoireTrueAndActifTrueOrderByNomAsc();

    /**
     * Trouve les catégories par couleur
     */
    List<CategorieTransaction> findByCouleurAndActifTrueOrderByNomAsc(String couleur);

    /**
     * Statistiques d'utilisation des catégories
     */
    @Query("SELECT ct.nom, ct.typeTransaction, COUNT(t), COALESCE(SUM(t.montant), 0) " +
           "FROM CategorieTransaction ct LEFT JOIN ct.transactions t " +
           "WHERE ct.actif = true AND (t.statut = 'VALIDEE' OR t.statut IS NULL) " +
           "GROUP BY ct.id, ct.nom, ct.typeTransaction " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getStatistiquesUtilisation();

    /**
     * Trouve les catégories les plus utilisées
     */
    @Query("SELECT ct FROM CategorieTransaction ct LEFT JOIN ct.transactions t " +
           "WHERE ct.actif = true AND t.statut = 'VALIDEE' " +
           "GROUP BY ct.id " +
           "ORDER BY COUNT(t) DESC")
    List<CategorieTransaction> findCategoriesLesPlusUtilisees(Pageable pageable);

    /**
     * Trouve les catégories non utilisées
     */
    @Query("SELECT ct FROM CategorieTransaction ct WHERE ct.actif = true AND " +
           "NOT EXISTS (SELECT 1 FROM Transaction t WHERE t.categorieTransaction = ct)")
    List<CategorieTransaction> findCategoriesNonUtilisees();

    /**
     * Vérifie l'existence d'une catégorie par nom
     */
    boolean existsByNomIgnoreCase(String nom);

    /**
     * Compte les catégories actives par type
     */
    long countByTypeTransactionAndActifTrue(CategorieTransaction.TypeTransaction typeTransaction);

    /**
     * Trouve les catégories par ordre d'affichage
     */
    List<CategorieTransaction> findByActifTrueOrderByOrdreAffichageAsc();

    /**
     * Recherche avancée de catégories
     */
    @Query("SELECT ct FROM CategorieTransaction ct WHERE " +
           "(:nom IS NULL OR LOWER(ct.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:typeTransaction IS NULL OR ct.typeTransaction = :typeTransaction) AND " +
           "(:actif IS NULL OR ct.actif = :actif) AND " +
           "(:validationRequise IS NULL OR ct.validationRequise = :validationRequise) AND " +
           "(:pieceJointeObligatoire IS NULL OR ct.pieceJointeObligatoire = :pieceJointeObligatoire) " +
           "ORDER BY ct.ordreAffichage ASC, ct.nom ASC")
    Page<CategorieTransaction> rechercheAvancee(
            @Param("nom") String nom,
            @Param("typeTransaction") CategorieTransaction.TypeTransaction typeTransaction,
            @Param("actif") Boolean actif,
            @Param("validationRequise") Boolean validationRequise,
            @Param("pieceJointeObligatoire") Boolean pieceJointeObligatoire,
            Pageable pageable);

    /**
     * Trouve la catégorie avec l'ordre d'affichage le plus élevé
     */
    CategorieTransaction findFirstByActifTrueOrderByOrdreAffichageDesc();

    /**
     * Trouve les catégories par icône
     */
    List<CategorieTransaction> findByIconeAndActifTrueOrderByNomAsc(String icone);
}
