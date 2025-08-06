package com.sprintbot.planningperformance.controller;

import com.sprintbot.planningperformance.entity.StatistiqueEntrainement;
import com.sprintbot.planningperformance.service.StatistiqueEntrainementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/statistiques")
@CrossOrigin(origins = "*")
public class StatistiqueEntrainementController {

    @Autowired
    private StatistiqueEntrainementService statistiqueService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<StatistiqueEntrainement> getStatistique(@PathVariable Long id) {
        Optional<StatistiqueEntrainement> statistique = statistiqueService.getStatistiqueById(id);
        return statistique.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<StatistiqueEntrainement>> getAllStatistiques() {
        try {
            List<StatistiqueEntrainement> statistiques = statistiqueService.getAllStatistiques();
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<StatistiqueEntrainement> getStatistiqueSpecifique(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            Optional<StatistiqueEntrainement> statistique = statistiqueService.getStatistique(joueurId, mois, annee);
            return statistique.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                             .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @GetMapping("/joueur/{joueurId}")
    public ResponseEntity<List<StatistiqueEntrainement>> getStatistiquesJoueur(@PathVariable Long joueurId) {
        try {
            List<StatistiqueEntrainement> statistiques = statistiqueService.getStatistiquesJoueur(joueurId);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/periode")
    public ResponseEntity<List<StatistiqueEntrainement>> getStatistiquesJoueurPeriode(
            @PathVariable Long joueurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<StatistiqueEntrainement> statistiques = statistiqueService.getStatistiquesJoueurPeriode(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/mois/{mois}/annee/{annee}")
    public ResponseEntity<List<StatistiqueEntrainement>> getStatistiquesMois(
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            List<StatistiqueEntrainement> statistiques = statistiqueService.getStatistiquesMois(mois, annee);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/recentes")
    public ResponseEntity<List<StatistiqueEntrainement>> getStatistiquesRecentes(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "6") int nombreMois) {
        try {
            List<StatistiqueEntrainement> statistiques = statistiqueService.getStatistiquesRecentes(joueurId, nombreMois);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueur/{joueurId}/meilleures")
    public ResponseEntity<List<StatistiqueEntrainement>> getMeilleuresStatistiques(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "5") int limite) {
        try {
            List<StatistiqueEntrainement> statistiques = statistiqueService.getMeilleuresStatistiques(joueurId, limite);
            return new ResponseEntity<>(statistiques, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Analyses et comparaisons
    // =====================================================

    @GetMapping("/evolution/joueur/{joueurId}")
    public ResponseEntity<List<StatistiqueEntrainement>> getEvolutionJoueur(@PathVariable Long joueurId) {
        try {
            List<StatistiqueEntrainement> evolution = statistiqueService.getEvolutionJoueur(joueurId);
            return new ResponseEntity<>(evolution, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comparaison-equipe/joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<Object[]> getComparaisonAvecEquipe(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            Object[] comparaison = statistiqueService.getComparaisonAvecEquipe(joueurId, mois, annee);
            return new ResponseEntity<>(comparaison, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-performers/mois/{mois}/annee/{annee}")
    public ResponseEntity<List<StatistiqueEntrainement>> getTopPerformers(
            @PathVariable int mois,
            @PathVariable int annee,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<StatistiqueEntrainement> topPerformers = statistiqueService.getTopPerformers(mois, annee, limite);
            return new ResponseEntity<>(topPerformers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/joueurs-assidus/mois/{mois}/annee/{annee}")
    public ResponseEntity<List<StatistiqueEntrainement>> getJoueursLesPlussidus(
            @PathVariable int mois,
            @PathVariable int annee,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<StatistiqueEntrainement> joueurs = statistiqueService.getJoueursLesPlussidus(mois, annee, limite);
            return new ResponseEntity<>(joueurs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/moyennes-equipe/mois/{mois}/annee/{annee}")
    public ResponseEntity<Map<String, Object>> getMoyennesEquipe(
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            Map<String, Object> moyennes = statistiqueService.getMoyennesEquipe(mois, annee);
            return new ResponseEntity<>(moyennes, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Analyses comportementales
    // =====================================================

    @GetMapping("/joueur-en-progression/{joueurId}")
    public ResponseEntity<Boolean> estJoueurEnProgression(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "3") int nombreMois) {
        try {
            boolean enProgression = statistiqueService.estJoueurEnProgression(joueurId, nombreMois);
            return new ResponseEntity<>(enProgression, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profil-joueur/{joueurId}")
    public ResponseEntity<String> getProfilJoueur(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "2024") Integer annee) {
        try {
            String profil = statistiqueService.getProfilJoueur(joueurId, annee);
            return new ResponseEntity<>(profil, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tendance-performance/joueur/{joueurId}")
    public ResponseEntity<List<Object[]>> getTendancePerformance(
            @PathVariable Long joueurId,
            @RequestParam(defaultValue = "6") int nombreMois) {
        try {
            List<Object[]> tendance = statistiqueService.getTendancePerformance(joueurId, nombreMois);
            return new ResponseEntity<>(tendance, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Gestion automatique
    // =====================================================

    @PostMapping("/calculer-mois/{mois}/annee/{annee}")
    public ResponseEntity<Void> calculerStatistiquesMois(
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            statistiqueService.calculerStatistiquesTousJoueurs(mois, annee);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/calculer-joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<StatistiqueEntrainement> calculerStatistiqueJoueur(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            StatistiqueEntrainement statistique = statistiqueService.calculerStatistiqueJoueur(joueurId, mois, annee);
            return new ResponseEntity<>(statistique, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/recalculer-toutes")
    public ResponseEntity<Void> recalculerToutesStatistiques() {
        try {
            statistiqueService.recalculerToutesStatistiques();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =====================================================
    // Rapports spécialisés
    // =====================================================

    @GetMapping("/rapport-mensuel/mois/{mois}/annee/{annee}")
    public ResponseEntity<String> genererRapportMensuel(
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            String rapport = statistiqueService.genererRapportMensuel(mois, annee);
            return new ResponseEntity<>(rapport, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rapport-equipe")
    public ResponseEntity<String> genererRapportEquipe(
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        try {
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);
            String rapport = statistiqueService.genererRapportEquipe(debut, fin);
            return new ResponseEntity<>(rapport, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Métriques spécifiques
    // =====================================================

    @GetMapping("/taux-presence/joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<BigDecimal> getTauxPresence(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            LocalDate dateDebut = LocalDate.of(annee, mois, 1);
            LocalDate dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
            BigDecimal taux = statistiqueService.getTauxPresence(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(taux, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/performance-moyenne/joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<BigDecimal> getPerformanceMoyenne(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            LocalDate dateDebut = LocalDate.of(annee, mois, 1);
            LocalDate dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
            BigDecimal performance = statistiqueService.getPerformanceMoyenne(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(performance, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre-entrainements/joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<Integer> getNombreEntrainements(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            LocalDate dateDebut = LocalDate.of(annee, mois, 1);
            LocalDate dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
            int nombre = statistiqueService.getNombreEntrainements(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(nombre, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre-absences/joueur/{joueurId}/mois/{mois}/annee/{annee}")
    public ResponseEntity<Integer> getNombreAbsences(
            @PathVariable Long joueurId,
            @PathVariable int mois,
            @PathVariable int annee) {
        try {
            LocalDate dateDebut = LocalDate.of(annee, mois, 1);
            LocalDate dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
            int nombre = statistiqueService.getNombreAbsences(joueurId, dateDebut, dateFin);
            return new ResponseEntity<>(nombre, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @GetMapping("/existe-statistique")
    public ResponseEntity<Boolean> existeStatistique(
            @RequestParam Long joueurId,
            @RequestParam int mois,
            @RequestParam int annee) {
        try {
            boolean existe = statistiqueService.existeStatistique(joueurId, mois, annee);
            return new ResponseEntity<>(existe, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/peut-calculer")
    public ResponseEntity<Boolean> peutCalculerStatistique(
            @RequestParam Long joueurId,
            @RequestParam int mois,
            @RequestParam int annee) {
        try {
            boolean peutCalculer = statistiqueService.peutCalculerStatistique(joueurId, mois, annee);
            return new ResponseEntity<>(peutCalculer, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
