package com.sprintbot.planningperformance.controller;

import com.sprintbot.planningperformance.entity.Performance;
import com.sprintbot.planningperformance.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/performances")
@CrossOrigin(origins = "*")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    @PostMapping
    public ResponseEntity<Performance> creerPerformance(@Valid @RequestBody Performance performance) {
        try {
            Performance nouvellePerformance = performanceService.creerPerformance(performance);
            return new ResponseEntity<>(nouvellePerformance, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformance(@PathVariable Long id) {
        Optional<Performance> performance = performanceService.getPerformanceById(id);
        return performance.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/entrainement/{entrainementId}/joueur/{joueurId}")
    public ResponseEntity<Performance> getPerformanceSpecifique(
            @PathVariable Long entrainementId,
            @PathVariable Long joueurId) {
        try {
            Optional<Performance> performance = performanceService.getPerformance(entrainementId, joueurId);
            return performance.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                             .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Performance> modifierPerformance(@PathVariable Long id, 
                                                          @Valid @RequestBody Performance performance) {
        try {
            Performance performanceModifiee = performanceService.modifierPerformance(id, performance);
            return new ResponseEntity<>(performanceModifiee, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/auto-evaluation")
    public ResponseEntity<Performance> ajouterAutoEvaluation(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId,
            @RequestParam BigDecimal autoEvaluation,
            @RequestBody(required = false) String commentaireJoueur) {
        try {
            Performance performance = performanceService.ajouterAutoEvaluation(
                entrainementId, joueurId, autoEvaluation, commentaireJoueur);
            return new ResponseEntity<>(performance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPerformance(@PathVariable Long id) {
        try {
            performanceService.supprimerPerformance(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @GetMapping("/joueur/{joueurId}")
    public ResponseEntity<List<Performance>> getPerformancesJoueur(@PathVariable Long joueurId) {
        try {
            List<Performance> performances = performanceService.getPerformancesJoueur(joueurId);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entrainement/{entrainementId}")
    public ResponseEntity<List<Performance>> getPerformancesEntrainement(@PathVariable Long entrainementId) {
        try {
            List<Performance> performances = performanceService.getPerformancesEntrainement(entrainementId);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/periode")
    public ResponseEntity<List<Performance>> getPerformancesJoueurPeriode(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Performance> performances = performanceService.getPerformancesJoueurPeriode(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/meilleures")
    public ResponseEntity<List<Performance>> getMeilleuresPerformances(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Performance> performances = performanceService.getMeilleuresPerformances(joueurId, limite);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/recentes")
    public ResponseEntity<List<Performance>> getPerformancesRecentes(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Performance> performances = performanceService.getPerformancesRecentes(joueurId, limite);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/objectifs-atteints")
    public ResponseEntity<List<Performance>> getPerformancesAvecObjectifsAtteints(@PathVariable Long joueurId) {
        try {
            List<Performance> performances = performanceService.getPerformancesAvecObjectifsAtteints(joueurId);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Statistiques et analyses
    // =====================================================

    @GetMapping("/moyenne/joueur/{joueurId}")
    public ResponseEntity<BigDecimal> getMoyennePerformanceJoueur(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            BigDecimal moyenne = performanceService.getMoyennePerformanceJoueur(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(moyenne, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/evolution/joueur/{joueurId}")
    public ResponseEntity<List<Object[]>> getEvolutionPerformanceJoueur(@PathVariable Long joueurId) {
        try {
            List<Object[]> evolution = performanceService.getEvolutionPerformanceJoueur(joueurId);
            return new ResponseEntity<>(evolution, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/par-type-entrainement/joueur/{joueurId}")
    public ResponseEntity<List<Object[]>> getPerformancesParTypeEntrainement(@PathVariable Long joueurId) {
        try {
            List<Object[]> performances = performanceService.getPerformancesParTypeEntrainement(joueurId);
            return new ResponseEntity<>(performances, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comparaison-equipe/joueur/{joueurId}")
    public ResponseEntity<Object[]> getComparaisonAvecEquipe(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            Object[] comparaison = performanceService.getComparaisonAvecEquipe(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(comparaison, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-performers")
    public ResponseEntity<List<Object[]>> getTopPerformers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Object[]> topPerformers = performanceService.getTopPerformers(dateDebut, dateFin, limite);
            return new ResponseEntity<>(topPerformers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistiques-detaillees/joueur/{joueurId}")
    public ResponseEntity<Object[]> getStatistiquesDetailleesJoueur(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            Object[] statistiques = performanceService.getStatistiquesDetailleesJoueur(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Analyses avancées
    // =====================================================

    @GetMapping("/est-en-progression/joueur/{joueurId}")
    public ResponseEntity<Boolean> estEnProgression(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "10") int nombreDernieresPerformances) {
        try {
            boolean enProgression = performanceService.estEnProgression(joueurId, nombreDernieresPerformances);
            return new ResponseEntity<>(enProgression, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/point-fort-principal/joueur/{joueurId}")
    public ResponseEntity<String> getPointFortPrincipal(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            String pointFort = performanceService.getPointFortPrincipal(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(pointFort, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @GetMapping("/peut-etre-evalue")
    public ResponseEntity<Boolean> peutEtreEvalue(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean peutEtreEvalue = performanceService.peutEtreEvalue(entrainementId, joueurId);
            return new ResponseEntity<>(peutEtreEvalue, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/existe-performance")
    public ResponseEntity<Boolean> existePerformance(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean existe = performanceService.existePerformance(entrainementId, joueurId);
            return new ResponseEntity<>(existe, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Gestion en lot
    // =====================================================

    @PostMapping("/creer-performances-entrainement/{entrainementId}")
    public ResponseEntity<Void> creerPerformancesEntrainement(
            @PathVariable Long entrainementId,
            @RequestParam Long evaluateurId) {
        try {
            performanceService.creerPerformancesEntrainement(entrainementId, evaluateurId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
