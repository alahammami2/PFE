package com.sprintbot.planningperformance.controller;

import com.sprintbot.planningperformance.entity.Absence;
import com.sprintbot.planningperformance.service.AbsenceService;
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
@RequestMapping("/api/absences")
@CrossOrigin(origins = "*")
public class AbsenceController {

    @Autowired
    private AbsenceService absenceService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    @PostMapping
    public ResponseEntity<Absence> declarerAbsence(@Valid @RequestBody Absence absence) {
        try {
            Absence nouvelleAbsence = absenceService.declarerAbsence(absence);
            return new ResponseEntity<>(nouvelleAbsence, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Absence> getAbsence(@PathVariable Long id) {
        Optional<Absence> absence = absenceService.getAbsenceById(id);
        return absence.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                     .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/entrainement/{entrainementId}/joueur/{joueurId}")
    public ResponseEntity<Absence> getAbsenceSpecifique(
            @PathVariable Long entrainementId,
            @PathVariable Long joueurId) {
        try {
            Optional<Absence> absence = absenceService.getAbsence(entrainementId, joueurId);
            return absence.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Absence> modifierAbsence(@PathVariable Long id, 
                                                  @Valid @RequestBody Absence absence) {
        try {
            Absence absenceModifiee = absenceService.modifierAbsence(id, absence);
            return new ResponseEntity<>(absenceModifiee, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/justifier")
    public ResponseEntity<Absence> justifierAbsence(
            @PathVariable Long id,
            @RequestBody String justificatifUrl) {
        try {
            Absence absence = absenceService.justifierAbsence(id, justificatifUrl);
            return new ResponseEntity<>(absence, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAbsence(@PathVariable Long id) {
        try {
            absenceService.supprimerAbsence(id);
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
    public ResponseEntity<List<Absence>> getAbsencesJoueur(@PathVariable Long joueurId) {
        try {
            List<Absence> absences = absenceService.getAbsencesJoueur(joueurId);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entrainement/{entrainementId}")
    public ResponseEntity<List<Absence>> getAbsencesEntrainement(@PathVariable Long entrainementId) {
        try {
            List<Absence> absences = absenceService.getAbsencesEntrainement(entrainementId);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/periode")
    public ResponseEntity<List<Absence>> getAbsencesJoueurPeriode(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Absence> absences = absenceService.getAbsencesJoueurPeriode(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/motif/{motif}")
    public ResponseEntity<List<Absence>> getAbsencesParMotif(@PathVariable Absence.MotifAbsence motif) {
        try {
            List<Absence> absences = absenceService.getAbsencesParMotif(motif);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/justifiees/{justifiee}")
    public ResponseEntity<List<Absence>> getAbsencesJustifiees(@PathVariable Boolean justifiee) {
        try {
            List<Absence> absences = absenceService.getAbsencesJustifiees(justifiee);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/recentes")
    public ResponseEntity<List<Absence>> getAbsencesRecentes(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Absence> absences = absenceService.getAbsencesRecentes(joueurId, limite);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/non-justifiees")
    public ResponseEntity<List<Absence>> getAbsencesNonJustifiees() {
        try {
            List<Absence> absences = absenceService.getAbsencesNonJustifiees();
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Statistiques et analyses
    // =====================================================

    @GetMapping("/statistiques/par-joueur")
    public ResponseEntity<List<Object[]>> getNombreAbsencesParJoueur(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Object[]> statistiques = absenceService.getNombreAbsencesParJoueur(dateDebut, dateFin);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistiques/par-motif")
    public ResponseEntity<List<Object[]>> getAbsencesParMotif(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Object[]> statistiques = absenceService.getAbsencesParMotif(dateDebut, dateFin);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/taux-justifiees/joueur/{joueurId}")
    public ResponseEntity<Double> getTauxAbsencesJustifiees(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            Double taux = absenceService.getTauxAbsencesJustifiees(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(taux, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueurs-plus-absents")
    public ResponseEntity<List<Object[]>> getJoueursAvecPlusAbsences(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Object[]> joueurs = absenceService.getJoueursAvecPlusAbsences(dateDebut, dateFin, limite);
            return new ResponseEntity<>(joueurs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistiques/joueur/{joueurId}/par-mois")
    public ResponseEntity<List<Object[]>> getStatistiquesAbsencesParMois(@PathVariable Long joueurId) {
        try {
            List<Object[]> statistiques = absenceService.getStatistiquesAbsencesParMois(joueurId);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/par-type-entrainement/joueur/{joueurId}")
    public ResponseEntity<List<Object[]>> getAbsencesParTypeEntrainement(@PathVariable Long joueurId) {
        try {
            List<Object[]> absences = absenceService.getAbsencesParTypeEntrainement(joueurId);
            return new ResponseEntity<>(absences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Analyses comportementales
    // =====================================================

    @GetMapping("/joueur-problematique/{joueurId}")
    public ResponseEntity<Boolean> estJoueurProblematique(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            boolean problematique = absenceService.estJoueurProblematique(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(problematique, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/motif-principal/joueur/{joueurId}")
    public ResponseEntity<String> getMotifAbsencePrincipal(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            String motif = absenceService.getMotifAbsencePrincipal(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(motif, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @GetMapping("/peut-declarer")
    public ResponseEntity<Boolean> peutDeclarerAbsence(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean peutDeclarer = absenceService.peutDeclarerAbsence(entrainementId, joueurId);
            return new ResponseEntity<>(peutDeclarer, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/existe-absence")
    public ResponseEntity<Boolean> existeAbsence(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean existe = absenceService.existeAbsence(entrainementId, joueurId);
            return new ResponseEntity<>(existe, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Gestion automatique
    // =====================================================

    @PostMapping("/marquer-automatiques/{entrainementId}")
    public ResponseEntity<Void> marquerAbsencesAutomatiques(@PathVariable Long entrainementId) {
        try {
            absenceService.marquerAbsencesAutomatiques(entrainementId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =====================================================
    // Rapports
    // =====================================================

    @GetMapping("/nombre-absences/joueur/{joueurId}")
    public ResponseEntity<Integer> getNombreAbsencesJoueur(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            int nombreAbsences = absenceService.getNombreAbsencesJoueur(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(nombreAbsences, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre-justifiees/joueur/{joueurId}")
    public ResponseEntity<Integer> getNombreAbsencesJustifieesJoueur(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            int nombreJustifiees = absenceService.getNombreAbsencesJustifieesJoueur(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(nombreJustifiees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
