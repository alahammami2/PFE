package com.sprintbot.finance.repository;

import com.sprintbot.finance.entity.ContratSponsoring;
import com.sprintbot.finance.entity.Sponsor;
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
 * Repository pour l'entité ContratSponsoring
 */
@Repository
public interface ContratSponsoringRepository extends JpaRepository<ContratSponsoring, Long> {

    /**
     * Trouve les contrats par sponsor
     */
    List<ContratSponsoring> findBySponsorOrderByDateSignatureDesc(Sponsor sponsor);

    /**
     * Trouve les contrats par statut
     */
    List<ContratSponsoring> findByStatutOrderByDateSignatureDesc(ContratSponsoring.StatutContrat statut);

    /**
     * Trouve les contrats actifs
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE c.statut = 'ACTIF' AND c.dateFin >= CURRENT_DATE ORDER BY c.dateSignature DESC")
    List<ContratSponsoring> findContratsActifs();

    /**
     * Trouve les contrats expirés
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE c.dateFin < CURRENT_DATE ORDER BY c.dateFin DESC")
    List<ContratSponsoring> findContratsExpires();

    /**
     * Trouve les contrats proches de l'expiration
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE c.statut = 'ACTIF' AND c.dateFin BETWEEN CURRENT_DATE AND :dateLimite ORDER BY c.dateFin ASC")
    List<ContratSponsoring> findContratsProchesExpiration(@Param("dateLimite") LocalDate dateLimite);

    /**
     * Trouve les contrats avec auto-renouvellement
     */
    List<ContratSponsoring> findByAutoRenouvellementTrueAndStatutOrderByDateFinAsc(ContratSponsoring.StatutContrat statut);

    /**
     * Trouve un contrat par numéro
     */
    Optional<ContratSponsoring> findByNumeroContrat(String numeroContrat);

    /**
     * Trouve les contrats par période de signature
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE c.dateSignature BETWEEN :dateDebut AND :dateFin ORDER BY c.dateSignature DESC")
    List<ContratSponsoring> findContratsParPeriodeSignature(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les contrats par modalité de paiement
     */
    List<ContratSponsoring> findByModalitePaiementAndStatutOrderByDateSignatureDesc(ContratSponsoring.ModalitePaiement modalitePaiement, ContratSponsoring.StatutContrat statut);

    /**
     * Calcule le montant total des contrats actifs
     */
    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM ContratSponsoring c WHERE c.statut = 'ACTIF'")
    BigDecimal calculerMontantTotalContratsActifs();

    /**
     * Calcule le montant total versé des contrats
     */
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM ContratSponsoring c JOIN c.paiements p WHERE c.statut = 'ACTIF' AND p.statut = 'EFFECTUE'")
    BigDecimal calculerMontantTotalVerse();

    /**
     * Statistiques par statut
     */
    @Query("SELECT c.statut, COUNT(c), COALESCE(SUM(c.montant), 0) FROM ContratSponsoring c GROUP BY c.statut")
    List<Object[]> getStatistiquesParStatut();

    /**
     * Statistiques par modalité de paiement
     */
    @Query("SELECT c.modalitePaiement, COUNT(c), COALESCE(SUM(c.montant), 0) FROM ContratSponsoring c WHERE c.statut = 'ACTIF' GROUP BY c.modalitePaiement")
    List<Object[]> getStatistiquesParModalitePaiement();

    /**
     * Trouve les plus gros contrats
     */
    List<ContratSponsoring> findByStatutOrderByMontantDesc(ContratSponsoring.StatutContrat statut, Pageable pageable);

    /**
     * Trouve les contrats par montant minimum
     */
    List<ContratSponsoring> findByMontantGreaterThanEqualAndStatutOrderByMontantDesc(BigDecimal montantMinimum, ContratSponsoring.StatutContrat statut);

    /**
     * Recherche textuelle dans les contrats
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE " +
           "(LOWER(c.numeroContrat) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(c.termesConditions) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(c.contreparties) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY c.dateSignature DESC")
    Page<ContratSponsoring> rechercheTextuelle(@Param("texte") String texte, Pageable pageable);

    /**
     * Recherche avancée de contrats
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE " +
           "(:sponsor IS NULL OR c.sponsor = :sponsor) AND " +
           "(:numeroContrat IS NULL OR LOWER(c.numeroContrat) LIKE LOWER(CONCAT('%', :numeroContrat, '%'))) AND " +
           "(:statut IS NULL OR c.statut = :statut) AND " +
           "(:modalitePaiement IS NULL OR c.modalitePaiement = :modalitePaiement) AND " +
           "(:dateSignatureDebut IS NULL OR c.dateSignature >= :dateSignatureDebut) AND " +
           "(:dateSignatureFin IS NULL OR c.dateSignature <= :dateSignatureFin) AND " +
           "(:dateDebutMin IS NULL OR c.dateDebut >= :dateDebutMin) AND " +
           "(:dateFinMax IS NULL OR c.dateFin <= :dateFinMax) AND " +
           "(:montantMin IS NULL OR c.montant >= :montantMin) AND " +
           "(:montantMax IS NULL OR c.montant <= :montantMax) " +
           "ORDER BY c.dateSignature DESC")
    Page<ContratSponsoring> rechercheAvancee(
            @Param("sponsor") Sponsor sponsor,
            @Param("numeroContrat") String numeroContrat,
            @Param("statut") ContratSponsoring.StatutContrat statut,
            @Param("modalitePaiement") ContratSponsoring.ModalitePaiement modalitePaiement,
            @Param("dateSignatureDebut") LocalDate dateSignatureDebut,
            @Param("dateSignatureFin") LocalDate dateSignatureFin,
            @Param("dateDebutMin") LocalDate dateDebutMin,
            @Param("dateFinMax") LocalDate dateFinMax,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable);

    /**
     * Vérifie l'existence d'un contrat par numéro
     */
    boolean existsByNumeroContrat(String numeroContrat);

    /**
     * Trouve les contrats nécessitant un renouvellement
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE c.statut = 'ACTIF' AND c.autoRenouvellement = true AND " +
           "c.dateFin BETWEEN CURRENT_DATE AND :dateLimite ORDER BY c.dateFin ASC")
    List<ContratSponsoring> findContratsNecessitantRenouvellement(@Param("dateLimite") LocalDate dateLimite);

    /**
     * Compte les contrats par statut
     */
    long countByStatut(ContratSponsoring.StatutContrat statut);

    /**
     * Trouve les contrats terminés (montant entièrement versé)
     */
    @Query("SELECT c FROM ContratSponsoring c WHERE " +
           "(SELECT COALESCE(SUM(p.montant), 0) FROM PaiementSponsor p WHERE p.contrat = c AND p.statut = 'EFFECTUE') >= c.montant")
    List<ContratSponsoring> findContratsTermines();

    /**
     * Trouve les contrats avec paiements en retard
     */
    @Query("SELECT DISTINCT c FROM ContratSponsoring c JOIN c.paiements p WHERE c.statut = 'ACTIF' AND p.statut = 'EN_RETARD'")
    List<ContratSponsoring> findContratsAvecPaiementsEnRetard();
}
