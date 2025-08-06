package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.PaiementSponsor;
import com.sprintbot.finance.entity.Sponsor;
import com.sprintbot.finance.entity.ContratSponsoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour l'entité PaiementSponsor
 */
@Repository
public interface PaiementSponsorRepository extends JpaRepository<PaiementSponsor, Long> {

    /**
     * Trouve les paiements par sponsor
     */
    List<PaiementSponsor> findBySponsorOrderByDatePrevueDesc(Sponsor sponsor);

    /**
     * Trouve les paiements par contrat
     */
    List<PaiementSponsor> findByContratOrderByDatePrevueAsc(ContratSponsoring contrat);

    /**
     * Trouve les paiements par statut
     */
    List<PaiementSponsor> findByStatutOrderByDatePrevueAsc(PaiementSponsor.StatutPaiement statut);

    /**
     * Trouve les paiements prévus
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'PREVU' ORDER BY p.datePrevue ASC")
    List<PaiementSponsor> findPaiementsPrevus();

    /**
     * Trouve les paiements effectués
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'EFFECTUE' ORDER BY p.dateEffective DESC")
    List<PaiementSponsor> findPaiementsEffectues();

    /**
     * Trouve les paiements en retard
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'EN_RETARD' OR (p.statut = 'PREVU' AND p.datePrevue < CURRENT_DATE) ORDER BY p.datePrevue ASC")
    List<PaiementSponsor> findPaiementsEnRetard();

    /**
     * Trouve les paiements dus aujourd'hui
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'PREVU' AND p.datePrevue = CURRENT_DATE")
    List<PaiementSponsor> findPaiementsDusAujourdhui();

    /**
     * Trouve les paiements dus dans les prochains jours
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'PREVU' AND p.datePrevue BETWEEN CURRENT_DATE AND :dateLimite ORDER BY p.datePrevue ASC")
    List<PaiementSponsor> findPaiementsDusProchainement(@Param("dateLimite") LocalDate dateLimite);

    /**
     * Trouve les paiements par période prévue
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.datePrevue BETWEEN :dateDebut AND :dateFin ORDER BY p.datePrevue ASC")
    List<PaiementSponsor> findPaiementsParPeriodePrevue(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les paiements par période effective
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.dateEffective BETWEEN :dateDebut AND :dateFin ORDER BY p.dateEffective DESC")
    List<PaiementSponsor> findPaiementsParPeriodeEffective(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Calcule le montant total des paiements effectués
     */
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementSponsor p WHERE p.statut = 'EFFECTUE'")
    BigDecimal calculerMontantTotalPaiementsEffectues();

    /**
     * Calcule le montant total des paiements prévus
     */
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementSponsor p WHERE p.statut = 'PREVU'")
    BigDecimal calculerMontantTotalPaiementsPrevus();

    /**
     * Calcule le montant total des paiements en retard
     */
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementSponsor p WHERE p.statut = 'EN_RETARD' OR (p.statut = 'PREVU' AND p.datePrevue < CURRENT_DATE)")
    BigDecimal calculerMontantTotalPaiementsEnRetard();

    /**
     * Statistiques par statut
     */
    @Query("SELECT p.statut, COUNT(p), COALESCE(SUM(p.montant), 0) FROM PaiementSponsor p GROUP BY p.statut")
    List<Object[]> getStatistiquesParStatut();

    /**
     * Statistiques mensuelles des paiements effectués
     */
    @Query("SELECT YEAR(p.dateEffective), MONTH(p.dateEffective), COUNT(p), COALESCE(SUM(p.montant), 0) " +
           "FROM PaiementSponsor p WHERE p.statut = 'EFFECTUE' " +
           "GROUP BY YEAR(p.dateEffective), MONTH(p.dateEffective) " +
           "ORDER BY YEAR(p.dateEffective) DESC, MONTH(p.dateEffective) DESC")
    List<Object[]> getStatistiquesMensuellesPaiements();

    /**
     * Trouve les paiements par mode de paiement
     */
    List<PaiementSponsor> findByModePaiementAndStatutOrderByDateEffectiveDesc(String modePaiement, PaiementSponsor.StatutPaiement statut);

    /**
     * Trouve les plus gros paiements
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'EFFECTUE' ORDER BY p.montant DESC")
    List<PaiementSponsor> findPlusGrosPaiements(Pageable pageable);

    /**
     * Recherche textuelle dans les paiements
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE " +
           "(LOWER(p.reference) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(p.notes) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY p.dateCreation DESC")
    Page<PaiementSponsor> rechercheTextuelle(@Param("texte") String texte, Pageable pageable);

    /**
     * Recherche avancée de paiements
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE " +
           "(:sponsor IS NULL OR p.sponsor = :sponsor) AND " +
           "(:contrat IS NULL OR p.contrat = :contrat) AND " +
           "(:reference IS NULL OR LOWER(p.reference) LIKE LOWER(CONCAT('%', :reference, '%'))) AND " +
           "(:statut IS NULL OR p.statut = :statut) AND " +
           "(:modePaiement IS NULL OR LOWER(p.modePaiement) LIKE LOWER(CONCAT('%', :modePaiement, '%'))) AND " +
           "(:datePrevueDebut IS NULL OR p.datePrevue >= :datePrevueDebut) AND " +
           "(:datePrevueFin IS NULL OR p.datePrevue <= :datePrevueFin) AND " +
           "(:dateEffectiveDebut IS NULL OR p.dateEffective >= :dateEffectiveDebut) AND " +
           "(:dateEffectiveFin IS NULL OR p.dateEffective <= :dateEffectiveFin) AND " +
           "(:montantMin IS NULL OR p.montant >= :montantMin) AND " +
           "(:montantMax IS NULL OR p.montant <= :montantMax) " +
           "ORDER BY p.datePrevue DESC")
    Page<PaiementSponsor> rechercheAvancee(
            @Param("sponsor") Sponsor sponsor,
            @Param("contrat") ContratSponsoring contrat,
            @Param("reference") String reference,
            @Param("statut") PaiementSponsor.StatutPaiement statut,
            @Param("modePaiement") String modePaiement,
            @Param("datePrevueDebut") LocalDate datePrevueDebut,
            @Param("datePrevueFin") LocalDate datePrevueFin,
            @Param("dateEffectiveDebut") LocalDate dateEffectiveDebut,
            @Param("dateEffectiveFin") LocalDate dateEffectiveFin,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable);

    /**
     * Vérifie l'existence d'un paiement par référence
     */
    boolean existsByReference(String reference);

    /**
     * Compte les paiements par statut
     */
    long countByStatut(PaiementSponsor.StatutPaiement statut);

    /**
     * Trouve les paiements du mois en cours
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE YEAR(p.datePrevue) = YEAR(CURRENT_DATE) AND MONTH(p.datePrevue) = MONTH(CURRENT_DATE) ORDER BY p.datePrevue ASC")
    List<PaiementSponsor> findPaiementsMoisEnCours();

    /**
     * Trouve les paiements effectués du mois en cours
     */
    @Query("SELECT p FROM PaiementSponsor p WHERE p.statut = 'EFFECTUE' AND YEAR(p.dateEffective) = YEAR(CURRENT_DATE) AND MONTH(p.dateEffective) = MONTH(CURRENT_DATE) ORDER BY p.dateEffective DESC")
    List<PaiementSponsor> findPaiementsEffectuesMoisEnCours();
}
