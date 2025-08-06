package com.sprintbot.finance.controller;

import com.sprintbot.finance.entity.Sponsor;
import com.sprintbot.finance.entity.PaiementSponsor;
import com.sprintbot.finance.service.SponsorService;
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
 * Contrôleur REST pour la gestion des sponsors
 */
@RestController
@RequestMapping("/api/sponsors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sponsor", description = "API de gestion des sponsors et partenariats")
@CrossOrigin(origins = "*")
public class SponsorController {

    private final SponsorService sponsorService;

    /**
     * Crée un nouveau sponsor
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Créer un sponsor", description = "Crée un nouveau sponsor avec contrat de partenariat")
    public ResponseEntity<Sponsor> creerSponsor(@Valid @RequestBody Sponsor sponsor) {
        log.info("Création d'un nouveau sponsor: {}", sponsor.getNom());
        Sponsor sponsorCree = sponsorService.creerSponsor(sponsor);
        return ResponseEntity.status(HttpStatus.CREATED).body(sponsorCree);
    }

    /**
     * Met à jour un sponsor
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Mettre à jour un sponsor", description = "Met à jour les informations d'un sponsor")
    public ResponseEntity<Sponsor> mettreAJourSponsor(
            @Parameter(description = "ID du sponsor") @PathVariable Long id,
            @Valid @RequestBody Sponsor sponsor) {
        log.info("Mise à jour du sponsor ID: {}", id);
        Sponsor sponsorMisAJour = sponsorService.mettreAJourSponsor(id, sponsor);
        return ResponseEntity.ok(sponsorMisAJour);
    }

    /**
     * Enregistre un paiement de sponsor
     */
    @PostMapping("/{id}/paiements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Enregistrer un paiement", description = "Enregistre un nouveau paiement de sponsor")
    public ResponseEntity<PaiementSponsor> enregistrerPaiement(
            @Parameter(description = "ID du sponsor") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        BigDecimal montant = new BigDecimal(request.get("montant").toString());
        String modePaiement = (String) request.get("modePaiement");
        String referencePaiement = (String) request.get("referencePaiement");
        String commentaires = (String) request.get("commentaires");
        
        log.info("Enregistrement d'un paiement de {} pour le sponsor ID: {}", montant, id);
        PaiementSponsor paiement = sponsorService.enregistrerPaiement(id, montant, modePaiement, 
                                                                      referencePaiement, commentaires);
        return ResponseEntity.status(HttpStatus.CREATED).body(paiement);
    }

    /**
     * Renouvelle un contrat de sponsor
     */
    @PostMapping("/{id}/renouveler")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Renouveler un contrat", description = "Renouvelle automatiquement un contrat de sponsor")
    public ResponseEntity<Sponsor> renouvellerContrat(@Parameter(description = "ID du sponsor") @PathVariable Long id) {
        log.info("Renouvellement du contrat pour le sponsor ID: {}", id);
        Sponsor sponsor = sponsorService.renouvellerContrat(id);
        return ResponseEntity.ok(sponsor);
    }

    /**
     * Suspend un sponsor
     */
    @PostMapping("/{id}/suspendre")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Suspendre un sponsor", description = "Suspend temporairement un sponsor")
    public ResponseEntity<Sponsor> suspendreSponsor(
            @Parameter(description = "ID du sponsor") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String motifSuspension = request.get("motifSuspension");
        
        log.info("Suspension du sponsor ID: {}", id);
        Sponsor sponsor = sponsorService.suspendreSponsor(id, motifSuspension);
        return ResponseEntity.ok(sponsor);
    }

    /**
     * Réactive un sponsor
     */
    @PostMapping("/{id}/reactiver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Réactiver un sponsor", description = "Réactive un sponsor suspendu")
    public ResponseEntity<Sponsor> reactiverSponsor(@Parameter(description = "ID du sponsor") @PathVariable Long id) {
        log.info("Réactivation du sponsor ID: {}", id);
        Sponsor sponsor = sponsorService.reactiverSponsor(id);
        return ResponseEntity.ok(sponsor);
    }

    /**
     * Obtient tous les sponsors actifs
     */
    @GetMapping("/actifs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir les sponsors actifs", description = "Récupère la liste de tous les sponsors actifs")
    public ResponseEntity<List<Sponsor>> obtenirSponsorsActifs() {
        List<Sponsor> sponsors = sponsorService.obtenirSponsorsActifs();
        return ResponseEntity.ok(sponsors);
    }

    /**
     * Obtient les sponsors proches de l'expiration
     */
    @GetMapping("/expiration")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les sponsors proches d'expiration", description = "Récupère les sponsors dont le contrat expire bientôt")
    public ResponseEntity<List<Sponsor>> obtenirSponsorsProchesExpiration(
            @Parameter(description = "Nombre de jours d'avance") @RequestParam(defaultValue = "30") int joursAvance) {
        List<Sponsor> sponsors = sponsorService.obtenirSponsorsProchesExpiration(joursAvance);
        return ResponseEntity.ok(sponsors);
    }

    /**
     * Obtient les sponsors avec paiements en retard
     */
    @GetMapping("/retards")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les sponsors en retard", description = "Récupère les sponsors avec des paiements en retard")
    public ResponseEntity<List<Sponsor>> obtenirSponsorsAvecRetard() {
        List<Sponsor> sponsors = sponsorService.obtenirSponsorsAvecRetard();
        return ResponseEntity.ok(sponsors);
    }

    /**
     * Obtient un sponsor par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir un sponsor par ID", description = "Récupère les détails d'un sponsor spécifique")
    public ResponseEntity<Sponsor> obtenirSponsorParId(@Parameter(description = "ID du sponsor") @PathVariable Long id) {
        return sponsorService.obtenirSponsorParId(id)
                .map(sponsor -> ResponseEntity.ok(sponsor))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient les paiements d'un sponsor
     */
    @GetMapping("/{id}/paiements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Obtenir les paiements d'un sponsor", description = "Récupère l'historique des paiements d'un sponsor")
    public ResponseEntity<List<PaiementSponsor>> obtenirPaiementsSponsor(@Parameter(description = "ID du sponsor") @PathVariable Long id) {
        List<PaiementSponsor> paiements = sponsorService.obtenirPaiementsSponsor(id);
        return ResponseEntity.ok(paiements);
    }

    /**
     * Calcule le montant total des contrats actifs
     */
    @GetMapping("/montant-total-actifs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Calculer le montant total des contrats actifs", description = "Calcule le montant total de tous les contrats actifs")
    public ResponseEntity<BigDecimal> calculerMontantTotalContratsActifs() {
        BigDecimal montantTotal = sponsorService.calculerMontantTotalContratsActifs();
        return ResponseEntity.ok(montantTotal);
    }

    /**
     * Calcule le montant total versé
     */
    @GetMapping("/montant-total-verse")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Calculer le montant total versé", description = "Calcule le montant total versé par tous les sponsors")
    public ResponseEntity<BigDecimal> calculerMontantTotalVerse() {
        BigDecimal montantTotal = sponsorService.calculerMontantTotalVerse();
        return ResponseEntity.ok(montantTotal);
    }

    /**
     * Calcule le montant total restant
     */
    @GetMapping("/montant-total-restant")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Calculer le montant total restant", description = "Calcule le montant total restant à verser")
    public ResponseEntity<BigDecimal> calculerMontantTotalRestant() {
        BigDecimal montantTotal = sponsorService.calculerMontantTotalRestant();
        return ResponseEntity.ok(montantTotal);
    }

    /**
     * Recherche avancée de sponsors
     */
    @GetMapping("/recherche")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('FINANCE_USER')")
    @Operation(summary = "Recherche avancée de sponsors", description = "Effectue une recherche avancée avec filtres multiples")
    public ResponseEntity<Page<Sponsor>> rechercherSponsors(
            @Parameter(description = "Nom du sponsor") @RequestParam(required = false) String nom,
            @Parameter(description = "Type de partenariat") @RequestParam(required = false) Sponsor.TypePartenariat typePartenariat,
            @Parameter(description = "Statut du sponsor") @RequestParam(required = false) Sponsor.StatutSponsor statut,
            @Parameter(description = "Date de début minimum") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin maximum") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @Parameter(description = "Montant minimum") @RequestParam(required = false) BigDecimal montantMin,
            @Parameter(description = "Montant maximum") @RequestParam(required = false) BigDecimal montantMax,
            Pageable pageable) {
        
        Page<Sponsor> sponsors = sponsorService.rechercherSponsors(nom, typePartenariat, statut, 
                                                                  dateDebut, dateFin, montantMin, montantMax, pageable);
        return ResponseEntity.ok(sponsors);
    }

    /**
     * Obtient les statistiques des sponsors
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Obtenir les statistiques des sponsors", description = "Récupère les statistiques globales des sponsors")
    public ResponseEntity<List<Object[]>> obtenirStatistiquesSponsors() {
        List<Object[]> statistiques = sponsorService.obtenirStatistiquesSponsors();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Traite les renouvellements automatiques
     */
    @PostMapping("/traiter-renouvellements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    @Operation(summary = "Traiter les renouvellements", description = "Lance le traitement des renouvellements automatiques de contrats")
    public ResponseEntity<String> traiterRenouvellements() {
        sponsorService.traiterRenouvellements();
        return ResponseEntity.ok("Traitement des renouvellements de contrats lancé avec succès");
    }

    /**
     * Supprime un sponsor
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un sponsor", description = "Supprime définitivement un sponsor")
    public ResponseEntity<Void> supprimerSponsor(@Parameter(description = "ID du sponsor") @PathVariable Long id) {
        log.info("Suppression du sponsor ID: {}", id);
        sponsorService.supprimerSponsor(id);
        return ResponseEntity.noContent().build();
    }
}
