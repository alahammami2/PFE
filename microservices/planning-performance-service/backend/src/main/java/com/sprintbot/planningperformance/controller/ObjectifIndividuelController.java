package com.sprintbot.planningperformance.controller;

import com.sprintbot.planningperformance.entity.ObjectifIndividuel;
import com.sprintbot.planningperformance.service.ObjectifIndividuelService;
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
@RequestMapping("/api/objectifs")
@CrossOrigin(origins = "*")
public class ObjectifIndividuelController {

    @Autowired
    private ObjectifIndividuelService objectifService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    @PostMapping
    public ResponseEntity<ObjectifIndividuel> creerObjectif(@Valid @RequestBody ObjectifIndividuel objectif) {
        try {
            ObjectifIndividuel nouvelObjectif = objectifService.creerObjectif(objectif);
            return new ResponseEntity<>(nouvelObjectif, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectifIndividuel> getObjectif(@PathVariable Long id) {
        Optional<ObjectifIndividuel> objectif = objectifService.getObjectifById(id);
        return objectif.map(o -> new ResponseEntity<>(o, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ObjectifIndividuel>> getAllObjectifs() {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getAllObjectifs();
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjectifIndividuel> modifierObjectif(@PathVariable Long id, 
                                                              @Valid @RequestBody ObjectifIndividuel objectif) {
        try {
            ObjectifIndividuel objectifModifie = objectifService.modifierObjectif(id, objectif);
            return new ResponseEntity<>(objectifModifie, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/progression")
    public ResponseEntity<ObjectifIndividuel> mettreAJourProgression(
            @PathVariable Long id,
            @RequestParam BigDecimal progression) {
        try {
            ObjectifIndividuel objectif = objectifService.mettreAJourProgression(id, progression);
            return new ResponseEntity<>(objectif, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/marquer-atteint")
    public ResponseEntity<ObjectifIndividuel> marquerAtteint(@PathVariable Long id) {
        try {
            ObjectifIndividuel objectif = objectifService.marquerAtteint(id);
            return new ResponseEntity<>(objectif, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/abandonner")
    public ResponseEntity<ObjectifIndividuel> abandonnerObjectif(@PathVariable Long id) {
        try {
            ObjectifIndividuel objectif = objectifService.abandonnerObjectif(id);
            return new ResponseEntity<>(objectif, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/remettre-en-cours")
    public ResponseEntity<ObjectifIndividuel> remettrEnCours(@PathVariable Long id) {
        try {
            ObjectifIndividuel objectif = objectifService.remettrEnCours(id);
            return new ResponseEntity<>(objectif, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerObjectif(@PathVariable Long id) {
        try {
            objectifService.supprimerObjectif(id);
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
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsJoueur(@PathVariable Long joueurId) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsJoueur(joueurId);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsCoach(@PathVariable Long coachId) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsCoach(coachId);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsParType(@PathVariable ObjectifIndividuel.TypeObjectif type) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsParType(type);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsParStatut(@PathVariable ObjectifIndividuel.StatutObjectif statut) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsParStatut(statut);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/en-cours")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsEnCours(@PathVariable Long joueurId) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsEnCours(joueurId);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/echus")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsEchus() {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsEchus();
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/proches-echeance")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsProchesEcheance(
            @RequestParam(defaultValue = "7") int nombreJours) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsProchesEcheance(nombreJours);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/recents")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsRecents(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsRecents(joueurId, limite);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/plus-avances")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsLesPlusAvances(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "5") int limite) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsLesPlusAvances(joueurId, limite);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<ObjectifIndividuel>> rechercherObjectifs(@RequestParam String motCle) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.rechercherObjectifs(motCle);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Statistiques et analyses
    // =====================================================

    @GetMapping("/statistiques/joueur/{joueurId}")
    public ResponseEntity<List<Object[]>> getStatistiquesObjectifsJoueur(@PathVariable Long joueurId) {
        try {
            List<Object[]> statistiques = objectifService.getStatistiquesObjectifsJoueur(joueurId);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/progression-par-type/joueur/{joueurId}")
    public ResponseEntity<List<Object[]>> getProgressionMoyenneParType(@PathVariable Long joueurId) {
        try {
            List<Object[]> progression = objectifService.getProgressionMoyenneParType(joueurId);
            return new ResponseEntity<>(progression, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/taux-reussite/joueur/{joueurId}")
    public ResponseEntity<Double> getTauxReussiteObjectifs(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            Double taux = objectifService.getTauxReussiteObjectifs(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(taux, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/coach/{coachId}/periode")
    public ResponseEntity<List<ObjectifIndividuel>> getObjectifsCoachPeriode(
            @PathVariable Long coachId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<ObjectifIndividuel> objectifs = objectifService.getObjectifsCoachPeriode(coachId, dateDebut, dateFin);
            return new ResponseEntity<>(objectifs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Analyses comportementales
    // =====================================================

    @GetMapping("/joueur-motive/{joueurId}")
    public ResponseEntity<Boolean> estJoueurMotivé(@PathVariable Long joueurId) {
        try {
            boolean motive = objectifService.estJoueurMotivé(joueurId);
            return new ResponseEntity<>(motive, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type-prefere/joueur/{joueurId}")
    public ResponseEntity<ObjectifIndividuel.TypeObjectif> getTypeObjectifPrefere(@PathVariable Long joueurId) {
        try {
            ObjectifIndividuel.TypeObjectif type = objectifService.getTypeObjectifPrefere(joueurId);
            return new ResponseEntity<>(type, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Gestion automatique
    // =====================================================

    @PostMapping("/verifier-echeances")
    public ResponseEntity<Void> verifierEcheances() {
        try {
            objectifService.verifierEcheances();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rappels-echeance")
    public ResponseEntity<List<ObjectifIndividuel>> genererRappelsEcheance(
            @RequestParam(defaultValue = "7") int nombreJours) {
        try {
            List<ObjectifIndividuel> rappels = objectifService.genererRappelsEcheance(nombreJours);
            return new ResponseEntity<>(rappels, HttpStatus.OK);
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
            boolean peutEtreModifie = objectifService.peutEtreModifie(id);
            return new ResponseEntity<>(peutEtreModifie, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/est-echu")
    public ResponseEntity<Boolean> estEchu(@PathVariable Long id) {
        try {
            boolean echu = objectifService.estEchu(id);
            return new ResponseEntity<>(echu, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Rapports
    // =====================================================

    @GetMapping("/nombre/joueur/{joueurId}/statut/{statut}")
    public ResponseEntity<Integer> getNombreObjectifsJoueur(
            @PathVariable Long joueurId,
            @PathVariable ObjectifIndividuel.StatutObjectif statut) {
        try {
            int nombre = objectifService.getNombreObjectifsJoueur(joueurId, statut);
            return new ResponseEntity<>(nombre, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/progression-moyenne/joueur/{joueurId}")
    public ResponseEntity<BigDecimal> getProgressionMoyenneJoueur(@PathVariable Long joueurId) {
        try {
            BigDecimal progression = objectifService.getProgressionMoyenneJoueur(joueurId);
            return new ResponseEntity<>(progression, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
