package com.sprintbot.finance.controller;

import com.sprintbot.finance.entity.Budget;
import com.sprintbot.finance.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des budgets
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Budget", description = "API de gestion des budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Crée un nouveau budget
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Créer un budget", description = "Crée un nouveau budget avec validation des données")
    public ResponseEntity<Budget> creerBudget(@Valid @RequestBody Budget budget) {
        log.info("Création d'un nouveau budget: {}", budget.getNom());
        Budget budgetCree = budgetService.creerBudget(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetCree);
    }

    /**
     * Met à jour un budget existant
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Mettre à jour un budget", description = "Met à jour les informations d'un budget existant")
    public ResponseEntity<Budget> mettreAJourBudget(
            @Parameter(description = "ID du budget") @PathVariable Long id,
            @Valid @RequestBody Budget budget) {
        log.info("Mise à jour du budget ID: {}", id);
        Budget budgetMisAJour = budgetService.mettreAJourBudget(id, budget);
        return ResponseEntity.ok(budgetMisAJour);
    }

    /**
     * Utilise un montant du budget
     */
    @PostMapping("/{id}/utiliser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Utiliser un montant du budget", description = "Utilise un montant spécifique du budget")
    public ResponseEntity<Budget> utiliserMontant(
            @Parameter(description = "ID du budget") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        BigDecimal montant = new BigDecimal(request.get("montant").toString());
        String description = (String) request.get("description");
        
        log.info("Utilisation de {} du budget ID: {}", montant, id);
        Budget budget = budgetService.utiliserMontantBudget(id, montant, description);
        return ResponseEntity.ok(budget);
    }

    /**
     * Libère un montant du budget
     */
    @PostMapping("/{id}/liberer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Libérer un montant du budget", description = "Libère un montant du budget (annulation de dépense)")
    public ResponseEntity<Budget> libererMontant(
            @Parameter(description = "ID du budget") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        BigDecimal montant = new BigDecimal(request.get("montant").toString());
        String description = (String) request.get("description");
        
        log.info("Libération de {} du budget ID: {}", montant, id);
        Budget budget = budgetService.libererMontantBudget(id, montant, description);
        return ResponseEntity.ok(budget);
    }

    /**
     * Clôture un budget
     */
    @PostMapping("/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Clôturer un budget", description = "Clôture définitivement un budget")
    public ResponseEntity<Budget> cloturerBudget(@Parameter(description = "ID du budget") @PathVariable Long id) {
        log.info("Clôture du budget ID: {}", id);
        Budget budget = budgetService.cloturerBudget(id);
        return ResponseEntity.ok(budget);
    }

    /**
     * Renouvelle un budget
     */
    @PostMapping("/{id}/renouveler")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Renouveler un budget", description = "Renouvelle automatiquement un budget")
    public ResponseEntity<Budget> renouvellerBudget(@Parameter(description = "ID du budget") @PathVariable Long id) {
        log.info("Renouvellement du budget ID: {}", id);
        Budget budget = budgetService.renouvellerBudget(id);
        return ResponseEntity.ok(budget);
    }

    /**
     * Obtient tous les budgets actifs
     */
    @GetMapping("/actifs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir les budgets actifs", description = "Récupère la liste de tous les budgets actifs")
    public ResponseEntity<List<Budget>> obtenirBudgetsActifs() {
        List<Budget> budgets = budgetService.obtenirBudgetsActifs();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Obtient les budgets avec alerte
     */
    @GetMapping("/alertes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les budgets avec alerte", description = "Récupère les budgets ayant dépassé leur seuil d'alerte")
    public ResponseEntity<List<Budget>> obtenirBudgetsAvecAlerte() {
        List<Budget> budgets = budgetService.obtenirBudgetsAvecAlerte();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Obtient les budgets proches de l'expiration
     */
    @GetMapping("/expiration")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les budgets proches d'expiration", description = "Récupère les budgets proches de leur date d'expiration")
    public ResponseEntity<List<Budget>> obtenirBudgetsProchesExpiration(
            @Parameter(description = "Nombre de jours d'avance") @RequestParam(defaultValue = "30") int joursAvance) {
        List<Budget> budgets = budgetService.obtenirBudgetsProchesExpiration(joursAvance);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Obtient un budget par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir un budget par ID", description = "Récupère les détails d'un budget spécifique")
    public ResponseEntity<Budget> obtenirBudgetParId(@Parameter(description = "ID du budget") @PathVariable Long id) {
        return budgetService.obtenirBudgetParId(id)
                .map(budget -> ResponseEntity.ok(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recherche avancée de budgets
     */
    @GetMapping("/recherche")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Recherche avancée de budgets", description = "Effectue une recherche avancée avec filtres multiples")
    public ResponseEntity<Page<Budget>> rechercherBudgets(
            @Parameter(description = "Nom du budget") @RequestParam(required = false) String nom,
            @Parameter(description = "Statut du budget") @RequestParam(required = false) Budget.StatutBudget statut,
            @Parameter(description = "Période du budget") @RequestParam(required = false) Budget.PeriodeBudget periodeBudget,
            @Parameter(description = "Date de début minimum") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin maximum") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @Parameter(description = "Montant minimum") @RequestParam(required = false) BigDecimal montantMin,
            @Parameter(description = "Montant maximum") @RequestParam(required = false) BigDecimal montantMax,
            Pageable pageable) {
        
        Page<Budget> budgets = budgetService.rechercherBudgets(nom, statut, periodeBudget, 
                                                              dateDebut, dateFin, montantMin, montantMax, pageable);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Obtient les statistiques des budgets
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les statistiques des budgets", description = "Récupère les statistiques globales des budgets")
    public ResponseEntity<List<Object[]>> obtenirStatistiquesBudgets() {
        List<Object[]> statistiques = budgetService.obtenirStatistiquesBudgets();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Traite les renouvellements automatiques
     */
    @PostMapping("/traiter-renouvellements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Traiter les renouvellements", description = "Lance le traitement des renouvellements automatiques")
    public ResponseEntity<String> traiterRenouvellements() {
        budgetService.traiterRenouvellements();
        return ResponseEntity.ok("Traitement des renouvellements lancé avec succès");
    }

    /**
     * Supprime un budget
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un budget", description = "Supprime définitivement un budget")
    public ResponseEntity<Void> supprimerBudget(@Parameter(description = "ID du budget") @PathVariable Long id) {
        log.info("Suppression du budget ID: {}", id);
        budgetService.supprimerBudget(id);
        return ResponseEntity.noContent().build();
    }
}
