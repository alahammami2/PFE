package com.sprintbot.planningperformance.service;

import com.sprintbot.planningperformance.entity.Entrainement;
import com.sprintbot.planningperformance.entity.Performance;
import com.sprintbot.planningperformance.entity.Participation;
import com.sprintbot.planningperformance.repository.EntrainementRepository;
import com.sprintbot.planningperformance.repository.PerformanceRepository;
import com.sprintbot.planningperformance.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EntrainementRepository entrainementRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    // =====================================================
    // CRUD Operations
    // =====================================================

    public Performance creerPerformance(Performance performance) {
        // Vérifier que l'entraînement existe
        Entrainement entrainement = entrainementRepository.findById(performance.getEntrainement().getId())
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé"));

        // Vérifier que le joueur était présent
        Optional<Participation> participationOpt = participationRepository
                .findByEntrainementIdAndJoueurId(entrainement.getId(), performance.getJoueurId());
        
        if (participationOpt.isEmpty() || 
            participationOpt.get().getStatutParticipation() != Participation.StatutParticipation.PRESENT) {
            throw new IllegalStateException("Impossible d'évaluer un joueur qui n'était pas présent");
        }

        // Vérifier qu'une performance n'existe pas déjà
        if (performanceRepository.existsPerformance(entrainement.getId(), performance.getJoueurId())) {
            throw new IllegalStateException("Une performance existe déjà pour ce joueur et cet entraînement");
        }

        // Calculer la note globale automatiquement si les notes détaillées sont fournies
        if (performance.getNoteTechnique() != null && 
            performance.getNotePhysique() != null && 
            performance.getNoteMental() != null) {
            performance.setNoteGlobale(performance.calculerMoyenne());
        }

        return performanceRepository.save(performance);
    }

    @Transactional(readOnly = true)
    public Optional<Performance> getPerformanceById(Long id) {
        return performanceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Performance> getPerformance(Long entrainementId, Long joueurId) {
        return performanceRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId);
    }

    public Performance modifierPerformance(Long id, Performance performanceModifiee) {
        return performanceRepository.findById(id)
                .map(performance -> {
                    // Vérifier que l'entraînement n'est pas trop ancien (par exemple, max 7 jours)
                    if (performance.getEntrainement().getDateEntrainement().isBefore(LocalDate.now().minusDays(7))) {
                        throw new IllegalStateException("Impossible de modifier une performance trop ancienne");
                    }

                    // Mise à jour des champs
                    performance.setNoteTechnique(performanceModifiee.getNoteTechnique());
                    performance.setNotePhysique(performanceModifiee.getNotePhysique());
                    performance.setNoteMental(performanceModifiee.getNoteMental());
                    performance.setCommentaireCoach(performanceModifiee.getCommentaireCoach());
                    performance.setObjectifsAtteints(performanceModifiee.getObjectifsAtteints());
                    performance.setPointsForts(performanceModifiee.getPointsForts());
                    performance.setPointsAmelioration(performanceModifiee.getPointsAmelioration());

                    // Recalculer la note globale
                    if (performance.getNoteTechnique() != null && 
                        performance.getNotePhysique() != null && 
                        performance.getNoteMental() != null) {
                        performance.setNoteGlobale(performance.calculerMoyenne());
                    }

                    return performanceRepository.save(performance);
                })
                .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'ID: " + id));
    }

    public Performance ajouterAutoEvaluation(Long entrainementId, Long joueurId, 
                                           BigDecimal autoEvaluation, String commentaireJoueur) {
        return performanceRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId)
                .map(performance -> {
                    performance.setAutoEvaluation(autoEvaluation);
                    performance.setCommentaireJoueur(commentaireJoueur);
                    return performanceRepository.save(performance);
                })
                .orElseThrow(() -> new RuntimeException("Performance non trouvée"));
    }

    public void supprimerPerformance(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'ID: " + id));

        // Vérifier que l'entraînement n'est pas trop ancien
        if (performance.getEntrainement().getDateEntrainement().isBefore(LocalDate.now().minusDays(7))) {
            throw new IllegalStateException("Impossible de supprimer une performance trop ancienne");
        }

        performanceRepository.deleteById(id);
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @Transactional(readOnly = true)
    public List<Performance> getPerformancesJoueur(Long joueurId) {
        return performanceRepository.findByJoueurIdOrderByIdDesc(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Performance> getPerformancesEntrainement(Long entrainementId) {
        return performanceRepository.findByEntrainementIdOrderByNoteGlobaleDesc(entrainementId);
    }

    @Transactional(readOnly = true)
    public List<Performance> getPerformancesJoueurPeriode(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return performanceRepository.findPerformancesJoueurPeriode(joueurId, dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Performance> getMeilleuresPerformances(Long joueurId, int limite) {
        return performanceRepository.getMeilleuresPerformances(joueurId, limite);
    }

    @Transactional(readOnly = true)
    public List<Performance> getPerformancesRecentes(Long joueurId, int limite) {
        return performanceRepository.getPerformancesRecentes(joueurId, limite);
    }

    @Transactional(readOnly = true)
    public List<Performance> getPerformancesAvecObjectifsAtteints(Long joueurId) {
        return performanceRepository.getPerformancesAvecObjectifsAtteints(joueurId);
    }

    // =====================================================
    // Statistiques et analyses
    // =====================================================

    @Transactional(readOnly = true)
    public BigDecimal getMoyennePerformanceJoueur(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        BigDecimal moyenne = performanceRepository.getMoyennePerformanceJoueur(joueurId, dateDebut, dateFin);
        return moyenne != null ? moyenne : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getEvolutionPerformanceJoueur(Long joueurId) {
        return performanceRepository.getEvolutionPerformanceJoueur(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPerformancesParTypeEntrainement(Long joueurId) {
        return performanceRepository.getPerformancesParTypeEntrainement(joueurId);
    }

    @Transactional(readOnly = true)
    public Object[] getComparaisonAvecEquipe(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return performanceRepository.getComparaisonAvecEquipe(joueurId, dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTopPerformers(LocalDate dateDebut, LocalDate dateFin, int limite) {
        return performanceRepository.getTopPerformers(dateDebut, dateFin, limite);
    }

    @Transactional(readOnly = true)
    public Object[] getStatistiquesDetailleesJoueur(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return performanceRepository.getStatistiquesDetailleesJoueur(joueurId, dateDebut, dateFin);
    }

    // =====================================================
    // Analyses avancées
    // =====================================================

    @Transactional(readOnly = true)
    public boolean estEnProgression(Long joueurId, int nombreDernieresPerformances) {
        List<Performance> performances = performanceRepository.getPerformancesRecentes(joueurId, nombreDernieresPerformances);
        
        if (performances.size() < 2) {
            return false;
        }

        // Comparer la moyenne des premières performances avec les dernières
        int milieu = performances.size() / 2;
        
        BigDecimal moyenneRecente = performances.subList(0, milieu).stream()
                .map(Performance::getNoteGlobale)
                .filter(note -> note != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(milieu), 2, java.math.RoundingMode.HALF_UP);

        BigDecimal moyenneAncienne = performances.subList(milieu, performances.size()).stream()
                .map(Performance::getNoteGlobale)
                .filter(note -> note != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(performances.size() - milieu), 2, java.math.RoundingMode.HALF_UP);

        return moyenneRecente.compareTo(moyenneAncienne) > 0;
    }

    @Transactional(readOnly = true)
    public String getPointFortPrincipal(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        List<Performance> performances = performanceRepository.findPerformancesJoueurPeriode(joueurId, dateDebut, dateFin);
        
        if (performances.isEmpty()) {
            return "Aucune donnée disponible";
        }

        BigDecimal moyenneTechnique = BigDecimal.ZERO;
        BigDecimal moyennePhysique = BigDecimal.ZERO;
        BigDecimal moyenneMental = BigDecimal.ZERO;
        int count = 0;

        for (Performance perf : performances) {
            if (perf.getNoteTechnique() != null && perf.getNotePhysique() != null && perf.getNoteMental() != null) {
                moyenneTechnique = moyenneTechnique.add(perf.getNoteTechnique());
                moyennePhysique = moyennePhysique.add(perf.getNotePhysique());
                moyenneMental = moyenneMental.add(perf.getNoteMental());
                count++;
            }
        }

        if (count == 0) {
            return "Aucune évaluation complète disponible";
        }

        moyenneTechnique = moyenneTechnique.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
        moyennePhysique = moyennePhysique.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
        moyenneMental = moyenneMental.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);

        if (moyenneTechnique.compareTo(moyennePhysique) >= 0 && moyenneTechnique.compareTo(moyenneMental) >= 0) {
            return "Technique";
        } else if (moyennePhysique.compareTo(moyenneMental) >= 0) {
            return "Physique";
        } else {
            return "Mental";
        }
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @Transactional(readOnly = true)
    public boolean peutEtreEvalue(Long entrainementId, Long joueurId) {
        // Vérifier que le joueur était présent
        Optional<Participation> participationOpt = participationRepository
                .findByEntrainementIdAndJoueurId(entrainementId, joueurId);
        
        return participationOpt.isPresent() && 
               participationOpt.get().getStatutParticipation() == Participation.StatutParticipation.PRESENT;
    }

    @Transactional(readOnly = true)
    public boolean existePerformance(Long entrainementId, Long joueurId) {
        return performanceRepository.existsPerformance(entrainementId, joueurId);
    }

    // =====================================================
    // Gestion en lot
    // =====================================================

    public void creerPerformancesEntrainement(Long entrainementId, Long evaluateurId) {
        // Récupérer tous les joueurs présents
        List<Participation> participationsPresentes = participationRepository
                .findByEntrainementAndStatut(entrainementId, Participation.StatutParticipation.PRESENT);

        Entrainement entrainement = entrainementRepository.findById(entrainementId)
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé"));

        for (Participation participation : participationsPresentes) {
            // Vérifier qu'une performance n'existe pas déjà
            if (!performanceRepository.existsPerformance(entrainementId, participation.getJoueurId())) {
                Performance performance = new Performance();
                performance.setEntrainement(entrainement);
                performance.setJoueurId(participation.getJoueurId());
                performance.setEvaluateurId(evaluateurId);
                performance.setDateEvaluation(java.time.LocalDateTime.now());
                performanceRepository.save(performance);
            }
        }
    }
}
