package com.sprintbot.finance.service;

import com.sprintbot.finance.entity.Salaire;
import com.sprintbot.finance.entity.ElementSalaire;
import com.sprintbot.finance.repository.SalaireRepository;
import com.sprintbot.finance.repository.ElementSalaireRepository;
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

/**
 * Service pour la gestion des salaires
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalaireService {

    private final SalaireRepository salaireRepository;
    private final ElementSalaireRepository elementSalaireRepository;

    /**
     * Calcule le salaire pour un employé
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public Salaire calculerSalaire(Long employeId, LocalDate periode, BigDecimal salaireBrut,
                                  BigDecimal primes, BigDecimal bonus, BigDecimal deductions,
                                  Integer heuresSupplementaires, Integer joursAbsence) {
        log.info("Calcul du salaire pour l'employé {} pour la période {}", employeId, periode);
        
        // Vérification qu'un salaire n'existe pas déjà pour cette période
        if (salaireRepository.existsByEmployeIdAndPeriode(employeId, periode)) {
            throw new IllegalArgumentException("Un salaire existe déjà pour cet employé et cette période");
        }
        
        Salaire salaire = new Salaire();
        salaire.setEmployeId(employeId);
        salaire.setPeriode(periode);
        salaire.setSalaireBrut(salaireBrut);
        salaire.setPrimes(primes != null ? primes : BigDecimal.ZERO);
        salaire.setBonus(bonus != null ? bonus : BigDecimal.ZERO);
        salaire.setDeductions(deductions != null ? deductions : BigDecimal.ZERO);
        salaire.setHeuresSupplementaires(heuresSupplementaires != null ? heuresSupplementaires : 0);
        salaire.setJoursAbsence(joursAbsence != null ? joursAbsence : 0);
        
        // Calcul automatique des cotisations et impôts
        salaire.setCotisationsSociales(calculerCotisationsSociales(salaireBrut, primes, bonus));
        salaire.setImpot(calculerImpot(salaireBrut, primes, bonus));
        
        // Calcul du salaire net
        salaire.calculerSalaireNet();
        
        salaire.setStatut(Salaire.StatutSalaire.CALCULE);
        
        Salaire salaireSauvegarde = salaireRepository.save(salaire);
        log.info("Salaire calculé avec succès: ID {}", salaireSauvegarde.getId());
        
        return salaireSauvegarde;
    }

    /**
     * Valide un salaire calculé
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public Salaire validerSalaire(Long salaireId, Long validateurId) {
        log.info("Validation du salaire ID: {} par utilisateur: {}", salaireId, validateurId);
        
        Salaire salaire = salaireRepository.findById(salaireId)
                .orElseThrow(() -> new RuntimeException("Salaire non trouvé: " + salaireId));
        
        salaire.valider(validateurId);
        
        Salaire salaireValide = salaireRepository.save(salaire);
        log.info("Salaire validé avec succès: employé {}, période {}", salaire.getEmployeId(), salaire.getPeriode());
        
        return salaireValide;
    }

    /**
     * Marque un salaire comme payé
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public Salaire marquerSalaireCommePayé(Long salaireId, String modePaiement, String referencePaiement) {
        log.info("Marquage du salaire ID: {} comme payé", salaireId);
        
        Salaire salaire = salaireRepository.findById(salaireId)
                .orElseThrow(() -> new RuntimeException("Salaire non trouvé: " + salaireId));
        
        salaire.marquerCommePayé(modePaiement, referencePaiement);
        
        Salaire salairePaye = salaireRepository.save(salaire);
        log.info("Salaire marqué comme payé: employé {}, période {}", salaire.getEmployeId(), salaire.getPeriode());
        
        return salairePaye;
    }

    /**
     * Annule un salaire
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public Salaire annulerSalaire(Long salaireId, String motifAnnulation) {
        log.info("Annulation du salaire ID: {}", salaireId);
        
        Salaire salaire = salaireRepository.findById(salaireId)
                .orElseThrow(() -> new RuntimeException("Salaire non trouvé: " + salaireId));
        
        if (salaire.getStatut() == Salaire.StatutSalaire.PAYE) {
            throw new IllegalStateException("Impossible d'annuler un salaire déjà payé");
        }
        
        salaire.setStatut(Salaire.StatutSalaire.ANNULE);
        salaire.setCommentaires(motifAnnulation);
        
        return salaireRepository.save(salaire);
    }

    /**
     * Ajoute un élément de salaire
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public ElementSalaire ajouterElementSalaire(Long salaireId, String libelle, ElementSalaire.TypeElement typeElement,
                                               BigDecimal montant, BigDecimal quantite, BigDecimal taux,
                                               boolean obligatoire, boolean imposable, boolean cotisable) {
        log.info("Ajout d'un élément de salaire: {} pour le salaire ID: {}", libelle, salaireId);
        
        Salaire salaire = salaireRepository.findById(salaireId)
                .orElseThrow(() -> new RuntimeException("Salaire non trouvé: " + salaireId));
        
        if (salaire.getStatut() == Salaire.StatutSalaire.PAYE) {
            throw new IllegalStateException("Impossible de modifier un salaire déjà payé");
        }
        
        ElementSalaire element = new ElementSalaire();
        element.setSalaire(salaire);
        element.setLibelle(libelle);
        element.setTypeElement(typeElement);
        element.setMontant(montant);
        element.setQuantite(quantite);
        element.setTaux(taux);
        element.setObligatoire(obligatoire);
        element.setImposable(imposable);
        element.setCotisable(cotisable);
        
        // Définition de l'ordre d'affichage
        ElementSalaire dernierElement = elementSalaireRepository.findFirstBySalaireOrderByOrdreAffichageDesc(salaire);
        element.setOrdreAffichage(dernierElement != null ? dernierElement.getOrdreAffichage() + 1 : 1);
        
        ElementSalaire elementSauvegarde = elementSalaireRepository.save(element);
        
        // Recalcul du salaire net
        salaire.calculerSalaireNet();
        salaireRepository.save(salaire);
        
        return elementSauvegarde;
    }

    /**
     * Obtient les salaires en attente de validation
     */
    public List<Salaire> obtenirSalairesEnAttenteValidation() {
        return salaireRepository.findSalairesEnAttenteValidation();
    }

    /**
     * Obtient les salaires validés mais non payés
     */
    public List<Salaire> obtenirSalairesValides() {
        return salaireRepository.findSalairesValides();
    }

    /**
     * Obtient les salaires d'un employé
     */
    @Cacheable(value = "salaires", key = "#employeId")
    public List<Salaire> obtenirSalairesEmploye(Long employeId) {
        return salaireRepository.findByEmployeIdOrderByPeriodeDesc(employeId);
    }

    /**
     * Obtient le salaire d'un employé pour une période
     */
    public Optional<Salaire> obtenirSalaireEmployePeriode(Long employeId, LocalDate periode) {
        return salaireRepository.findByEmployeIdAndPeriode(employeId, periode);
    }

    /**
     * Calcule le montant total des salaires pour une période
     */
    public BigDecimal calculerMontantTotalSalaires(LocalDate dateDebut, LocalDate dateFin) {
        return salaireRepository.calculerMontantTotalSalairesNets(dateDebut, dateFin);
    }

    /**
     * Calcule le montant total des cotisations pour une période
     */
    public BigDecimal calculerMontantTotalCotisations(LocalDate dateDebut, LocalDate dateFin) {
        return salaireRepository.calculerMontantTotalCotisations(dateDebut, dateFin);
    }

    /**
     * Obtient les statistiques des salaires
     */
    public List<Object[]> obtenirStatistiquesSalaires() {
        return salaireRepository.getStatistiquesParStatut();
    }

    /**
     * Obtient les statistiques mensuelles
     */
    public List<Object[]> obtenirStatistiquesMensuelles() {
        return salaireRepository.getStatistiquesMensuelles();
    }

    /**
     * Recherche avancée de salaires
     */
    public Page<Salaire> rechercherSalaires(Long employeId, Salaire.StatutSalaire statut,
                                           LocalDate periodeDebut, LocalDate periodeFin,
                                           BigDecimal salaireMin, BigDecimal salaireMax,
                                           Pageable pageable) {
        return salaireRepository.rechercheAvancee(employeId, statut, periodeDebut, periodeFin,
                                                 salaireMin, salaireMax, pageable);
    }

    /**
     * Trouve un salaire par ID
     */
    @Cacheable(value = "salaires", key = "#id")
    public Optional<Salaire> obtenirSalaireParId(Long id) {
        return salaireRepository.findById(id);
    }

    /**
     * Obtient les éléments d'un salaire
     */
    public List<ElementSalaire> obtenirElementsSalaire(Long salaireId) {
        Salaire salaire = salaireRepository.findById(salaireId)
                .orElseThrow(() -> new RuntimeException("Salaire non trouvé: " + salaireId));
        
        return elementSalaireRepository.findBySalaireOrderByOrdreAffichageAscLibelleAsc(salaire);
    }

    /**
     * Calcule les cotisations sociales
     */
    private BigDecimal calculerCotisationsSociales(BigDecimal salaireBrut, BigDecimal primes, BigDecimal bonus) {
        BigDecimal baseCalcul = salaireBrut.add(primes).add(bonus);
        // Taux de cotisations sociales (exemple: 23%)
        return baseCalcul.multiply(new BigDecimal("0.23"));
    }

    /**
     * Calcule l'impôt sur le revenu
     */
    private BigDecimal calculerImpot(BigDecimal salaireBrut, BigDecimal primes, BigDecimal bonus) {
        BigDecimal baseCalcul = salaireBrut.add(primes).add(bonus);
        // Calcul simplifié de l'impôt (exemple: 15%)
        return baseCalcul.multiply(new BigDecimal("0.15"));
    }

    /**
     * Traite la paie mensuelle pour tous les employés
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public void traiterPaieMensuelle(LocalDate periode) {
        log.info("Traitement de la paie mensuelle pour la période: {}", periode);
        
        List<Salaire> salairesValides = salaireRepository.findSalairesValides();
        
        for (Salaire salaire : salairesValides) {
            if (salaire.getPeriode().equals(periode)) {
                try {
                    marquerSalaireCommePayé(salaire.getId(), "VIREMENT", "AUTO-" + periode);
                    log.info("Salaire payé automatiquement: employé {}", salaire.getEmployeId());
                } catch (Exception e) {
                    log.error("Erreur lors du paiement du salaire pour l'employé {}: {}", 
                             salaire.getEmployeId(), e.getMessage());
                }
            }
        }
    }

    /**
     * Supprime un salaire
     */
    @CacheEvict(value = {"salaires", "salaires-valides"}, allEntries = true)
    public void supprimerSalaire(Long id) {
        log.info("Suppression du salaire ID: {}", id);
        
        Salaire salaire = salaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salaire non trouvé: " + id));
        
        if (salaire.getStatut() == Salaire.StatutSalaire.PAYE) {
            throw new IllegalStateException("Impossible de supprimer un salaire déjà payé");
        }
        
        salaireRepository.delete(salaire);
    }
}
