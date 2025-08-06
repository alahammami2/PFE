package com.sprintbot.finance.service;

import com.sprintbot.finance.entity.Sponsor;
import com.sprintbot.finance.entity.ContratSponsoring;
import com.sprintbot.finance.entity.PaiementSponsor;
import com.sprintbot.finance.repository.SponsorRepository;
import com.sprintbot.finance.repository.ContratSponsoringRepository;
import com.sprintbot.finance.repository.PaiementSponsorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des sponsors
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SponsorService {

    private final SponsorRepository sponsorRepository;
    private final ContratSponsoringRepository contratRepository;
    private final PaiementSponsorRepository paiementRepository;

    /**
     * Crée un nouveau sponsor
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public Sponsor creerSponsor(Sponsor sponsor) {
        log.info("Création d'un nouveau sponsor: {}", sponsor.getNom());
        
        // Vérification de l'unicité du nom
        if (sponsorRepository.existsByNomIgnoreCase(sponsor.getNom())) {
            throw new IllegalArgumentException("Un sponsor avec ce nom existe déjà");
        }
        
        // Validation des dates
        if (sponsor.getDateFin().isBefore(sponsor.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
        
        // Initialisation des montants
        sponsor.setMontantVerse(BigDecimal.ZERO);
        sponsor.setMontantRestant(sponsor.getMontantContrat());
        sponsor.setStatut(Sponsor.StatutSponsor.ACTIF);
        
        Sponsor sponsorSauvegarde = sponsorRepository.save(sponsor);
        log.info("Sponsor créé avec succès: ID {}", sponsorSauvegarde.getId());
        
        return sponsorSauvegarde;
    }

    /**
     * Met à jour un sponsor existant
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public Sponsor mettreAJourSponsor(Long id, Sponsor sponsorMiseAJour) {
        log.info("Mise à jour du sponsor ID: {}", id);
        
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor non trouvé: " + id));
        
        // Mise à jour des champs modifiables
        sponsor.setNom(sponsorMiseAJour.getNom());
        sponsor.setSecteurActivite(sponsorMiseAJour.getSecteurActivite());
        sponsor.setAdresse(sponsorMiseAJour.getAdresse());
        sponsor.setVille(sponsorMiseAJour.getVille());
        sponsor.setCodePostal(sponsorMiseAJour.getCodePostal());
        sponsor.setPays(sponsorMiseAJour.getPays());
        sponsor.setTelephone(sponsorMiseAJour.getTelephone());
        sponsor.setEmail(sponsorMiseAJour.getEmail());
        sponsor.setSiteWeb(sponsorMiseAJour.getSiteWeb());
        sponsor.setTypePartenariat(sponsorMiseAJour.getTypePartenariat());
        sponsor.setMontantContrat(sponsorMiseAJour.getMontantContrat());
        sponsor.setAutoRenouvellement(sponsorMiseAJour.getAutoRenouvellement());
        
        // Recalcul du montant restant
        sponsor.setMontantRestant(sponsor.getMontantContrat().subtract(sponsor.getMontantVerse()));
        
        return sponsorRepository.save(sponsor);
    }

    /**
     * Enregistre un paiement de sponsor
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public Sponsor enregistrerPaiement(Long sponsorId, BigDecimal montant, String modePaiement, String reference) {
        log.info("Enregistrement d'un paiement de {} pour le sponsor ID: {}", montant, sponsorId);
        
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor non trouvé: " + sponsorId));
        
        sponsor.enregistrerPaiement(montant);
        
        // Création du paiement
        PaiementSponsor paiement = new PaiementSponsor();
        paiement.setSponsor(sponsor);
        paiement.setMontant(montant);
        paiement.setModePaiement(modePaiement);
        paiement.setReference(reference != null ? reference : genererReferencePaiement());
        paiement.setDatePrevue(LocalDate.now());
        paiement.setDateEffective(LocalDate.now());
        paiement.setStatut(PaiementSponsor.StatutPaiement.EFFECTUE);
        
        paiementRepository.save(paiement);
        
        Sponsor sponsorMisAJour = sponsorRepository.save(sponsor);
        log.info("Paiement enregistré avec succès pour le sponsor: {}", sponsor.getNom());
        
        return sponsorMisAJour;
    }

    /**
     * Renouvelle un contrat de sponsor
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public Sponsor renouvellerContrat(Long sponsorId, LocalDate nouvelleDate, BigDecimal nouveauMontant) {
        log.info("Renouvellement du contrat pour le sponsor ID: {}", sponsorId);
        
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor non trouvé: " + sponsorId));
        
        sponsor.renouvellerContrat(nouvelleDate, nouveauMontant);
        
        Sponsor sponsorRenouvele = sponsorRepository.save(sponsor);
        log.info("Contrat renouvelé pour le sponsor: {}", sponsor.getNom());
        
        return sponsorRenouvele;
    }

    /**
     * Suspend un sponsor
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public Sponsor suspendreSponsor(Long sponsorId, String motif) {
        log.info("Suspension du sponsor ID: {}", sponsorId);
        
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor non trouvé: " + sponsorId));
        
        sponsor.setStatut(Sponsor.StatutSponsor.SUSPENDU);
        
        return sponsorRepository.save(sponsor);
    }

    /**
     * Réactive un sponsor
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public Sponsor reactiverSponsor(Long sponsorId) {
        log.info("Réactivation du sponsor ID: {}", sponsorId);
        
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor non trouvé: " + sponsorId));
        
        if (sponsor.getDateFin().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Impossible de réactiver un sponsor expiré");
        }
        
        sponsor.setStatut(Sponsor.StatutSponsor.ACTIF);
        
        return sponsorRepository.save(sponsor);
    }

    /**
     * Obtient tous les sponsors actifs
     */
    @Cacheable(value = "sponsors-actifs")
    public List<Sponsor> obtenirSponsorsActifs() {
        return sponsorRepository.findSponsorsActifs();
    }

    /**
     * Obtient les sponsors proches de l'expiration
     */
    public List<Sponsor> obtenirSponsorsProchesExpiration(int joursAvance) {
        LocalDate dateLimite = LocalDate.now().plusDays(joursAvance);
        return sponsorRepository.findSponsorsProchesExpiration(dateLimite);
    }

    /**
     * Obtient les sponsors nécessitant un renouvellement
     */
    public List<Sponsor> obtenirSponsorsNecessitantRenouvellement(int joursAvance) {
        LocalDate dateLimite = LocalDate.now().plusDays(joursAvance);
        return sponsorRepository.findSponsorsNecessitantRenouvellement(dateLimite);
    }

    /**
     * Obtient les sponsors avec paiements en retard
     */
    public List<Sponsor> obtenirSponsorsAvecPaiementsEnRetard() {
        return sponsorRepository.findSponsorsAvecPaiementsEnRetard();
    }

    /**
     * Calcule le montant total des contrats actifs
     */
    public BigDecimal calculerMontantTotalContratsActifs() {
        return sponsorRepository.calculerMontantTotalContratsActifs();
    }

    /**
     * Calcule le montant total versé
     */
    public BigDecimal calculerMontantTotalVerse() {
        return sponsorRepository.calculerMontantTotalVerse();
    }

    /**
     * Calcule le montant total restant
     */
    public BigDecimal calculerMontantTotalRestant() {
        return sponsorRepository.calculerMontantTotalRestant();
    }

    /**
     * Obtient les statistiques des sponsors
     */
    public List<Object[]> obtenirStatistiquesSponsors() {
        return sponsorRepository.getStatistiquesParTypePartenariat();
    }

    /**
     * Recherche avancée de sponsors
     */
    public Page<Sponsor> rechercherSponsors(String nom, Sponsor.TypePartenariat typePartenariat,
                                           Sponsor.StatutSponsor statut, String secteurActivite, String ville,
                                           LocalDate dateDebutMin, LocalDate dateFinMax, BigDecimal montantMin,
                                           BigDecimal montantMax, Pageable pageable) {
        return sponsorRepository.rechercheAvancee(nom, typePartenariat, statut, secteurActivite, ville,
                                                 dateDebutMin, dateFinMax, montantMin, montantMax, pageable);
    }

    /**
     * Recherche textuelle dans les sponsors
     */
    public Page<Sponsor> rechercheTextuelle(String texte, Pageable pageable) {
        return sponsorRepository.rechercheTextuelle(texte, pageable);
    }

    /**
     * Trouve un sponsor par ID
     */
    @Cacheable(value = "sponsors", key = "#id")
    public Optional<Sponsor> obtenirSponsorParId(Long id) {
        return sponsorRepository.findById(id);
    }

    /**
     * Traite les renouvellements automatiques
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public void traiterRenouvellements() {
        log.info("Traitement des renouvellements automatiques de sponsors");
        
        List<Sponsor> sponsorsARenouveler = sponsorRepository.findSponsorsNecessitantRenouvellement(LocalDate.now().plusDays(30));
        
        for (Sponsor sponsor : sponsorsARenouveler) {
            if (sponsor.getAutoRenouvellement()) {
                try {
                    LocalDate nouvelleDate = sponsor.getDateFin().plusYears(1);
                    renouvellerContrat(sponsor.getId(), nouvelleDate, sponsor.getMontantContrat());
                    log.info("Sponsor renouvelé automatiquement: {}", sponsor.getNom());
                } catch (Exception e) {
                    log.error("Erreur lors du renouvellement du sponsor {}: {}", sponsor.getNom(), e.getMessage());
                }
            }
        }
    }

    /**
     * Supprime un sponsor
     */
    @CacheEvict(value = {"sponsors", "sponsors-actifs"}, allEntries = true)
    public void supprimerSponsor(Long id) {
        log.info("Suppression du sponsor ID: {}", id);
        
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor non trouvé: " + id));
        
        // Vérification qu'aucun paiement n'est lié
        if (sponsor.getMontantVerse().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Impossible de supprimer un sponsor avec des paiements");
        }
        
        sponsorRepository.delete(sponsor);
    }

    /**
     * Génère une référence unique pour le paiement
     */
    private String genererReferencePaiement() {
        String prefix = "PAY";
        String date = LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + date + "-" + uuid;
    }
}
