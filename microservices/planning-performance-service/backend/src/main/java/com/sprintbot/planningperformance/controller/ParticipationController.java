package com.sprintbot.planningperformance.controller;

import com.sprintbot.planningperformance.entity.Participation;
import com.sprintbot.planningperformance.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participations")
@CrossOrigin(origins = "*")
public class ParticipationController {

    @Autowired
    private ParticipationService participationService;

    // =====================================================
    // Gestion des inscriptions
    // =====================================================

    @PostMapping("/inscrire")
    public ResponseEntity<Participation> inscrireJoueur(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            Participation participation = participationService.inscrireJoueur(entrainementId, joueurId);
            return new ResponseEntity<>(participation, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/desinscrire")
    public ResponseEntity<Void> desinscrireJoueur(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            participationService.desinscrireJoueur(entrainementId, joueurId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/inscrire-multiple")
    public ResponseEntity<Void> inscrireJoueurs(
            @RequestParam Long entrainementId,
            @RequestBody List<Long> joueurIds) {
        try {
            participationService.inscrireJoueurs(entrainementId, joueurIds);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =====================================================
    // Gestion des présences
    // =====================================================

    @PutMapping("/marquer-present")
    public ResponseEntity<Participation> marquerPresent(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            Participation participation = participationService.marquerPresent(entrainementId, joueurId);
            return new ResponseEntity<>(participation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/marquer-absent")
    public ResponseEntity<Participation> marquerAbsent(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            Participation participation = participationService.marquerAbsent(entrainementId, joueurId);
            return new ResponseEntity<>(participation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/marquer-excuse")
    public ResponseEntity<Participation> marquerExcuse(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            Participation participation = participationService.marquerExcuse(entrainementId, joueurId);
            return new ResponseEntity<>(participation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/marquer-tous-presents/{entrainementId}")
    public ResponseEntity<Void> marquerTousPresents(@PathVariable Long entrainementId) {
        try {
            participationService.marquerTousPresents(entrainementId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/ajouter-commentaire")
    public ResponseEntity<Participation> ajouterCommentaire(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId,
            @RequestBody String commentaire) {
        try {
            Participation participation = participationService.ajouterCommentaire(entrainementId, joueurId, commentaire);
            return new ResponseEntity<>(participation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @GetMapping("/joueur/{joueurId}")
    public ResponseEntity<List<Participation>> getParticipationsJoueur(@PathVariable Long joueurId) {
        try {
            List<Participation> participations = participationService.getParticipationsJoueur(joueurId);
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entrainement/{entrainementId}")
    public ResponseEntity<List<Participation>> getParticipationsEntrainement(@PathVariable Long entrainementId) {
        try {
            List<Participation> participations = participationService.getParticipationsEntrainement(entrainementId);
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/participation")
    public ResponseEntity<Participation> getParticipation(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            Optional<Participation> participation = participationService.getParticipation(entrainementId, joueurId);
            return participation.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                              .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/periode")
    public ResponseEntity<List<Participation>> getParticipationsJoueurPeriode(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Participation> participations = participationService.getParticipationsJoueurPeriode(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entrainement/{entrainementId}/statut/{statut}")
    public ResponseEntity<List<Participation>> getParticipationsParStatut(
            @PathVariable Long entrainementId,
            @PathVariable Participation.StatutParticipation statut) {
        try {
            List<Participation> participations = participationService.getParticipationsParStatut(entrainementId, statut);
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/recentes")
    public ResponseEntity<List<Participation>> getParticipationsRecentes(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Participation> participations = participationService.getParticipationsRecentes(joueurId, limite);
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Statistiques
    // =====================================================

    @GetMapping("/statistiques/entrainement/{entrainementId}")
    public ResponseEntity<List<Object[]>> getStatistiquesParticipationEntrainement(@PathVariable Long entrainementId) {
        try {
            List<Object[]> statistiques = participationService.getStatistiquesParticipationEntrainement(entrainementId);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/taux-presence/joueur/{joueurId}")
    public ResponseEntity<Double> getTauxPresenceJoueur(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            Double tauxPresence = participationService.getTauxPresenceJoueur(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(tauxPresence, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueurs-assidus")
    public ResponseEntity<List<Object[]>> getJoueursLesPlussidus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Object[]> joueurs = participationService.getJoueursLesPlussidus(dateDebut, dateFin);
            return new ResponseEntity<>(joueurs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistiques/joueur/{joueurId}/par-mois")
    public ResponseEntity<List<Object[]>> getStatistiquesParticipationParMois(@PathVariable Long joueurId) {
        try {
            List<Object[]> statistiques = participationService.getStatistiquesParticipationParMois(joueurId);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @GetMapping("/est-inscrit")
    public ResponseEntity<Boolean> estInscrit(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean estInscrit = participationService.estInscrit(entrainementId, joueurId);
            return new ResponseEntity<>(estInscrit, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/peut-sinscrire")
    public ResponseEntity<Boolean> peutSinscrire(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean peutSinscrire = participationService.peutSinscrire(entrainementId, joueurId);
            return new ResponseEntity<>(peutSinscrire, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/peut-se-desinscrire")
    public ResponseEntity<Boolean> peutSeDesinscrire(
            @RequestParam Long entrainementId,
            @RequestParam Long joueurId) {
        try {
            boolean peutSeDesinscrire = participationService.peutSeDesinscrire(entrainementId, joueurId);
            return new ResponseEntity<>(peutSeDesinscrire, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Rapports
    // =====================================================

    @GetMapping("/nombre-inscrits/{entrainementId}")
    public ResponseEntity<Integer> getNombreInscrits(@PathVariable Long entrainementId) {
        try {
            int nombreInscrits = participationService.getNombreInscrits(entrainementId);
            return new ResponseEntity<>(nombreInscrits, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre-presents/{entrainementId}")
    public ResponseEntity<Integer> getNombrePresents(@PathVariable Long entrainementId) {
        try {
            int nombrePresents = participationService.getNombrePresents(entrainementId);
            return new ResponseEntity<>(nombrePresents, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre-absents/{entrainementId}")
    public ResponseEntity<Integer> getNombreAbsents(@PathVariable Long entrainementId) {
        try {
            int nombreAbsents = participationService.getNombreAbsents(entrainementId);
            return new ResponseEntity<>(nombreAbsents, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/taux-presence-entrainement/{entrainementId}")
    public ResponseEntity<Double> getTauxPresenceEntrainement(@PathVariable Long entrainementId) {
        try {
            double tauxPresence = participationService.getTauxPresenceEntrainement(entrainementId);
            return new ResponseEntity<>(tauxPresence, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
