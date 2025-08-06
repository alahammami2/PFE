package com.sprintbot.finance.service;

import com.sprintbot.finance.entity.Budget;
import com.sprintbot.finance.entity.Sponsor;
import com.sprintbot.finance.entity.Transaction;
import com.sprintbot.finance.repository.BudgetRepository;
import com.sprintbot.finance.repository.SponsorRepository;
import com.sprintbot.finance.repository.TransactionRepository;
import com.sprintbot.finance.repository.SalaireRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour les notifications financières
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationFinanceService {

    private final BudgetRepository budgetRepository;
    private final SponsorRepository sponsorRepository;
    private final TransactionRepository transactionRepository;
    private final SalaireRepository salaireRepository;
    private final JavaMailSender mailSender;

    @Value("${finance.notification.email.from:finance@sprintbot.com}")
    private String emailFrom;

    @Value("${finance.notification.email.admin:admin@sprintbot.com}")
    private String emailAdmin;

    @Value("${finance.notification.enabled:true}")
    private boolean notificationsEnabled;

    /**
     * Vérifie les alertes budgétaires (exécuté quotidiennement)
     */
    @Scheduled(cron = "0 0 9 * * ?") // Tous les jours à 9h
    public void verifierAlertesBudgetaires() {
        if (!notificationsEnabled) return;
        
        log.info("Vérification des alertes budgétaires");
        
        List<Budget> budgetsAvecAlerte = budgetRepository.findBudgetsAvecAlerte();
        
        if (!budgetsAvecAlerte.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Alerte budgétaire - Les budgets suivants ont dépassé leur seuil d'alerte :\n\n");
            
            for (Budget budget : budgetsAvecAlerte) {
                message.append(String.format("- %s : %.2f%% utilisé (seuil: %.2f%%)\n",
                        budget.getNom(),
                        budget.getPourcentageUtilise(),
                        budget.getSeuilAlerte()));
            }
            
            envoyerNotificationEmail("Alerte Budgétaire", message.toString());
        }
        
        // Vérification des budgets proches de l'expiration
        List<Budget> budgetsProchesExpiration = budgetRepository.findBudgetsProchesExpiration(
                LocalDate.now(), LocalDate.now().plusDays(7));
        
        if (!budgetsProchesExpiration.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Budgets proches de l'expiration (dans les 7 prochains jours) :\n\n");
            
            for (Budget budget : budgetsProchesExpiration) {
                message.append(String.format("- %s : expire le %s\n",
                        budget.getNom(),
                        budget.getDateFin()));
            }
            
            envoyerNotificationEmail("Budgets Proches d'Expiration", message.toString());
        }
    }

    /**
     * Vérifie les alertes de sponsors (exécuté quotidiennement)
     */
    @Scheduled(cron = "0 0 10 * * ?") // Tous les jours à 10h
    public void verifierAlertesSponsors() {
        if (!notificationsEnabled) return;
        
        log.info("Vérification des alertes sponsors");
        
        // Sponsors proches de l'expiration
        List<Sponsor> sponsorsProchesExpiration = sponsorRepository.findSponsorsProchesExpiration(
                LocalDate.now().plusDays(30));
        
        if (!sponsorsProchesExpiration.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Contrats de sponsoring proches de l'expiration (dans les 30 prochains jours) :\n\n");
            
            for (Sponsor sponsor : sponsorsProchesExpiration) {
                message.append(String.format("- %s : expire le %s (Montant: %.2f €)\n",
                        sponsor.getNom(),
                        sponsor.getDateFin(),
                        sponsor.getMontantContrat()));
            }
            
            envoyerNotificationEmail("Contrats de Sponsoring Proches d'Expiration", message.toString());
        }
        
        // Sponsors avec paiements en retard
        List<Sponsor> sponsorsAvecRetard = sponsorRepository.findSponsorsAvecPaiementsEnRetard();
        
        if (!sponsorsAvecRetard.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Sponsors avec des paiements en retard :\n\n");
            
            for (Sponsor sponsor : sponsorsAvecRetard) {
                message.append(String.format("- %s : Montant restant %.2f €\n",
                        sponsor.getNom(),
                        sponsor.getMontantRestant()));
            }
            
            envoyerNotificationEmail("Paiements de Sponsors en Retard", message.toString());
        }
    }

    /**
     * Vérifie les transactions en attente (exécuté quotidiennement)
     */
    @Scheduled(cron = "0 0 11 * * ?") // Tous les jours à 11h
    public void verifierTransactionsEnAttente() {
        if (!notificationsEnabled) return;
        
        log.info("Vérification des transactions en attente");
        
        // Transactions en attente depuis plus de 3 jours
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(3);
        List<Transaction> transactionsEnAttente = transactionRepository.findTransactionsEnAttenteDepuis(dateLimite);
        
        if (!transactionsEnAttente.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Transactions en attente de validation depuis plus de 3 jours :\n\n");
            
            for (Transaction transaction : transactionsEnAttente) {
                long joursAttente = java.time.Duration.between(transaction.getDateCreation(), LocalDateTime.now()).toDays();
                message.append(String.format("- %s : %s (%.2f €) - %d jours d'attente\n",
                        transaction.getReference(),
                        transaction.getDescription(),
                        transaction.getMontant(),
                        joursAttente));
            }
            
            envoyerNotificationEmail("Transactions en Attente de Validation", message.toString());
        }
    }

    /**
     * Vérifie les salaires en attente (exécuté quotidiennement)
     */
    @Scheduled(cron = "0 0 12 * * ?") // Tous les jours à 12h
    public void verifierSalairesEnAttente() {
        if (!notificationsEnabled) return;
        
        log.info("Vérification des salaires en attente");
        
        long salairesEnAttente = salaireRepository.countByStatut(
                com.sprintbot.finance.entity.Salaire.StatutSalaire.CALCULE);
        
        if (salairesEnAttente > 0) {
            String message = String.format("Il y a %d salaire(s) en attente de validation.", salairesEnAttente);
            envoyerNotificationEmail("Salaires en Attente de Validation", message);
        }
        
        long salairesValides = salaireRepository.countByStatut(
                com.sprintbot.finance.entity.Salaire.StatutSalaire.VALIDE);
        
        if (salairesValides > 0) {
            String message = String.format("Il y a %d salaire(s) validé(s) en attente de paiement.", salairesValides);
            envoyerNotificationEmail("Salaires en Attente de Paiement", message);
        }
    }

    /**
     * Rapport financier hebdomadaire (exécuté le lundi à 8h)
     */
    @Scheduled(cron = "0 0 8 * * MON")
    public void envoyerRapportHebdomadaire() {
        if (!notificationsEnabled) return;
        
        log.info("Envoi du rapport financier hebdomadaire");
        
        LocalDate finSemaine = LocalDate.now().minusDays(1);
        LocalDate debutSemaine = finSemaine.minusDays(6);
        
        StringBuilder rapport = new StringBuilder();
        rapport.append("RAPPORT FINANCIER HEBDOMADAIRE\n");
        rapport.append("Période: ").append(debutSemaine).append(" au ").append(finSemaine).append("\n\n");
        
        // Résumé budgétaire
        BigDecimal budgetTotal = budgetRepository.calculerMontantTotalBudgetsActifs();
        BigDecimal budgetUtilise = budgetRepository.calculerMontantUtiliseTotalBudgetsActifs();
        
        rapport.append("BUDGET:\n");
        rapport.append(String.format("- Total: %.2f €\n", budgetTotal));
        rapport.append(String.format("- Utilisé: %.2f €\n", budgetUtilise));
        rapport.append(String.format("- Restant: %.2f €\n", budgetTotal.subtract(budgetUtilise)));
        
        // Résumé des transactions
        BigDecimal recettes = transactionRepository.calculerTotalRecettesPourPeriode(debutSemaine, finSemaine);
        BigDecimal depenses = transactionRepository.calculerTotalDepensesPourPeriode(debutSemaine, finSemaine);
        
        rapport.append("\nTRANSACTIONS:\n");
        rapport.append(String.format("- Recettes: %.2f €\n", recettes));
        rapport.append(String.format("- Dépenses: %.2f €\n", depenses));
        rapport.append(String.format("- Solde: %.2f €\n", recettes.subtract(depenses)));
        
        // Résumé des sponsors
        BigDecimal sponsorTotal = sponsorRepository.calculerMontantTotalContratsActifs();
        BigDecimal sponsorVerse = sponsorRepository.calculerMontantTotalVerse();
        
        rapport.append("\nSPONSORS:\n");
        rapport.append(String.format("- Contrats actifs: %.2f €\n", sponsorTotal));
        rapport.append(String.format("- Montant versé: %.2f €\n", sponsorVerse));
        rapport.append(String.format("- Montant restant: %.2f €\n", sponsorTotal.subtract(sponsorVerse)));
        
        // Alertes
        int budgetsAlerte = budgetRepository.findBudgetsAvecAlerte().size();
        long transactionsAttente = transactionRepository.countByStatut(Transaction.StatutTransaction.EN_ATTENTE);
        
        rapport.append("\nALERTES:\n");
        rapport.append(String.format("- Budgets avec alerte: %d\n", budgetsAlerte));
        rapport.append(String.format("- Transactions en attente: %d\n", transactionsAttente));
        
        envoyerNotificationEmail("Rapport Financier Hebdomadaire", rapport.toString());
    }

    /**
     * Notification pour dépassement de budget
     */
    @Async
    public void notifierDepassementBudget(Budget budget, BigDecimal montantDepasse) {
        if (!notificationsEnabled) return;
        
        String message = String.format(
                "ALERTE: Le budget '%s' a dépassé son seuil d'alerte.\n\n" +
                "Pourcentage utilisé: %.2f%%\n" +
                "Seuil d'alerte: %.2f%%\n" +
                "Montant dépassé: %.2f €\n\n" +
                "Veuillez prendre les mesures nécessaires.",
                budget.getNom(),
                budget.getPourcentageUtilise(),
                budget.getSeuilAlerte(),
                montantDepasse
        );
        
        envoyerNotificationEmail("Alerte: Dépassement de Budget", message);
    }

    /**
     * Notification pour transaction importante
     */
    @Async
    public void notifierTransactionImportante(Transaction transaction) {
        if (!notificationsEnabled) return;
        
        // Seuil pour transaction importante (configurable)
        BigDecimal seuilImportant = new BigDecimal("10000");
        
        if (transaction.getMontant().compareTo(seuilImportant) >= 0) {
            String message = String.format(
                    "Transaction importante détectée:\n\n" +
                    "Référence: %s\n" +
                    "Type: %s\n" +
                    "Montant: %.2f €\n" +
                    "Description: %s\n" +
                    "Date: %s\n\n" +
                    "Cette transaction nécessite une attention particulière.",
                    transaction.getReference(),
                    transaction.getTypeTransaction(),
                    transaction.getMontant(),
                    transaction.getDescription(),
                    transaction.getDateTransaction()
            );
            
            envoyerNotificationEmail("Transaction Importante", message);
        }
    }

    /**
     * Envoie une notification par email
     */
    @Async
    public void envoyerNotificationEmail(String sujet, String contenu) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailAdmin);
            message.setSubject("[SprintBot Finance] " + sujet);
            message.setText(contenu);
            
            mailSender.send(message);
            log.info("Notification envoyée: {}", sujet);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification '{}': {}", sujet, e.getMessage());
        }
    }

    /**
     * Active/désactive les notifications
     */
    public void configurerNotifications(boolean activer) {
        this.notificationsEnabled = activer;
        log.info("Notifications financières {}", activer ? "activées" : "désactivées");
    }
}
