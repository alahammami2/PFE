package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.ElementSalaire;
import com.sprintbot.finance.entity.Salaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository pour l'entité ElementSalaire
 */
@Repository
public interface ElementSalaireRepository extends JpaRepository<ElementSalaire, Long> {

    /**
     * Trouve les éléments par salaire
     */
    List<ElementSalaire> findBySalaireOrderByOrdreAffichageAscLibelleAsc(Salaire salaire);

    /**
     * Trouve les éléments par type
     */
    List<ElementSalaire> findByTypeElementOrderByLibelleAsc(ElementSalaire.TypeElement typeElement);

    /**
     * Trouve les éléments de gain (salaire, primes, bonus, etc.)
     */
    @Query("SELECT e FROM ElementSalaire e WHERE e.typeElement IN ('SALAIRE_BASE', 'PRIME', 'BONUS', 'HEURES_SUPPLEMENTAIRES', 'INDEMNITE') ORDER BY e.libelle ASC")
    List<ElementSalaire> findElementsGain();

    /**
     * Trouve les éléments de déduction
     */
    @Query("SELECT e FROM ElementSalaire e WHERE e.typeElement IN ('COTISATION_SOCIALE', 'IMPOT', 'DEDUCTION', 'AVANCE', 'RETENUE') ORDER BY e.libelle ASC")
    List<ElementSalaire> findElementsDeduction();

    /**
     * Trouve les éléments obligatoires
     */
    List<ElementSalaire> findByObligatoireTrueOrderByOrdreAffichageAsc();

    /**
     * Trouve les éléments imposables
     */
    List<ElementSalaire> findByImposableTrueOrderByLibelleAsc();

    /**
     * Trouve les éléments cotisables
     */
    List<ElementSalaire> findByCotisableTrueOrderByLibelleAsc();

    /**
     * Calcule le total des gains pour un salaire
     */
    @Query("SELECT COALESCE(SUM(e.montant), 0) FROM ElementSalaire e WHERE e.salaire = :salaire AND e.typeElement IN ('SALAIRE_BASE', 'PRIME', 'BONUS', 'HEURES_SUPPLEMENTAIRES', 'INDEMNITE')")
    BigDecimal calculerTotalGains(@Param("salaire") Salaire salaire);

    /**
     * Calcule le total des déductions pour un salaire
     */
    @Query("SELECT COALESCE(SUM(e.montant), 0) FROM ElementSalaire e WHERE e.salaire = :salaire AND e.typeElement IN ('COTISATION_SOCIALE', 'IMPOT', 'DEDUCTION', 'AVANCE', 'RETENUE')")
    BigDecimal calculerTotalDeductions(@Param("salaire") Salaire salaire);

    /**
     * Calcule le total des éléments imposables pour un salaire
     */
    @Query("SELECT COALESCE(SUM(e.montant), 0) FROM ElementSalaire e WHERE e.salaire = :salaire AND e.imposable = true")
    BigDecimal calculerTotalImposable(@Param("salaire") Salaire salaire);

    /**
     * Calcule le total des éléments cotisables pour un salaire
     */
    @Query("SELECT COALESCE(SUM(e.montant), 0) FROM ElementSalaire e WHERE e.salaire = :salaire AND e.cotisable = true")
    BigDecimal calculerTotalCotisable(@Param("salaire") Salaire salaire);

    /**
     * Trouve les éléments par libellé (recherche partielle)
     */
    @Query("SELECT e FROM ElementSalaire e WHERE LOWER(e.libelle) LIKE LOWER(CONCAT('%', :libelle, '%')) ORDER BY e.libelle ASC")
    List<ElementSalaire> findByLibelleContainingIgnoreCase(@Param("libelle") String libelle);

    /**
     * Statistiques par type d'élément
     */
    @Query("SELECT e.typeElement, COUNT(e), COALESCE(SUM(e.montant), 0), COALESCE(AVG(e.montant), 0) " +
           "FROM ElementSalaire e GROUP BY e.typeElement ORDER BY SUM(e.montant) DESC")
    List<Object[]> getStatistiquesParType();

    /**
     * Trouve les éléments les plus utilisés
     */
    @Query("SELECT e.libelle, e.typeElement, COUNT(e), COALESCE(SUM(e.montant), 0) " +
           "FROM ElementSalaire e GROUP BY e.libelle, e.typeElement ORDER BY COUNT(e) DESC")
    List<Object[]> findElementsLesPlusUtilises();

    /**
     * Trouve les éléments par montant minimum
     */
    List<ElementSalaire> findByMontantGreaterThanEqualOrderByMontantDesc(BigDecimal montantMinimum);

    /**
     * Trouve les éléments avec quantité
     */
    @Query("SELECT e FROM ElementSalaire e WHERE e.quantite IS NOT NULL AND e.quantite > 0 ORDER BY e.quantite DESC")
    List<ElementSalaire> findElementsAvecQuantite();

    /**
     * Trouve les éléments avec taux
     */
    @Query("SELECT e FROM ElementSalaire e WHERE e.taux IS NOT NULL AND e.taux > 0 ORDER BY e.taux DESC")
    List<ElementSalaire> findElementsAvecTaux();

    /**
     * Compte les éléments par salaire
     */
    long countBySalaire(Salaire salaire);

    /**
     * Compte les éléments par type
     */
    long countByTypeElement(ElementSalaire.TypeElement typeElement);

    /**
     * Trouve les éléments par ordre d'affichage
     */
    List<ElementSalaire> findBySalaireOrderByOrdreAffichageAsc(Salaire salaire);

    /**
     * Vérifie l'existence d'un élément par libellé et salaire
     */
    boolean existsBySalaireAndLibelleIgnoreCase(Salaire salaire, String libelle);

    /**
     * Trouve l'élément avec l'ordre d'affichage le plus élevé pour un salaire
     */
    ElementSalaire findFirstBySalaireOrderByOrdreAffichageDesc(Salaire salaire);
}
