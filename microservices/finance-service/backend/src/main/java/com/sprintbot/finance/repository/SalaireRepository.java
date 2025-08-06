package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.Salaire;
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
 * Repository pour l'entité Salaire
 */
@Repository
public interface SalaireRepository extends JpaRepository<Salaire, Long> {

    /**
     * Trouve les salaires par employé
     */
    List<Salaire> findByEmployeIdOrderByPeriodeDesc(Long employeId);

    /**
     * Trouve les salaires par statut
     */
    List<Salaire> findByStatutOrderByPeriodeDesc(Salaire.StatutSalaire statut);

    /**
     * Trouve les salaires par période
     */
    List<Salaire> findByPeriodeOrderByEmployeIdAsc(LocalDate periode);

    /**
     * Trouve les salaires pour une période donnée
     */
    @Query("SELECT s FROM Salaire s WHERE s.periode BETWEEN :dateDebut AND :dateFin ORDER BY s.periode DESC, s.employeId ASC")
    List<Salaire> findSalairesPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve le salaire d'un employé pour une période spécifique
     */
    Optional<Salaire> findByEmployeIdAndPeriode(Long employeId, LocalDate periode);

    /**
     * Trouve les salaires en attente de validation
     */
    @Query("SELECT s FROM Salaire s WHERE s.statut = 'CALCULE' ORDER BY s.dateCalcul ASC")
    List<Salaire> findSalairesEnAttenteValidation();

    /**
     * Trouve les salaires validés mais non payés
     */
    @Query("SELECT s FROM Salaire s WHERE s.statut = 'VALIDE' ORDER BY s.dateValidation ASC")
    List<Salaire> findSalairesValides();

    /**
     * Trouve les salaires payés pour une période
     */
    @Query("SELECT s FROM Salaire s WHERE s.statut = 'PAYE' AND s.dateVersement BETWEEN :dateDebut AND :dateFin ORDER BY s.dateVersement DESC")
    List<Salaire> findSalairesPayesPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le montant total des salaires bruts pour une période
     */
    @Query("SELECT COALESCE(SUM(s.salaireBrut), 0) FROM Salaire s WHERE s.periode BETWEEN :dateDebut AND :dateFin AND s.statut != 'ANNULE'")
    BigDecimal calculerMontantTotalSalairesBruts(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le montant total des salaires nets pour une période
     */
    @Query("SELECT COALESCE(SUM(s.salaireNet), 0) FROM Salaire s WHERE s.periode BETWEEN :dateDebut AND :dateFin AND s.statut != 'ANNULE'")
    BigDecimal calculerMontantTotalSalairesNets(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le montant total des cotisations sociales pour une période
     */
    @Query("SELECT COALESCE(SUM(s.cotisationsSociales), 0) FROM Salaire s WHERE s.periode BETWEEN :dateDebut AND :dateFin AND s.statut != 'ANNULE'")
    BigDecimal calculerMontantTotalCotisations(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le montant total des impôts pour une période
     */
    @Query("SELECT COALESCE(SUM(s.impot), 0) FROM Salaire s WHERE s.periode BETWEEN :dateDebut AND :dateFin AND s.statut != 'ANNULE'")
    BigDecimal calculerMontantTotalImpots(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Statistiques des salaires par statut
     */
    @Query("SELECT s.statut, COUNT(s), COALESCE(SUM(s.salaireBrut), 0), COALESCE(SUM(s.salaireNet), 0) " +
           "FROM Salaire s GROUP BY s.statut")
    List<Object[]> getStatistiquesParStatut();

    /**
     * Statistiques mensuelles des salaires
     */
    @Query("SELECT YEAR(s.periode), MONTH(s.periode), COUNT(s), " +
           "COALESCE(SUM(s.salaireBrut), 0), COALESCE(SUM(s.salaireNet), 0) " +
           "FROM Salaire s WHERE s.statut != 'ANNULE' " +
           "GROUP BY YEAR(s.periode), MONTH(s.periode) " +
           "ORDER BY YEAR(s.periode) DESC, MONTH(s.periode) DESC")
    List<Object[]> getStatistiquesMensuelles();

    /**
     * Trouve les salaires avec primes
     */
    @Query("SELECT s FROM Salaire s WHERE s.primes > 0 ORDER BY s.primes DESC")
    List<Salaire> findSalairesAvecPrimes();

    /**
     * Trouve les salaires avec bonus
     */
    @Query("SELECT s FROM Salaire s WHERE s.bonus > 0 ORDER BY s.bonus DESC")
    List<Salaire> findSalairesAvecBonus();

    /**
     * Trouve les salaires avec déductions
     */
    @Query("SELECT s FROM Salaire s WHERE s.deductions > 0 ORDER BY s.deductions DESC")
    List<Salaire> findSalairesAvecDeductions();

    /**
     * Trouve les plus hauts salaires
     */
    @Query("SELECT s FROM Salaire s WHERE s.statut != 'ANNULE' ORDER BY s.salaireBrut DESC")
    List<Salaire> findPlusHautsSalaires(Pageable pageable);

    /**
     * Recherche avancée de salaires
     */
    @Query("SELECT s FROM Salaire s WHERE " +
           "(:employeId IS NULL OR s.employeId = :employeId) AND " +
           "(:statut IS NULL OR s.statut = :statut) AND " +
           "(:periodeDebut IS NULL OR s.periode >= :periodeDebut) AND " +
           "(:periodeFin IS NULL OR s.periode <= :periodeFin) AND " +
           "(:salaireMin IS NULL OR s.salaireBrut >= :salaireMin) AND " +
           "(:salaireMax IS NULL OR s.salaireBrut <= :salaireMax) " +
           "ORDER BY s.periode DESC, s.employeId ASC")
    Page<Salaire> rechercheAvancee(
            @Param("employeId") Long employeId,
            @Param("statut") Salaire.StatutSalaire statut,
            @Param("periodeDebut") LocalDate periodeDebut,
            @Param("periodeFin") LocalDate periodeFin,
            @Param("salaireMin") BigDecimal salaireMin,
            @Param("salaireMax") BigDecimal salaireMax,
            Pageable pageable);

    /**
     * Vérifie l'existence d'un salaire pour un employé et une période
     */
    boolean existsByEmployeIdAndPeriode(Long employeId, LocalDate periode);

    /**
     * Compte les salaires par statut
     */
    long countByStatut(Salaire.StatutSalaire statut);

    /**
     * Trouve les salaires du mois en cours
     */
    @Query("SELECT s FROM Salaire s WHERE YEAR(s.periode) = YEAR(CURRENT_DATE) AND MONTH(s.periode) = MONTH(CURRENT_DATE) ORDER BY s.employeId ASC")
    List<Salaire> findSalairesMoisEnCours();

    /**
     * Trouve les salaires de l'année en cours
     */
    @Query("SELECT s FROM Salaire s WHERE YEAR(s.periode) = YEAR(CURRENT_DATE) ORDER BY s.periode DESC, s.employeId ASC")
    List<Salaire> findSalairesAnneeEnCours();

    /**
     * Calcule la moyenne des salaires bruts pour une période
     */
    @Query("SELECT COALESCE(AVG(s.salaireBrut), 0) FROM Salaire s WHERE s.periode BETWEEN :dateDebut AND :dateFin AND s.statut != 'ANNULE'")
    BigDecimal calculerMoyenneSalairesBruts(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les salaires avec heures supplémentaires
     */
    @Query("SELECT s FROM Salaire s WHERE s.heuresSupplementaires > 0 ORDER BY s.heuresSupplementaires DESC")
    List<Salaire> findSalairesAvecHeuresSupplementaires();

    /**
     * Trouve les salaires avec absences
     */
    @Query("SELECT s FROM Salaire s WHERE s.joursAbsence > 0 ORDER BY s.joursAbsence DESC")
    List<Salaire> findSalairesAvecAbsences();

    /**
     * Trouve les derniers salaires calculés
     */
    @Query("SELECT s FROM Salaire s ORDER BY s.dateCalcul DESC")
    List<Salaire> findDerniersSalairesCalcules(Pageable pageable);
}
