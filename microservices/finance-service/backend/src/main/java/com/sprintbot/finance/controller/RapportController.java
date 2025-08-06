package com.sprintbot.finance.controller;

import com.sprintbot.finance.service.RapportFinancierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Contrôleur REST pour la génération de rapports financiers
 */
@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rapport", description = "API de génération de rapports financiers")
@CrossOrigin(origins = "*")
public class RapportController {

    private final RapportFinancierService rapportFinancierService;

    /**
     * Génère un rapport financier global
     */
    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport financier global", description = "Génère un rapport financier complet avec toutes les données")
    public ResponseEntity<Map<String, Object>> genererRapportGlobal(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        log.info("Génération du rapport financier global pour la période {} - {}", dateDebut, dateFin);
        Map<String, Object> rapport = rapportFinancierService.genererRapportGlobal(dateDebut, dateFin);
        return ResponseEntity.ok(rapport);
    }

    /**
     * Génère un rapport de trésorerie
     */
    @GetMapping("/tresorerie")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport de trésorerie", description = "Génère un rapport détaillé de la trésorerie et des flux financiers")
    public ResponseEntity<Map<String, Object>> genererRapportTresorerie(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        log.info("Génération du rapport de trésorerie pour la période {} - {}", dateDebut, dateFin);
        Map<String, Object> rapport = rapportFinancierService.genererRapportTresorerie(dateDebut, dateFin);
        return ResponseEntity.ok(rapport);
    }

    /**
     * Génère et télécharge un rapport Excel
     */
    @GetMapping("/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport Excel", description = "Génère et télécharge un rapport financier complet au format Excel")
    public ResponseEntity<byte[]> genererRapportExcel(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        try {
            log.info("Génération du rapport Excel pour la période {} - {}", dateDebut, dateFin);
            byte[] rapportExcel = rapportFinancierService.genererRapportExcel(dateDebut, dateFin);
            
            String filename = String.format("rapport_financier_%s_%s.xlsx", 
                    dateDebut.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    dateFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(rapportExcel.length);
            
            return new ResponseEntity<>(rapportExcel, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("Erreur lors de la génération du rapport Excel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Génère un rapport de synthèse mensuel
     */
    @GetMapping("/mensuel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport mensuel", description = "Génère un rapport de synthèse pour un mois donné")
    public ResponseEntity<Map<String, Object>> genererRapportMensuel(
            @Parameter(description = "Année") @RequestParam int annee,
            @Parameter(description = "Mois (1-12)") @RequestParam int mois) {
        
        LocalDate dateDebut = LocalDate.of(annee, mois, 1);
        LocalDate dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
        
        log.info("Génération du rapport mensuel pour {}/{}", mois, annee);
        Map<String, Object> rapport = rapportFinancierService.genererRapportGlobal(dateDebut, dateFin);
        
        // Ajout d'informations spécifiques au rapport mensuel
        rapport.put("periode", String.format("%02d/%d", mois, annee));
        rapport.put("dateDebut", dateDebut);
        rapport.put("dateFin", dateFin);
        rapport.put("typeRapport", "MENSUEL");
        
        return ResponseEntity.ok(rapport);
    }

    /**
     * Génère un rapport annuel
     */
    @GetMapping("/annuel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport annuel", description = "Génère un rapport de synthèse pour une année donnée")
    public ResponseEntity<Map<String, Object>> genererRapportAnnuel(
            @Parameter(description = "Année") @RequestParam int annee) {
        
        LocalDate dateDebut = LocalDate.of(annee, 1, 1);
        LocalDate dateFin = LocalDate.of(annee, 12, 31);
        
        log.info("Génération du rapport annuel pour {}", annee);
        Map<String, Object> rapport = rapportFinancierService.genererRapportGlobal(dateDebut, dateFin);
        
        // Ajout d'informations spécifiques au rapport annuel
        rapport.put("periode", String.valueOf(annee));
        rapport.put("dateDebut", dateDebut);
        rapport.put("dateFin", dateFin);
        rapport.put("typeRapport", "ANNUEL");
        
        return ResponseEntity.ok(rapport);
    }

    /**
     * Génère un rapport trimestriel
     */
    @GetMapping("/trimestriel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport trimestriel", description = "Génère un rapport de synthèse pour un trimestre donné")
    public ResponseEntity<Map<String, Object>> genererRapportTrimestriel(
            @Parameter(description = "Année") @RequestParam int annee,
            @Parameter(description = "Trimestre (1-4)") @RequestParam int trimestre) {
        
        if (trimestre < 1 || trimestre > 4) {
            return ResponseEntity.badRequest().build();
        }
        
        int moisDebut = (trimestre - 1) * 3 + 1;
        LocalDate dateDebut = LocalDate.of(annee, moisDebut, 1);
        LocalDate dateFin = dateDebut.plusMonths(2).withDayOfMonth(dateDebut.plusMonths(2).lengthOfMonth());
        
        log.info("Génération du rapport trimestriel Q{} {} pour la période {} - {}", trimestre, annee, dateDebut, dateFin);
        Map<String, Object> rapport = rapportFinancierService.genererRapportGlobal(dateDebut, dateFin);
        
        // Ajout d'informations spécifiques au rapport trimestriel
        rapport.put("periode", String.format("Q%d %d", trimestre, annee));
        rapport.put("dateDebut", dateDebut);
        rapport.put("dateFin", dateFin);
        rapport.put("typeRapport", "TRIMESTRIEL");
        rapport.put("trimestre", trimestre);
        rapport.put("annee", annee);
        
        return ResponseEntity.ok(rapport);
    }

    /**
     * Génère un rapport personnalisé avec période libre
     */
    @GetMapping("/personnalise")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport personnalisé", description = "Génère un rapport pour une période personnalisée")
    public ResponseEntity<Map<String, Object>> genererRapportPersonnalise(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @Parameter(description = "Nom du rapport") @RequestParam(required = false, defaultValue = "Rapport Personnalisé") String nomRapport) {
        
        if (dateDebut.isAfter(dateFin)) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Génération du rapport personnalisé '{}' pour la période {} - {}", nomRapport, dateDebut, dateFin);
        Map<String, Object> rapport = rapportFinancierService.genererRapportGlobal(dateDebut, dateFin);
        
        // Ajout d'informations spécifiques au rapport personnalisé
        rapport.put("nomRapport", nomRapport);
        rapport.put("dateDebut", dateDebut);
        rapport.put("dateFin", dateFin);
        rapport.put("typeRapport", "PERSONNALISE");
        rapport.put("dureeJours", java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1);
        
        return ResponseEntity.ok(rapport);
    }

    /**
     * Génère un rapport de comparaison entre deux périodes
     */
    @GetMapping("/comparaison")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Rapport de comparaison", description = "Compare les données financières entre deux périodes")
    public ResponseEntity<Map<String, Object>> genererRapportComparaison(
            @Parameter(description = "Date de début période 1") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut1,
            @Parameter(description = "Date de fin période 1") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin1,
            @Parameter(description = "Date de début période 2") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut2,
            @Parameter(description = "Date de fin période 2") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin2) {
        
        log.info("Génération du rapport de comparaison entre {} - {} et {} - {}", 
                dateDebut1, dateFin1, dateDebut2, dateFin2);
        
        Map<String, Object> rapport1 = rapportFinancierService.genererRapportGlobal(dateDebut1, dateFin1);
        Map<String, Object> rapport2 = rapportFinancierService.genererRapportGlobal(dateDebut2, dateFin2);
        
        Map<String, Object> rapportComparaison = Map.of(
                "periode1", Map.of(
                        "dateDebut", dateDebut1,
                        "dateFin", dateFin1,
                        "donnees", rapport1
                ),
                "periode2", Map.of(
                        "dateDebut", dateDebut2,
                        "dateFin", dateFin2,
                        "donnees", rapport2
                ),
                "typeRapport", "COMPARAISON"
        );
        
        return ResponseEntity.ok(rapportComparaison);
    }

    /**
     * Génère un rapport de tableau de bord (dashboard)
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Rapport dashboard", description = "Génère un rapport synthétique pour le tableau de bord")
    public ResponseEntity<Map<String, Object>> genererRapportDashboard() {
        LocalDate dateFin = LocalDate.now();
        LocalDate dateDebut = dateFin.minusMonths(1); // Dernier mois
        
        log.info("Génération du rapport dashboard pour la période {} - {}", dateDebut, dateFin);
        Map<String, Object> rapport = rapportFinancierService.genererRapportGlobal(dateDebut, dateFin);
        
        // Ajout d'informations spécifiques au dashboard
        rapport.put("typeRapport", "DASHBOARD");
        rapport.put("dateGeneration", LocalDate.now());
        rapport.put("periodeAnalysee", "30 derniers jours");
        
        return ResponseEntity.ok(rapport);
    }
}
