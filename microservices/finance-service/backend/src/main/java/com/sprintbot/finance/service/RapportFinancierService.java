package com.sprintbot.finance.service;

import com.sprintbot.finance.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour la génération de rapports financiers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RapportFinancierService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final SponsorRepository sponsorRepository;
    private final SalaireRepository salaireRepository;
    private final TransactionService transactionService;
    private final SponsorService sponsorService;
    private final SalaireService salaireService;

    /**
     * Génère un rapport financier global
     */
    public Map<String, Object> genererRapportGlobal(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Génération du rapport financier global pour la période {} - {}", dateDebut, dateFin);
        
        Map<String, Object> rapport = new HashMap<>();
        
        // Données budgétaires
        BigDecimal budgetTotal = budgetRepository.calculerMontantTotalBudgetsActifs();
        BigDecimal budgetUtilise = budgetRepository.calculerMontantUtiliseTotalBudgetsActifs();
        
        rapport.put("budgetTotal", budgetTotal);
        rapport.put("budgetUtilise", budgetUtilise);
        rapport.put("budgetRestant", budgetTotal.subtract(budgetUtilise));
        rapport.put("pourcentageBudgetUtilise", 
                   budgetTotal.compareTo(BigDecimal.ZERO) > 0 ? 
                   budgetUtilise.multiply(BigDecimal.valueOf(100)).divide(budgetTotal, 2, BigDecimal.ROUND_HALF_UP) : 
                   BigDecimal.ZERO);
        
        // Données des transactions
        BigDecimal totalRecettes = transactionService.calculerTotalRecettes(dateDebut, dateFin);
        BigDecimal totalDepenses = transactionService.calculerTotalDepenses(dateDebut, dateFin);
        BigDecimal solde = transactionService.calculerSolde(dateDebut, dateFin);
        
        rapport.put("totalRecettes", totalRecettes);
        rapport.put("totalDepenses", totalDepenses);
        rapport.put("solde", solde);
        
        // Données des sponsors
        BigDecimal sponsorTotal = sponsorService.calculerMontantTotalContratsActifs();
        BigDecimal sponsorVerse = sponsorService.calculerMontantTotalVerse();
        BigDecimal sponsorRestant = sponsorService.calculerMontantTotalRestant();
        
        rapport.put("sponsorTotal", sponsorTotal);
        rapport.put("sponsorVerse", sponsorVerse);
        rapport.put("sponsorRestant", sponsorRestant);
        
        // Données des salaires
        BigDecimal totalSalaires = salaireService.calculerMontantTotalSalaires(dateDebut, dateFin);
        BigDecimal totalCotisations = salaireService.calculerMontantTotalCotisations(dateDebut, dateFin);
        
        rapport.put("totalSalaires", totalSalaires);
        rapport.put("totalCotisations", totalCotisations);
        rapport.put("totalChargesSalariales", totalSalaires.add(totalCotisations));
        
        // Statistiques
        rapport.put("statistiquesBudgets", budgetRepository.getStatistiquesParStatut());
        rapport.put("statistiquesTransactions", transactionRepository.getStatistiquesParCategorie());
        rapport.put("statistiquesSponsors", sponsorRepository.getStatistiquesParTypePartenariat());
        rapport.put("statistiquesSalaires", salaireRepository.getStatistiquesParStatut());
        
        // Alertes
        rapport.put("budgetsAvecAlerte", budgetRepository.findBudgetsAvecAlerte().size());
        rapport.put("sponsorsProchesExpiration", sponsorRepository.findSponsorsProchesExpiration(LocalDate.now().plusDays(30)).size());
        rapport.put("transactionsEnAttente", transactionRepository.countByStatut(com.sprintbot.finance.entity.Transaction.StatutTransaction.EN_ATTENTE));
        rapport.put("salairesEnAttente", salaireRepository.countByStatut(com.sprintbot.finance.entity.Salaire.StatutSalaire.CALCULE));
        
        log.info("Rapport financier global généré avec succès");
        return rapport;
    }

    /**
     * Génère un rapport de trésorerie
     */
    public Map<String, Object> genererRapportTresorerie(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Génération du rapport de trésorerie pour la période {} - {}", dateDebut, dateFin);
        
        Map<String, Object> rapport = new HashMap<>();
        
        // Flux de trésorerie
        BigDecimal recettes = transactionService.calculerTotalRecettes(dateDebut, dateFin);
        BigDecimal depenses = transactionService.calculerTotalDepenses(dateDebut, dateFin);
        BigDecimal fluxNet = recettes.subtract(depenses);
        
        rapport.put("recettes", recettes);
        rapport.put("depenses", depenses);
        rapport.put("fluxNet", fluxNet);
        
        // Détail par catégorie
        rapport.put("statistiquesParCategorie", transactionRepository.getStatistiquesParCategorie());
        rapport.put("statistiquesMensuelles", transactionRepository.getStatistiquesMensuelles());
        
        // Prévisions
        BigDecimal sponsorRestant = sponsorService.calculerMontantTotalRestant();
        BigDecimal salairesAPayer = salaireRepository.calculerMontantTotalSalairesNets(dateDebut, dateFin);
        
        rapport.put("recettesPrevisionnelles", sponsorRestant);
        rapport.put("depensesPrevisionnelles", salairesAPayer);
        rapport.put("soldeProvisionnel", sponsorRestant.subtract(salairesAPayer));
        
        return rapport;
    }

    /**
     * Génère un rapport Excel
     */
    public byte[] genererRapportExcel(LocalDate dateDebut, LocalDate dateFin) throws IOException {
        log.info("Génération du rapport Excel pour la période {} - {}", dateDebut, dateFin);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Feuille de synthèse
            Sheet synthese = workbook.createSheet("Synthèse");
            creerFeuilleSynthese(synthese, dateDebut, dateFin);
            
            // Feuille des budgets
            Sheet budgets = workbook.createSheet("Budgets");
            creerFeuilleBudgets(budgets);
            
            // Feuille des transactions
            Sheet transactions = workbook.createSheet("Transactions");
            creerFeuilleTransactions(transactions, dateDebut, dateFin);
            
            // Feuille des sponsors
            Sheet sponsors = workbook.createSheet("Sponsors");
            creerFeuilleSponsors(sponsors);
            
            // Feuille des salaires
            Sheet salaires = workbook.createSheet("Salaires");
            creerFeuilleSalaires(salaires, dateDebut, dateFin);
            
            // Conversion en bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Rapport Excel généré avec succès");
            return outputStream.toByteArray();
        }
    }

    /**
     * Crée la feuille de synthèse
     */
    private void creerFeuilleSynthese(Sheet sheet, LocalDate dateDebut, LocalDate dateFin) {
        Map<String, Object> rapport = genererRapportGlobal(dateDebut, dateFin);
        
        // En-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("RAPPORT FINANCIER - SYNTHÈSE");
        headerRow.createCell(1).setCellValue("Période: " + dateDebut + " au " + dateFin);
        
        int rowNum = 2;
        
        // Section Budget
        Row budgetHeader = sheet.createRow(rowNum++);
        budgetHeader.createCell(0).setCellValue("BUDGET");
        
        Row budgetTotal = sheet.createRow(rowNum++);
        budgetTotal.createCell(0).setCellValue("Budget Total");
        budgetTotal.createCell(1).setCellValue(((BigDecimal) rapport.get("budgetTotal")).doubleValue());
        
        Row budgetUtilise = sheet.createRow(rowNum++);
        budgetUtilise.createCell(0).setCellValue("Budget Utilisé");
        budgetUtilise.createCell(1).setCellValue(((BigDecimal) rapport.get("budgetUtilise")).doubleValue());
        
        Row budgetRestant = sheet.createRow(rowNum++);
        budgetRestant.createCell(0).setCellValue("Budget Restant");
        budgetRestant.createCell(1).setCellValue(((BigDecimal) rapport.get("budgetRestant")).doubleValue());
        
        rowNum++;
        
        // Section Trésorerie
        Row tresorerieHeader = sheet.createRow(rowNum++);
        tresorerieHeader.createCell(0).setCellValue("TRÉSORERIE");
        
        Row recettes = sheet.createRow(rowNum++);
        recettes.createCell(0).setCellValue("Total Recettes");
        recettes.createCell(1).setCellValue(((BigDecimal) rapport.get("totalRecettes")).doubleValue());
        
        Row depenses = sheet.createRow(rowNum++);
        depenses.createCell(0).setCellValue("Total Dépenses");
        depenses.createCell(1).setCellValue(((BigDecimal) rapport.get("totalDepenses")).doubleValue());
        
        Row solde = sheet.createRow(rowNum++);
        solde.createCell(0).setCellValue("Solde");
        solde.createCell(1).setCellValue(((BigDecimal) rapport.get("solde")).doubleValue());
        
        // Auto-ajustement des colonnes
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * Crée la feuille des budgets
     */
    private void creerFeuilleBudgets(Sheet sheet) {
        // En-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nom");
        headerRow.createCell(1).setCellValue("Montant Total");
        headerRow.createCell(2).setCellValue("Montant Utilisé");
        headerRow.createCell(3).setCellValue("Montant Restant");
        headerRow.createCell(4).setCellValue("% Utilisé");
        headerRow.createCell(5).setCellValue("Statut");
        
        // Données
        List<com.sprintbot.finance.entity.Budget> budgets = budgetRepository.findByStatutOrderByDateDebutDesc(
                com.sprintbot.finance.entity.Budget.StatutBudget.ACTIF);
        
        int rowNum = 1;
        for (com.sprintbot.finance.entity.Budget budget : budgets) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(budget.getNom());
            row.createCell(1).setCellValue(budget.getMontantTotal().doubleValue());
            row.createCell(2).setCellValue(budget.getMontantUtilise().doubleValue());
            row.createCell(3).setCellValue(budget.getMontantRestant().doubleValue());
            row.createCell(4).setCellValue(budget.getPourcentageUtilise().doubleValue());
            row.createCell(5).setCellValue(budget.getStatut().toString());
        }
        
        // Auto-ajustement des colonnes
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Crée la feuille des transactions
     */
    private void creerFeuilleTransactions(Sheet sheet, LocalDate dateDebut, LocalDate dateFin) {
        // En-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("Référence");
        headerRow.createCell(2).setCellValue("Description");
        headerRow.createCell(3).setCellValue("Type");
        headerRow.createCell(4).setCellValue("Montant");
        headerRow.createCell(5).setCellValue("Statut");
        
        // Données
        List<com.sprintbot.finance.entity.Transaction> transactions = 
                transactionRepository.findTransactionsValideesPourPeriode(dateDebut, dateFin);
        
        int rowNum = 1;
        for (com.sprintbot.finance.entity.Transaction transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getDateTransaction().format(DateTimeFormatter.ISO_LOCAL_DATE));
            row.createCell(1).setCellValue(transaction.getReference());
            row.createCell(2).setCellValue(transaction.getDescription());
            row.createCell(3).setCellValue(transaction.getTypeTransaction().toString());
            row.createCell(4).setCellValue(transaction.getMontant().doubleValue());
            row.createCell(5).setCellValue(transaction.getStatut().toString());
        }
        
        // Auto-ajustement des colonnes
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Crée la feuille des sponsors
     */
    private void creerFeuilleSponsors(Sheet sheet) {
        // En-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nom");
        headerRow.createCell(1).setCellValue("Type Partenariat");
        headerRow.createCell(2).setCellValue("Montant Contrat");
        headerRow.createCell(3).setCellValue("Montant Versé");
        headerRow.createCell(4).setCellValue("Montant Restant");
        headerRow.createCell(5).setCellValue("Statut");
        
        // Données
        List<com.sprintbot.finance.entity.Sponsor> sponsors = sponsorRepository.findSponsorsActifs();
        
        int rowNum = 1;
        for (com.sprintbot.finance.entity.Sponsor sponsor : sponsors) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sponsor.getNom());
            row.createCell(1).setCellValue(sponsor.getTypePartenariat().toString());
            row.createCell(2).setCellValue(sponsor.getMontantContrat().doubleValue());
            row.createCell(3).setCellValue(sponsor.getMontantVerse().doubleValue());
            row.createCell(4).setCellValue(sponsor.getMontantRestant().doubleValue());
            row.createCell(5).setCellValue(sponsor.getStatut().toString());
        }
        
        // Auto-ajustement des colonnes
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Crée la feuille des salaires
     */
    private void creerFeuilleSalaires(Sheet sheet, LocalDate dateDebut, LocalDate dateFin) {
        // En-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Employé ID");
        headerRow.createCell(1).setCellValue("Période");
        headerRow.createCell(2).setCellValue("Salaire Brut");
        headerRow.createCell(3).setCellValue("Cotisations");
        headerRow.createCell(4).setCellValue("Impôt");
        headerRow.createCell(5).setCellValue("Salaire Net");
        headerRow.createCell(6).setCellValue("Statut");
        
        // Données
        List<com.sprintbot.finance.entity.Salaire> salaires = 
                salaireRepository.findSalairesPourPeriode(dateDebut, dateFin);
        
        int rowNum = 1;
        for (com.sprintbot.finance.entity.Salaire salaire : salaires) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(salaire.getEmployeId());
            row.createCell(1).setCellValue(salaire.getPeriode().format(DateTimeFormatter.ISO_LOCAL_DATE));
            row.createCell(2).setCellValue(salaire.getSalaireBrut().doubleValue());
            row.createCell(3).setCellValue(salaire.getCotisationsSociales().doubleValue());
            row.createCell(4).setCellValue(salaire.getImpot().doubleValue());
            row.createCell(5).setCellValue(salaire.getSalaireNet().doubleValue());
            row.createCell(6).setCellValue(salaire.getStatut().toString());
        }
        
        // Auto-ajustement des colonnes
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
