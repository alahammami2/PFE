package com.sprintbot.planningperformance.controller;

import com.sprintbot.planningperformance.entity.Entrainement;
import com.sprintbot.planningperformance.service.EntrainementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entrainements")
@CrossOrigin(origins = "*")
public class EntrainementController {

    @Autowired
    private EntrainementService entrainementService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    @PostMapping
    public ResponseEntity<Entrainement> creerEntrainement(@Valid @RequestBody Entrainement entrainement) {
        try {
            Entrainement nouvelEntrainement = entrainementService.creerEntrainement(entrainement);
            return new ResponseEntity<>(nouvelEntrainement, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entrainement> getEntrainement(@PathVariable Long id) {
        Optional<Entrainement> entrainement = entrainementService.getEntrainementById(id);
        return entrainement.map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Entrainement>> getAllEntrainements() {
        try {
            List<Entrainement> entrainements = entrainementService.getAllEntrainements();
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entrainement> modifierEntrainement(@PathVariable Long id, 
                                                            @Valid @RequestBody Entrainement entrainement) {
        try {
            Entrainement entrainementModifie = entrainementService.modifierEntrainement(id, entrainement);
            return new ResponseEntity<>(entrainementModifie, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEntrainement(@PathVariable Long id) {
        try {
            entrainementService.supprimerEntrainement(id);
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
    // Gestion des statuts
    // =====================================================

    @PutMapping("/{id}/demarrer")
    public ResponseEntity<Entrainement> demarrerEntrainement(@PathVariable Long id) {
        try {
            Entrainement entrainement = entrainementService.demarrerEntrainement(id);
            return new ResponseEntity<>(entrainement, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<Entrainement> terminerEntrainement(@PathVariable Long id) {
        try {
            Entrainement entrainement = entrainementService.terminerEntrainement(id);
            return new ResponseEntity<>(entrainement, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Entrainement> annulerEntrainement(@PathVariable Long id) {
        try {
            Entrainement entrainement = entrainementService.annulerEntrainement(id);
            return new ResponseEntity<>(entrainement, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // =====================================================
    // Recherches spécialisées
    // =====================================================

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Entrainement>> getEntrainementsByCoach(@PathVariable Long coachId) {
        try {
            List<Entrainement> entrainements = entrainementService.getEntrainementsByCoach(coachId);
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/periode")
    public ResponseEntity<List<Entrainement>> getEntrainementsPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Entrainement> entrainements = entrainementService.getEntrainementsPeriode(dateDebut, dateFin);
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jour/{date}")
    public ResponseEntity<List<Entrainement>> getEntrainementsDuJour(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Entrainement> entrainements = entrainementService.getEntrainementsDuJour(date);
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/semaine/{date}")
    public ResponseEntity<List<Entrainement>> getEntrainementsDeLaSemaine(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Entrainement> entrainements = entrainementService.getEntrainementsDeLaSemaine(date);
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/futurs")
    public ResponseEntity<List<Entrainement>> getEntrainementsFuturs() {
        try {
            List<Entrainement> entrainements = entrainementService.getEntrainementsFuturs();
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/places-disponibles")
    public ResponseEntity<List<Entrainement>> getEntrainementsAvecPlacesDisponibles() {
        try {
            List<Entrainement> entrainements = entrainementService.getEntrainementsAvecPlacesDisponibles();
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<Entrainement>> rechercherEntrainements(@RequestParam String motCle) {
        try {
            List<Entrainement> entrainements = entrainementService.rechercherEntrainements(motCle);
            return new ResponseEntity<>(entrainements, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Statistiques
    // =====================================================

    @GetMapping("/statistiques/par-type")
    public ResponseEntity<List<Object[]>> getStatistiquesParType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Object[]> statistiques = entrainementService.getStatistiquesParType(dateDebut, dateFin);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistiques/par-coach")
    public ResponseEntity<List<Object[]>> getNombreEntrainementsParCoach(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Object[]> statistiques = entrainementService.getNombreEntrainementsParCoach(dateDebut, dateFin);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Gestion des places
    // =====================================================

    @GetMapping("/{id}/places-disponibles")
    public ResponseEntity<Boolean> aPlacesDisponibles(@PathVariable Long id) {
        try {
            boolean placesDisponibles = entrainementService.aPlacesDisponibles(id);
            return new ResponseEntity<>(placesDisponibles, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/places-restantes")
    public ResponseEntity<Integer> getNombrePlacesRestantes(@PathVariable Long id) {
        try {
            int placesRestantes = entrainementService.getNombrePlacesRestantes(id);
            return new ResponseEntity<>(placesRestantes, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @GetMapping("/{id}/peut-etre-modifie")
    public ResponseEntity<Boolean> peutEtreModifie(@PathVariable Long id) {
        try {
            boolean peutEtreModifie = entrainementService.peutEtreModifie(id);
            return new ResponseEntity<>(peutEtreModifie, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/est-dans-le-futur")
    public ResponseEntity<Boolean> estDansLeFutur(@PathVariable Long id) {
        try {
            boolean estDansLeFutur = entrainementService.estDansLeFutur(id);
            return new ResponseEntity<>(estDansLeFutur, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
