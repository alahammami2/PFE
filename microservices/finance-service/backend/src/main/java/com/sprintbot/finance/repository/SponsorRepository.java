package com.sprintbot.finance.repository;

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

/**
 * Repository pour l'entité Sponsor
 */
@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {

    /**
     * Trouve les sponsors par statut
     */
    List<Sponsor> findByStatutOrderByNomAsc(Sponsor.StatutSponsor statut);

    /**
     * Trouve les sponsors actifs
     */
    @Query("SELECT s FROM Sponsor s WHERE s.statut = 'ACTIF' AND s.dateFin >= CURRENT_DATE ORDER BY s.nom ASC")
    List<Sponsor> findSponsorsActifs();

    /**
     * Trouve les sponsors par type de partenariat
     */
    List<Sponsor> findByTypePartenariatAndStatutOrderByMontantContratDesc(Sponsor.TypePartenariat typePartenariat, Sponsor.StatutSponsor statut);

    /**
     * Trouve les sponsors expirés
     */
    @Query("SELECT s FROM Sponsor s WHERE s.dateFin < CURRENT_DATE ORDER BY s.dateFin DESC")
    List<Sponsor> findSponsorsExpires();

    /**
     * Trouve les sponsors proches de l'expiration
     */
    @Query("SELECT s FROM Sponsor s WHERE s.statut = 'ACTIF' AND s.dateFin BETWEEN CURRENT_DATE AND :dateLimite ORDER BY s.dateFin ASC")
    List<Sponsor> findSponsorsProchesExpiration(@Param("dateLimite") LocalDate dateLimite);

    /**
     * Trouve les sponsors avec auto-renouvellement
     */
    List<Sponsor> findByAutoRenouvellementTrueAndStatutOrderByDateFinAsc(Sponsor.StatutSponsor statut);

    /**
     * Trouve les sponsors nécessitant un renouvellement
     */
    @Query("SELECT s FROM Sponsor s WHERE s.statut = 'ACTIF' AND s.autoRenouvellement = true AND " +
           "s.dateFin BETWEEN CURRENT_DATE AND :dateLimite ORDER BY s.dateFin ASC")
    List<Sponsor> findSponsorsNecessitantRenouvellement(@Param("dateLimite") LocalDate dateLimite);

    /**
     * Calcule le montant total des contrats actifs
     */
    @Query("SELECT COALESCE(SUM(s.montantContrat), 0) FROM Sponsor s WHERE s.statut = 'ACTIF'")
    BigDecimal calculerMontantTotalContratsActifs();

    /**
     * Calcule le montant total versé par les sponsors actifs
     */
    @Query("SELECT COALESCE(SUM(s.montantVerse), 0) FROM Sponsor s WHERE s.statut = 'ACTIF'")
    BigDecimal calculerMontantTotalVerse();

    /**
     * Calcule le montant total restant des sponsors actifs
     */
    @Query("SELECT COALESCE(SUM(s.montantRestant), 0) FROM Sponsor s WHERE s.statut = 'ACTIF'")
    BigDecimal calculerMontantTotalRestant();

    /**
     * Trouve les sponsors par secteur d'activité
     */
    List<Sponsor> findBySecteurActiviteContainingIgnoreCaseOrderByNomAsc(String secteurActivite);

    /**
     * Trouve les sponsors par ville
     */
    List<Sponsor> findByVilleIgnoreCaseOrderByNomAsc(String ville);

    /**
     * Recherche textuelle dans les sponsors
     */
    @Query("SELECT s FROM Sponsor s WHERE " +
           "(LOWER(s.nom) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(s.secteurActivite) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(s.ville) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY s.nom ASC")
    Page<Sponsor> rechercheTextuelle(@Param("texte") String texte, Pageable pageable);

    /**
     * Statistiques par type de partenariat
     */
    @Query("SELECT s.typePartenariat, COUNT(s), COALESCE(SUM(s.montantContrat), 0), COALESCE(SUM(s.montantVerse), 0) " +
           "FROM Sponsor s WHERE s.statut = 'ACTIF' GROUP BY s.typePartenariat ORDER BY SUM(s.montantContrat) DESC")
    List<Object[]> getStatistiquesParTypePartenariat();

    /**
     * Statistiques par statut
     */
    @Query("SELECT s.statut, COUNT(s), COALESCE(SUM(s.montantContrat), 0) " +
           "FROM Sponsor s GROUP BY s.statut")
    List<Object[]> getStatistiquesParStatut();

    /**
     * Trouve les plus gros sponsors
     */
    List<Sponsor> findByStatutOrderByMontantContratDesc(Sponsor.StatutSponsor statut, Pageable pageable);

    /**
     * Trouve les sponsors par montant minimum
     */
    List<Sponsor> findByMontantContratGreaterThanEqualAndStatutOrderByMontantContratDesc(BigDecimal montantMinimum, Sponsor.StatutSponsor statut);

    /**
     * Trouve les sponsors avec paiements en retard
     */
    @Query("SELECT DISTINCT s FROM Sponsor s JOIN s.contrats c JOIN c.paiements p " +
           "WHERE s.statut = 'ACTIF' AND p.statut = 'EN_RETARD'")
    List<Sponsor> findSponsorsAvecPaiementsEnRetard();

    /**
     * Recherche avancée de sponsors
     */
    @Query("SELECT s FROM Sponsor s WHERE " +
           "(:nom IS NULL OR LOWER(s.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:typePartenariat IS NULL OR s.typePartenariat = :typePartenariat) AND " +
           "(:statut IS NULL OR s.statut = :statut) AND " +
           "(:secteurActivite IS NULL OR LOWER(s.secteurActivite) LIKE LOWER(CONCAT('%', :secteurActivite, '%'))) AND " +
           "(:ville IS NULL OR LOWER(s.ville) LIKE LOWER(CONCAT('%', :ville, '%'))) AND " +
           "(:dateDebutMin IS NULL OR s.dateDebut >= :dateDebutMin) AND " +
           "(:dateFinMax IS NULL OR s.dateFin <= :dateFinMax) AND " +
           "(:montantMin IS NULL OR s.montantContrat >= :montantMin) AND " +
           "(:montantMax IS NULL OR s.montantContrat <= :montantMax) " +
           "ORDER BY s.nom ASC")
    Page<Sponsor> rechercheAvancee(
            @Param("nom") String nom,
            @Param("typePartenariat") Sponsor.TypePartenariat typePartenariat,
            @Param("statut") Sponsor.StatutSponsor statut,
            @Param("secteurActivite") String secteurActivite,
            @Param("ville") String ville,
            @Param("dateDebutMin") LocalDate dateDebutMin,
            @Param("dateFinMax") LocalDate dateFinMax,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable);

    /**
     * Vérifie l'existence d'un sponsor par nom
     */
    boolean existsByNomIgnoreCase(String nom);

    /**
     * Trouve les sponsors par période de contrat
     */
    @Query("SELECT s FROM Sponsor s WHERE s.dateDebut <= :dateFin AND s.dateFin >= :dateDebut ORDER BY s.dateDebut ASC")
    List<Sponsor> findSponsorsPourPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    /**
     * Compte les sponsors actifs
     */
    long countByStatut(Sponsor.StatutSponsor statut);

    /**
     * Trouve les sponsors avec contrats terminés
     */
    @Query("SELECT s FROM Sponsor s WHERE s.montantVerse >= s.montantContrat ORDER BY s.nom ASC")
    List<Sponsor> findSponsorsContratsTermines();
}
