package com.sprintbot.finance.controller;

import com.sprintbot.finance.entity.Transaction;
import com.sprintbot.finance.service.TransactionService;
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
 * Contrôleur REST pour la gestion des transactions
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction", description = "API de gestion des transactions financières")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Crée une nouvelle transaction
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Créer une transaction", description = "Crée une nouvelle transaction financière")
    public ResponseEntity<Transaction> creerTransaction(@Valid @RequestBody Transaction transaction) {
        log.info("Création d'une nouvelle transaction: {}", transaction.getDescription());
        Transaction transactionCreee = transactionService.creerTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionCreee);
    }

    /**
     * Met à jour une transaction
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Mettre à jour une transaction", description = "Met à jour une transaction existante")
    public ResponseEntity<Transaction> mettreAJourTransaction(
            @Parameter(description = "ID de la transaction") @PathVariable Long id,
            @Valid @RequestBody Transaction transaction) {
        log.info("Mise à jour de la transaction ID: {}", id);
        Transaction transactionMiseAJour = transactionService.mettreAJourTransaction(id, transaction);
        return ResponseEntity.ok(transactionMiseAJour);
    }

    /**
     * Valide une transaction
     */
    @PostMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Valider une transaction", description = "Valide une transaction en attente")
    public ResponseEntity<Transaction> validerTransaction(
            @Parameter(description = "ID de la transaction") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long validateurId = Long.valueOf(request.get("validateurId").toString());
        
        log.info("Validation de la transaction ID: {} par utilisateur: {}", id, validateurId);
        Transaction transaction = transactionService.validerTransaction(id, validateurId);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Rejette une transaction
     */
    @PostMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rejeter une transaction", description = "Rejette une transaction en attente")
    public ResponseEntity<Transaction> rejeterTransaction(
            @Parameter(description = "ID de la transaction") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long validateurId = Long.valueOf(request.get("validateurId").toString());
        String motifRejet = (String) request.get("motifRejet");
        
        log.info("Rejet de la transaction ID: {} par utilisateur: {}", id, validateurId);
        Transaction transaction = transactionService.rejeterTransaction(id, validateurId, motifRejet);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Annule une transaction
     */
    @PostMapping("/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Annuler une transaction", description = "Annule une transaction validée")
    public ResponseEntity<Transaction> annulerTransaction(
            @Parameter(description = "ID de la transaction") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String motifAnnulation = request.get("motifAnnulation");
        
        log.info("Annulation de la transaction ID: {}", id);
        Transaction transaction = transactionService.annulerTransaction(id, motifAnnulation);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Obtient les transactions en attente
     */
    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les transactions en attente", description = "Récupère toutes les transactions en attente de validation")
    public ResponseEntity<List<Transaction>> obtenirTransactionsEnAttente() {
        List<Transaction> transactions = transactionService.obtenirTransactionsEnAttente();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Obtient les transactions validées pour une période
     */
    @GetMapping("/validees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir les transactions validées", description = "Récupère les transactions validées pour une période donnée")
    public ResponseEntity<List<Transaction>> obtenirTransactionsValidees(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<Transaction> transactions = transactionService.obtenirTransactionsValidees(dateDebut, dateFin);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Obtient les transactions du jour
     */
    @GetMapping("/du-jour")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir les transactions du jour", description = "Récupère toutes les transactions du jour")
    public ResponseEntity<List<Transaction>> obtenirTransactionsDuJour() {
        List<Transaction> transactions = transactionService.obtenirTransactionsDuJour();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Calcule le total des recettes
     */
    @GetMapping("/recettes/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Calculer le total des recettes", description = "Calcule le montant total des recettes pour une période")
    public ResponseEntity<BigDecimal> calculerTotalRecettes(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        BigDecimal total = transactionService.calculerTotalRecettes(dateDebut, dateFin);
        return ResponseEntity.ok(total);
    }

    /**
     * Calcule le total des dépenses
     */
    @GetMapping("/depenses/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Calculer le total des dépenses", description = "Calcule le montant total des dépenses pour une période")
    public ResponseEntity<BigDecimal> calculerTotalDepenses(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        BigDecimal total = transactionService.calculerTotalDepenses(dateDebut, dateFin);
        return ResponseEntity.ok(total);
    }

    /**
     * Calcule le solde
     */
    @GetMapping("/solde")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Calculer le solde", description = "Calcule le solde (recettes - dépenses) pour une période")
    public ResponseEntity<BigDecimal> calculerSolde(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        BigDecimal solde = transactionService.calculerSolde(dateDebut, dateFin);
        return ResponseEntity.ok(solde);
    }

    /**
     * Obtient une transaction par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir une transaction par ID", description = "Récupère les détails d'une transaction spécifique")
    public ResponseEntity<Transaction> obtenirTransactionParId(@Parameter(description = "ID de la transaction") @PathVariable Long id) {
        return transactionService.obtenirTransactionParId(id)
                .map(transaction -> ResponseEntity.ok(transaction))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recherche par référence
     */
    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Rechercher par référence", description = "Recherche les transactions par référence")
    public ResponseEntity<List<Transaction>> obtenirTransactionsParReference(
            @Parameter(description = "Référence de la transaction") @PathVariable String reference) {
        List<Transaction> transactions = transactionService.obtenirTransactionsParReference(reference);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Recherche textuelle
     */
    @GetMapping("/recherche-textuelle")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Recherche textuelle", description = "Effectue une recherche textuelle dans les transactions")
    public ResponseEntity<Page<Transaction>> rechercheTextuelle(
            @Parameter(description = "Texte à rechercher") @RequestParam String texte,
            Pageable pageable) {
        Page<Transaction> transactions = transactionService.rechercheTextuelle(texte, pageable);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Recherche avancée
     */
    @GetMapping("/recherche")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Recherche avancée", description = "Effectue une recherche avancée avec filtres multiples")
    public ResponseEntity<Page<Transaction>> rechercherTransactions(
            @Parameter(description = "Référence") @RequestParam(required = false) String reference,
            @Parameter(description = "Type de transaction") @RequestParam(required = false) Transaction.TypeTransaction typeTransaction,
            @Parameter(description = "Statut") @RequestParam(required = false) Transaction.StatutTransaction statut,
            @Parameter(description = "ID utilisateur") @RequestParam(required = false) Long utilisateurId,
            @Parameter(description = "Date de début") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @Parameter(description = "Montant minimum") @RequestParam(required = false) BigDecimal montantMin,
            @Parameter(description = "Montant maximum") @RequestParam(required = false) BigDecimal montantMax,
            Pageable pageable) {
        
        Page<Transaction> transactions = transactionService.rechercherTransactions(reference, typeTransaction, statut, 
                                                                                  null, null, utilisateurId, dateDebut, dateFin, 
                                                                                  montantMin, montantMax, pageable);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Obtient les statistiques des transactions
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les statistiques", description = "Récupère les statistiques des transactions")
    public ResponseEntity<List<Object[]>> obtenirStatistiquesTransactions() {
        List<Object[]> statistiques = transactionService.obtenirStatistiquesTransactions();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Obtient les statistiques mensuelles
     */
    @GetMapping("/statistiques/mensuelles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les statistiques mensuelles", description = "Récupère les statistiques mensuelles des transactions")
    public ResponseEntity<List<Object[]>> obtenirStatistiquesMensuelles() {
        List<Object[]> statistiques = transactionService.obtenirStatistiquesMensuelles();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Traite les transactions en attente longue
     */
    @PostMapping("/traiter-attente-longue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Traiter les transactions en attente", description = "Lance le traitement des transactions en attente depuis trop longtemps")
    public ResponseEntity<String> traiterTransactionsEnAttenteLongue(
            @Parameter(description = "Nombre de jours limite") @RequestParam(defaultValue = "7") int joursLimite) {
        transactionService.traiterTransactionsEnAttenteLongue(joursLimite);
        return ResponseEntity.ok("Traitement des transactions en attente lancé avec succès");
    }

    /**
     * Supprime une transaction
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une transaction", description = "Supprime définitivement une transaction")
    public ResponseEntity<Void> supprimerTransaction(@Parameter(description = "ID de la transaction") @PathVariable Long id) {
        log.info("Suppression de la transaction ID: {}", id);
        transactionService.supprimerTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
