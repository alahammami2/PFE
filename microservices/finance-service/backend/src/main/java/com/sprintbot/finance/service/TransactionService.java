package com.sprintbot.finance.service;

import com.sprintbot.finance.entity.Transaction;
import com.sprintbot.finance.entity.Budget;
import com.sprintbot.finance.entity.CategorieBudget;
import com.sprintbot.finance.entity.CategorieTransaction;
import com.sprintbot.finance.repository.TransactionRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des transactions financières
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CategorieBudgetRepository categorieBudgetRepository;
    private final BudgetService budgetService;

    /**
     * Crée une nouvelle transaction
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public Transaction creerTransaction(Transaction transaction) {
        log.info("Création d'une nouvelle transaction: {}", transaction.getDescription());
        
        // Génération de la référence si non fournie
        if (transaction.getReference() == null || transaction.getReference().isEmpty()) {
            transaction.setReference(genererReference());
        }
        
        // Vérification de l'unicité de la référence
        if (transactionRepository.existsByReference(transaction.getReference())) {
            throw new IllegalArgumentException("Une transaction avec cette référence existe déjà");
        }
        
        // Initialisation du statut
        transaction.setStatut(Transaction.StatutTransaction.EN_ATTENTE);
        
        // Calcul de la TVA si applicable
        if (transaction.getTvaApplicable()) {
            transaction.calculerMontantTTC();
        }
        
        Transaction transactionSauvegardee = transactionRepository.save(transaction);
        log.info("Transaction créée avec succès: ID {}", transactionSauvegardee.getId());
        
        return transactionSauvegardee;
    }

    /**
     * Valide une transaction
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public Transaction validerTransaction(Long transactionId, Long validateurId) {
        log.info("Validation de la transaction ID: {} par utilisateur: {}", transactionId, validateurId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + transactionId));
        
        // Validation de la transaction
        transaction.valider(validateurId);
        
        // Mise à jour du budget si c'est une dépense
        if (transaction.getTypeTransaction() == Transaction.TypeTransaction.DEPENSE && 
            transaction.getBudget() != null) {
            budgetService.utiliserMontantBudget(transaction.getBudget().getId(), 
                                              transaction.getMontant(), 
                                              "Transaction: " + transaction.getReference());
        }
        
        // Mise à jour de la catégorie budget si applicable
        if (transaction.getCategorieBudget() != null) {
            CategorieBudget categorie = transaction.getCategorieBudget();
            if (transaction.getTypeTransaction() == Transaction.TypeTransaction.DEPENSE) {
                categorie.utiliserMontant(transaction.getMontant());
            } else {
                categorie.libererMontant(transaction.getMontant());
            }
            categorieBudgetRepository.save(categorie);
        }
        
        Transaction transactionValidee = transactionRepository.save(transaction);
        log.info("Transaction validée avec succès: {}", transaction.getReference());
        
        return transactionValidee;
    }

    /**
     * Rejette une transaction
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public Transaction rejeterTransaction(Long transactionId, Long validateurId, String motifRejet) {
        log.info("Rejet de la transaction ID: {} par utilisateur: {}", transactionId, validateurId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + transactionId));
        
        transaction.rejeter(validateurId, motifRejet);
        
        Transaction transactionRejetee = transactionRepository.save(transaction);
        log.info("Transaction rejetée: {}", transaction.getReference());
        
        return transactionRejetee;
    }

    /**
     * Annule une transaction validée
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public Transaction annulerTransaction(Long transactionId, String motifAnnulation) {
        log.info("Annulation de la transaction ID: {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + transactionId));
        
        if (transaction.getStatut() != Transaction.StatutTransaction.VALIDEE) {
            throw new IllegalStateException("Seules les transactions validées peuvent être annulées");
        }
        
        // Libération du montant du budget si c'était une dépense
        if (transaction.getTypeTransaction() == Transaction.TypeTransaction.DEPENSE && 
            transaction.getBudget() != null) {
            budgetService.libererMontantBudget(transaction.getBudget().getId(), 
                                             transaction.getMontant(), 
                                             "Annulation: " + transaction.getReference());
        }
        
        // Libération du montant de la catégorie budget
        if (transaction.getCategorieBudget() != null) {
            CategorieBudget categorie = transaction.getCategorieBudget();
            if (transaction.getTypeTransaction() == Transaction.TypeTransaction.DEPENSE) {
                categorie.libererMontant(transaction.getMontant());
            } else {
                categorie.utiliserMontant(transaction.getMontant());
            }
            categorieBudgetRepository.save(categorie);
        }
        
        transaction.setStatut(Transaction.StatutTransaction.ANNULEE);
        transaction.setMotifRejet(motifAnnulation);
        
        return transactionRepository.save(transaction);
    }

    /**
     * Obtient les transactions en attente de validation
     */
    public List<Transaction> obtenirTransactionsEnAttente() {
        return transactionRepository.findTransactionsEnAttenteValidation();
    }

    /**
     * Obtient les transactions validées pour une période
     */
    @Cacheable(value = "transactions-validees", key = "#dateDebut + '_' + #dateFin")
    public List<Transaction> obtenirTransactionsValidees(LocalDate dateDebut, LocalDate dateFin) {
        return transactionRepository.findTransactionsValideesPourPeriode(dateDebut, dateFin);
    }

    /**
     * Calcule le total des recettes pour une période
     */
    public BigDecimal calculerTotalRecettes(LocalDate dateDebut, LocalDate dateFin) {
        return transactionRepository.calculerTotalRecettesPourPeriode(dateDebut, dateFin);
    }

    /**
     * Calcule le total des dépenses pour une période
     */
    public BigDecimal calculerTotalDepenses(LocalDate dateDebut, LocalDate dateFin) {
        return transactionRepository.calculerTotalDepensesPourPeriode(dateDebut, dateFin);
    }

    /**
     * Calcule le solde pour une période
     */
    public BigDecimal calculerSolde(LocalDate dateDebut, LocalDate dateFin) {
        return transactionRepository.calculerSoldePourPeriode(dateDebut, dateFin);
    }

    /**
     * Obtient les statistiques des transactions
     */
    public List<Object[]> obtenirStatistiquesTransactions() {
        return transactionRepository.getStatistiquesParCategorie();
    }

    /**
     * Obtient les statistiques mensuelles
     */
    public List<Object[]> obtenirStatistiquesMensuelles() {
        return transactionRepository.getStatistiquesMensuelles();
    }

    /**
     * Recherche avancée de transactions
     */
    public Page<Transaction> rechercherTransactions(String reference, Transaction.TypeTransaction typeTransaction,
                                                   Transaction.StatutTransaction statut, CategorieTransaction categorieTransaction,
                                                   Budget budget, Long utilisateurId, LocalDate dateDebut,
                                                   LocalDate dateFin, BigDecimal montantMin, BigDecimal montantMax,
                                                   Pageable pageable) {
        return transactionRepository.rechercheAvancee(reference, typeTransaction, statut, categorieTransaction,
                                                     budget, utilisateurId, dateDebut, dateFin, montantMin, montantMax, pageable);
    }

    /**
     * Recherche textuelle dans les transactions
     */
    public Page<Transaction> rechercheTextuelle(String texte, Pageable pageable) {
        return transactionRepository.rechercheTextuelle(texte, pageable);
    }

    /**
     * Trouve une transaction par ID
     */
    @Cacheable(value = "transactions", key = "#id")
    public Optional<Transaction> obtenirTransactionParId(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Trouve une transaction par référence
     */
    public List<Transaction> obtenirTransactionsParReference(String reference) {
        return transactionRepository.findByReferenceContainingIgnoreCaseOrderByDateCreationDesc(reference);
    }

    /**
     * Obtient les transactions du jour
     */
    public List<Transaction> obtenirTransactionsDuJour() {
        return transactionRepository.findTransactionsDuJour();
    }

    /**
     * Traite les transactions en attente depuis trop longtemps
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public void traiterTransactionsEnAttenteLongue(int joursLimite) {
        log.info("Traitement des transactions en attente depuis plus de {} jours", joursLimite);
        
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursLimite);
        List<Transaction> transactionsEnAttente = transactionRepository.findTransactionsEnAttenteDepuis(dateLimite);
        
        for (Transaction transaction : transactionsEnAttente) {
            log.warn("Transaction en attente depuis {} jours: {}", 
                    java.time.Duration.between(transaction.getDateCreation(), LocalDateTime.now()).toDays(),
                    transaction.getReference());
            // TODO: Envoyer notification de rappel
        }
    }

    /**
     * Génère une référence unique pour la transaction
     */
    private String genererReference() {
        String prefix = "TXN";
        String date = LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + date + "-" + uuid;
    }

    /**
     * Met à jour une transaction
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public Transaction mettreAJourTransaction(Long id, Transaction transactionMiseAJour) {
        log.info("Mise à jour de la transaction ID: {}", id);
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + id));
        
        if (transaction.getStatut() == Transaction.StatutTransaction.VALIDEE) {
            throw new IllegalStateException("Impossible de modifier une transaction validée");
        }
        
        // Mise à jour des champs modifiables
        transaction.setDescription(transactionMiseAJour.getDescription());
        transaction.setMontant(transactionMiseAJour.getMontant());
        transaction.setBeneficiaire(transactionMiseAJour.getBeneficiaire());
        transaction.setModePaiement(transactionMiseAJour.getModePaiement());
        transaction.setCategorieTransaction(transactionMiseAJour.getCategorieTransaction());
        transaction.setBudget(transactionMiseAJour.getBudget());
        transaction.setCategorieBudget(transactionMiseAJour.getCategorieBudget());
        
        // Recalcul de la TVA si applicable
        if (transaction.getTvaApplicable()) {
            transaction.calculerMontantTTC();
        }
        
        return transactionRepository.save(transaction);
    }

    /**
     * Supprime une transaction
     */
    @CacheEvict(value = {"transactions", "transactions-validees"}, allEntries = true)
    public void supprimerTransaction(Long id) {
        log.info("Suppression de la transaction ID: {}", id);
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée: " + id));
        
        if (transaction.getStatut() == Transaction.StatutTransaction.VALIDEE) {
            throw new IllegalStateException("Impossible de supprimer une transaction validée");
        }
        
        transactionRepository.delete(transaction);
    }
}
