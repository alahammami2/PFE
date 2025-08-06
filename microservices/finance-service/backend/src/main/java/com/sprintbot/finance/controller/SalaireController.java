package com.sprintbot.finance.controller;

import com.sprintbot.finance.entity.Salaire;
import com.sprintbot.finance.entity.ElementSalaire;
import com.sprintbot.finance.service.SalaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Contrôleur REST pour la gestion des salaires
 */
@RestController
@RequestMapping("/api/salaires")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Salaire", description = "API de gestion des salaires et paie")
@CrossOrigin(origins = "*")
public class SalaireController {

    private final SalaireService salaireService;

    /**
     * Calcule un salaire pour un employé
     */
    @PostMapping("/calculer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Calculer un salaire", description = "Calcule le salaire d'un employé pour une période donnée")
    public ResponseEntity<Salaire> calculerSalaire(@RequestBody Map<String, Object> request) {
        Long employeId = Long.valueOf(request.get("employeId").toString());
        LocalDate periode = LocalDate.parse(request.get("periode").toString());
        BigDecimal salaireBrut = new BigDecimal(request.get("salaireBrut").toString());
        
        BigDecimal primes = request.get("primes") != null ? new BigDecimal(request.get("primes").toString()) : null;
        BigDecimal bonus = request.get("bonus") != null ? new BigDecimal(request.get("bonus").toString()) : null;
        BigDecimal deductions = request.get("deductions") != null ? new BigDecimal(request.get("deductions").toString()) : null;
        Integer heuresSupplementaires = request.get("heuresSupplementaires") != null ? 
                Integer.valueOf(request.get("heuresSupplementaires").toString()) : null;
        Integer joursAbsence = request.get("joursAbsence") != null ? 
                Integer.valueOf(request.get("joursAbsence").toString()) : null;
        
        log.info("Calcul du salaire pour l'employé {} pour la période {}", employeId, periode);
        Salaire salaire = salaireService.calculerSalaire(employeId, periode, salaireBrut, primes, bonus, 
                                                        deductions, heuresSupplementaires, joursAbsence);
        return ResponseEntity.status(HttpStatus.CREATED).body(salaire);
    }

    /**
     * Valide un salaire calculé
     */
    @PostMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Valider un salaire", description = "Valide un salaire calculé")
    public ResponseEntity<Salaire> validerSalaire(
            @Parameter(description = "ID du salaire") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long validateurId = Long.valueOf(request.get("validateurId").toString());
        
        log.info("Validation du salaire ID: {} par utilisateur: {}", id, validateurId);
        Salaire salaire = salaireService.validerSalaire(id, validateurId);
        return ResponseEntity.ok(salaire);
    }

    /**
     * Marque un salaire comme payé
     */
    @PostMapping("/{id}/payer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Marquer comme payé", description = "Marque un salaire comme payé")
    public ResponseEntity<Salaire> marquerCommePayé(
            @Parameter(description = "ID du salaire") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String modePaiement = request.get("modePaiement");
        String referencePaiement = request.get("referencePaiement");
        
        log.info("Marquage du salaire ID: {} comme payé", id);
        Salaire salaire = salaireService.marquerSalaireCommePayé(id, modePaiement, referencePaiement);
        return ResponseEntity.ok(salaire);
    }

    /**
     * Annule un salaire
     */
    @PostMapping("/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Annuler un salaire", description = "Annule un salaire calculé ou validé")
    public ResponseEntity<Salaire> annulerSalaire(
            @Parameter(description = "ID du salaire") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String motifAnnulation = request.get("motifAnnulation");
        
        log.info("Annulation du salaire ID: {}", id);
        Salaire salaire = salaireService.annulerSalaire(id, motifAnnulation);
        return ResponseEntity.ok(salaire);
    }

    /**
     * Ajoute un élément de salaire
     */
    @PostMapping("/{id}/elements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Ajouter un élément de salaire", description = "Ajoute un élément spécifique au salaire")
    public ResponseEntity<ElementSalaire> ajouterElementSalaire(
            @Parameter(description = "ID du salaire") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String libelle = (String) request.get("libelle");
        ElementSalaire.TypeElement typeElement = ElementSalaire.TypeElement.valueOf(request.get("typeElement").toString());
        BigDecimal montant = new BigDecimal(request.get("montant").toString());
        BigDecimal quantite = request.get("quantite") != null ? new BigDecimal(request.get("quantite").toString()) : null;
        BigDecimal taux = request.get("taux") != null ? new BigDecimal(request.get("taux").toString()) : null;
        boolean obligatoire = Boolean.parseBoolean(request.get("obligatoire").toString());
        boolean imposable = Boolean.parseBoolean(request.get("imposable").toString());
        boolean cotisable = Boolean.parseBoolean(request.get("cotisable").toString());
        
        log.info("Ajout d'un élément de salaire: {} pour le salaire ID: {}", libelle, id);
        ElementSalaire element = salaireService.ajouterElementSalaire(id, libelle, typeElement, montant, 
                                                                     quantite, taux, obligatoire, imposable, cotisable);
        return ResponseEntity.status(HttpStatus.CREATED).body(element);
    }

    /**
     * Obtient les salaires en attente de validation
     */
    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Obtenir les salaires en attente", description = "Récupère tous les salaires en attente de validation")
    public ResponseEntity<List<Salaire>> obtenirSalairesEnAttenteValidation() {
        List<Salaire> salaires = salaireService.obtenirSalairesEnAttenteValidation();
        return ResponseEntity.ok(salaires);
    }

    /**
     * Obtient les salaires validés mais non payés
     */
    @GetMapping("/valides")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les salaires validés", description = "Récupère tous les salaires validés mais non payés")
    public ResponseEntity<List<Salaire>> obtenirSalairesValides() {
        List<Salaire> salaires = salaireService.obtenirSalairesValides();
        return ResponseEntity.ok(salaires);
    }

    /**
     * Obtient les salaires d'un employé
     */
    @GetMapping("/employe/{employeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Obtenir les salaires d'un employé", description = "Récupère l'historique des salaires d'un employé")
    public ResponseEntity<List<Salaire>> obtenirSalairesEmploye(@Parameter(description = "ID de l'employé") @PathVariable Long employeId) {
        List<Salaire> salaires = salaireService.obtenirSalairesEmploye(employeId);
        return ResponseEntity.ok(salaires);
    }

    /**
     * Obtient le salaire d'un employé pour une période
     */
    @GetMapping("/employe/{employeId}/periode/{periode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Obtenir le salaire pour une période", description = "Récupère le salaire d'un employé pour une période spécifique")
    public ResponseEntity<Salaire> obtenirSalaireEmployePeriode(
            @Parameter(description = "ID de l'employé") @PathVariable Long employeId,
            @Parameter(description = "Période (YYYY-MM-DD)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periode) {
        return salaireService.obtenirSalaireEmployePeriode(employeId, periode)
                .map(salaire -> ResponseEntity.ok(salaire))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Calcule le montant total des salaires pour une période
     */
    @GetMapping("/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Calculer le total des salaires", description = "Calcule le montant total des salaires pour une période")
    public ResponseEntity<BigDecimal> calculerMontantTotalSalaires(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        BigDecimal montantTotal = salaireService.calculerMontantTotalSalaires(dateDebut, dateFin);
        return ResponseEntity.ok(montantTotal);
    }

    /**
     * Calcule le montant total des cotisations pour une période
     */
    @GetMapping("/cotisations/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Calculer le total des cotisations", description = "Calcule le montant total des cotisations pour une période")
    public ResponseEntity<BigDecimal> calculerMontantTotalCotisations(
            @Parameter(description = "Date de début") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        BigDecimal montantTotal = salaireService.calculerMontantTotalCotisations(dateDebut, dateFin);
        return ResponseEntity.ok(montantTotal);
    }

    /**
     * Obtient un salaire par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Obtenir un salaire par ID", description = "Récupère les détails d'un salaire spécifique")
    public ResponseEntity<Salaire> obtenirSalaireParId(@Parameter(description = "ID du salaire") @PathVariable Long id) {
        return salaireService.obtenirSalaireParId(id)
                .map(salaire -> ResponseEntity.ok(salaire))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient les éléments d'un salaire
     */
    @GetMapping("/{id}/elements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Obtenir les éléments d'un salaire", description = "Récupère tous les éléments détaillés d'un salaire")
    public ResponseEntity<List<ElementSalaire>> obtenirElementsSalaire(@Parameter(description = "ID du salaire") @PathVariable Long id) {
        List<ElementSalaire> elements = salaireService.obtenirElementsSalaire(id);
        return ResponseEntity.ok(elements);
    }

    /**
     * Recherche avancée de salaires
     */
    @GetMapping("/recherche")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('HR_MANAGER')")
    @Operation(summary = "Recherche avancée de salaires", description = "Effectue une recherche avancée avec filtres multiples")
    public ResponseEntity<Page<Salaire>> rechercherSalaires(
            @Parameter(description = "ID de l'employé") @RequestParam(required = false) Long employeId,
            @Parameter(description = "Statut du salaire") @RequestParam(required = false) Salaire.StatutSalaire statut,
            @Parameter(description = "Période de début") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeDebut,
            @Parameter(description = "Période de fin") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeFin,
            @Parameter(description = "Salaire minimum") @RequestParam(required = false) BigDecimal salaireMin,
            @Parameter(description = "Salaire maximum") @RequestParam(required = false) BigDecimal salaireMax,
            Pageable pageable) {
        
        Page<Salaire> salaires = salaireService.rechercherSalaires(employeId, statut, periodeDebut, periodeFin, 
                                                                  salaireMin, salaireMax, pageable);
        return ResponseEntity.ok(salaires);
    }

    /**
     * Obtient les statistiques des salaires
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les statistiques des salaires", description = "Récupère les statistiques globales des salaires")
    public ResponseEntity<List<Object[]>> obtenirStatistiquesSalaires() {
        List<Object[]> statistiques = salaireService.obtenirStatistiquesSalaires();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Obtient les statistiques mensuelles
     */
    @GetMapping("/statistiques/mensuelles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les statistiques mensuelles", description = "Récupère les statistiques mensuelles des salaires")
    public ResponseEntity<List<Object[]>> obtenirStatistiquesMensuelles() {
        List<Object[]> statistiques = salaireService.obtenirStatistiquesMensuelles();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Traite la paie mensuelle
     */
    @PostMapping("/traiter-paie-mensuelle")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Traiter la paie mensuelle", description = "Lance le traitement automatique de la paie mensuelle")
    public ResponseEntity<String> traiterPaieMensuelle(
            @Parameter(description = "Période (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periode) {
        salaireService.traiterPaieMensuelle(periode);
        return ResponseEntity.ok("Traitement de la paie mensuelle lancé avec succès pour la période: " + periode);
    }

    /**
     * Supprime un salaire
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un salaire", description = "Supprime définitivement un salaire")
    public ResponseEntity<Void> supprimerSalaire(@Parameter(description = "ID du salaire") @PathVariable Long id) {
        log.info("Suppression du salaire ID: {}", id);
        salaireService.supprimerSalaire(id);
        return ResponseEntity.noContent().build();
    }
}
