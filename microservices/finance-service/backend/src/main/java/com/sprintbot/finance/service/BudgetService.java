package com.sprintbot.finance.service;

import com.sprintbot.finance.entity.Budget;
import com.sprintbot.finance.entity.CategorieBudget;
import com.sprintbot.finance.repository.BudgetRepository;
import com.sprintbot.finance.repository.CategorieBudgetRepository;
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
 * Service pour la gestion des budgets
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategorieBudgetRepository categorieBudgetRepository;

    /**
     * Crée un nouveau budget
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public Budget creerBudget(Budget budget) {
        log.info("Création d'un nouveau budget: {}", budget.getNom());
        
        // Validation des dates
        if (budget.getDateFin().isBefore(budget.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
        
        // Vérification de chevauchement avec budgets actifs
        if (budgetRepository.existeBudgetActifPourPeriode(budget.getDateDebut(), budget.getDateFin())) {
            throw new IllegalArgumentException("Un budget actif existe déjà pour cette période");
        }
        
        // Initialisation des montants
        budget.setMontantUtilise(BigDecimal.ZERO);
        budget.setMontantRestant(budget.getMontantTotal());
        budget.setStatut(Budget.StatutBudget.ACTIF);
        
        Budget budgetSauvegarde = budgetRepository.save(budget);
        log.info("Budget créé avec succès: ID {}", budgetSauvegarde.getId());
        
        return budgetSauvegarde;
    }

    /**
     * Met à jour un budget existant
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public Budget mettreAJourBudget(Long id, Budget budgetMiseAJour) {
        log.info("Mise à jour du budget ID: {}", id);
        
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé: " + id));
        
        // Mise à jour des champs modifiables
        budget.setNom(budgetMiseAJour.getNom());
        budget.setDescription(budgetMiseAJour.getDescription());
        budget.setMontantTotal(budgetMiseAJour.getMontantTotal());
        budget.setSeuilAlerte(budgetMiseAJour.getSeuilAlerte());
        budget.setAutoRenouvellement(budgetMiseAJour.getAutoRenouvellement());
        
        // Recalcul du montant restant
        budget.setMontantRestant(budget.getMontantTotal().subtract(budget.getMontantUtilise()));
        
        return budgetRepository.save(budget);
    }

    /**
     * Utilise un montant du budget
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public Budget utiliserMontantBudget(Long budgetId, BigDecimal montant, String description) {
        log.info("Utilisation de {} du budget ID: {}", montant, budgetId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé: " + budgetId));
        
        budget.utiliserMontant(montant);
        Budget budgetMisAJour = budgetRepository.save(budget);
        
        // Vérification du seuil d'alerte
        if (budget.getPourcentageUtilise().compareTo(budget.getSeuilAlerte()) >= 0) {
            log.warn("Seuil d'alerte atteint pour le budget {}: {}%", budget.getNom(), budget.getPourcentageUtilise());
            // TODO: Envoyer notification d'alerte
        }
        
        return budgetMisAJour;
    }

    /**
     * Libère un montant du budget (annulation de dépense)
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public Budget libererMontantBudget(Long budgetId, BigDecimal montant, String description) {
        log.info("Libération de {} du budget ID: {}", montant, budgetId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé: " + budgetId));
        
        budget.libererMontant(montant);
        return budgetRepository.save(budget);
    }

    /**
     * Clôture un budget
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public Budget cloturerBudget(Long budgetId) {
        log.info("Clôture du budget ID: {}", budgetId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé: " + budgetId));
        
        budget.setStatut(Budget.StatutBudget.CLOTURE);
        return budgetRepository.save(budget);
    }

    /**
     * Renouvelle automatiquement un budget
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public Budget renouvellerBudget(Long budgetId) {
        log.info("Renouvellement du budget ID: {}", budgetId);
        
        Budget budgetOriginal = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé: " + budgetId));
        
        if (!budgetOriginal.getAutoRenouvellement()) {
            throw new IllegalStateException("Le budget n'est pas configuré pour le renouvellement automatique");
        }
        
        // Création du nouveau budget
        Budget nouveauBudget = new Budget();
        nouveauBudget.setNom(budgetOriginal.getNom() + " - Renouvelé");
        nouveauBudget.setDescription(budgetOriginal.getDescription());
        nouveauBudget.setMontantTotal(budgetOriginal.getMontantTotal());
        nouveauBudget.setPeriodeBudget(budgetOriginal.getPeriodeBudget());
        nouveauBudget.setSeuilAlerte(budgetOriginal.getSeuilAlerte());
        nouveauBudget.setAutoRenouvellement(budgetOriginal.getAutoRenouvellement());
        
        // Calcul des nouvelles dates
        LocalDate nouvelleDate = budgetOriginal.getDateFin().plusDays(1);
        nouveauBudget.setDateDebut(nouvelleDate);
        
        switch (budgetOriginal.getPeriodeBudget()) {
            case MENSUEL -> nouveauBudget.setDateFin(nouvelleDate.plusMonths(1).minusDays(1));
            case TRIMESTRIEL -> nouveauBudget.setDateFin(nouvelleDate.plusMonths(3).minusDays(1));
            case ANNUEL -> nouveauBudget.setDateFin(nouvelleDate.plusYears(1).minusDays(1));
        }
        
        // Clôture de l'ancien budget
        budgetOriginal.setStatut(Budget.StatutBudget.CLOTURE);
        budgetRepository.save(budgetOriginal);
        
        return creerBudget(nouveauBudget);
    }

    /**
     * Trouve tous les budgets actifs
     */
    @Cacheable(value = "budgets-actifs")
    public List<Budget> obtenirBudgetsActifs() {
        return budgetRepository.findByStatutOrderByDateDebutDesc(Budget.StatutBudget.ACTIF);
    }

    /**
     * Trouve les budgets avec alerte
     */
    public List<Budget> obtenirBudgetsAvecAlerte() {
        return budgetRepository.findBudgetsAvecAlerte();
    }

    /**
     * Trouve les budgets proches de l'expiration
     */
    public List<Budget> obtenirBudgetsProchesExpiration(int joursAvance) {
        LocalDate dateLimite = LocalDate.now().plusDays(joursAvance);
        return budgetRepository.findBudgetsProchesExpiration(LocalDate.now(), dateLimite);
    }

    /**
     * Obtient les statistiques des budgets
     */
    public List<Object[]> obtenirStatistiquesBudgets() {
        return budgetRepository.getStatistiquesParStatut();
    }

    /**
     * Recherche avancée de budgets
     */
    public Page<Budget> rechercherBudgets(String nom, Budget.StatutBudget statut, 
                                         Budget.PeriodeBudget periodeBudget, LocalDate dateDebut, 
                                         LocalDate dateFin, BigDecimal montantMin, 
                                         BigDecimal montantMax, Pageable pageable) {
        return budgetRepository.rechercheAvancee(nom, statut, periodeBudget, dateDebut, 
                                               dateFin, montantMin, montantMax, pageable);
    }

    /**
     * Trouve un budget par ID
     */
    @Cacheable(value = "budgets", key = "#id")
    public Optional<Budget> obtenirBudgetParId(Long id) {
        return budgetRepository.findById(id);
    }

    /**
     * Supprime un budget
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public void supprimerBudget(Long id) {
        log.info("Suppression du budget ID: {}", id);
        
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé: " + id));
        
        // Vérification qu'aucune transaction n'est liée
        if (budget.getMontantUtilise().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Impossible de supprimer un budget avec des transactions");
        }
        
        budgetRepository.delete(budget);
    }

    /**
     * Traite les renouvellements automatiques
     */
    @CacheEvict(value = {"budgets", "budgets-actifs"}, allEntries = true)
    public void traiterRenouvellements() {
        log.info("Traitement des renouvellements automatiques");
        
        List<Budget> budgetsARenouveler = budgetRepository.findBudgetsProchesExpiration(
                LocalDate.now(), LocalDate.now().plusDays(7));
        
        for (Budget budget : budgetsARenouveler) {
            if (budget.getAutoRenouvellement()) {
                try {
                    renouvellerBudget(budget.getId());
                    log.info("Budget renouvelé automatiquement: {}", budget.getNom());
                } catch (Exception e) {
                    log.error("Erreur lors du renouvellement du budget {}: {}", budget.getNom(), e.getMessage());
                }
            }
        }
    }
}
