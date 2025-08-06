package com.sprintbot.planningperformance.service;

import com.sprintbot.planningperformance.entity.Entrainement;
import com.sprintbot.planningperformance.entity.Participation;
import com.sprintbot.planningperformance.repository.EntrainementRepository;
import com.sprintbot.planningperformance.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private EntrainementRepository entrainementRepository;

    @Autowired
    private EntrainementService entrainementService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    public Participation inscrireJoueur(Long entrainementId, Long joueurId) {
        // Vérifier que l'entraînement existe
        Entrainement entrainement = entrainementRepository.findById(entrainementId)
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé avec l'ID: " + entrainementId));

        // Vérifier que le joueur n'est pas déjà inscrit
        if (participationRepository.isJoueurInscrit(entrainementId, joueurId)) {
            throw new IllegalStateException("Le joueur est déjà inscrit à cet entraînement");
        }

        // Vérifier qu'il y a des places disponibles
        if (!entrainementService.aPlacesDisponibles(entrainementId)) {
            throw new IllegalStateException("Aucune place disponible pour cet entraînement");
        }

        // Vérifier que l'entraînement est dans le futur
        if (entrainement.getDateEntrainement().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Impossible de s'inscrire à un entraînement passé");
        }

        // Vérifier que l'entraînement n'est pas annulé
        if (entrainement.getStatut() == Entrainement.StatutEntrainement.ANNULE) {
            throw new IllegalStateException("Impossible de s'inscrire à un entraînement annulé");
        }

        Participation participation = new Participation(entrainement, joueurId);
        return participationRepository.save(participation);
    }

    public void desinscrireJoueur(Long entrainementId, Long joueurId) {
        Participation participation = participationRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée"));

        // Vérifier que l'entraînement n'a pas encore commencé
        Entrainement entrainement = participation.getEntrainement();
        if (entrainement.getStatut() != Entrainement.StatutEntrainement.PLANIFIE) {
            throw new IllegalStateException("Impossible de se désinscrire d'un entraînement en cours ou terminé");
        }

        participationRepository.delete(participation);
    }

    public Participation marquerPresent(Long entrainementId, Long joueurId) {
        return modifierStatutParticipation(entrainementId, joueurId, Participation.StatutParticipation.PRESENT);
    }

    public Participation marquerAbsent(Long entrainementId, Long joueurId) {
        return modifierStatutParticipation(entrainementId, joueurId, Participation.StatutParticipation.ABSENT);
    }

    public Participation marquerExcuse(Long entrainementId, Long joueurId) {
        return modifierStatutParticipation(entrainementId, joueurId, Participation.StatutParticipation.EXCUSE);
    }

    private Participation modifierStatutParticipation(Long entrainementId, Long joueurId, 
                                                     Participation.StatutParticipation nouveauStatut) {
        return participationRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId)
                .map(participation -> {
                    participation.setStatutParticipation(nouveauStatut);
                    return participationRepository.save(participation);
                })
                .orElseThrow(() -> new RuntimeException("Participation non trouvée"));
    }

    public Participation ajouterCommentaire(Long entrainementId, Long joueurId, String commentaire) {
        return participationRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId)
                .map(participation -> {
                    participation.setCommentaire(commentaire);
                    return participationRepository.save(participation);
                })
                .orElseThrow(() -> new RuntimeException("Participation non trouvée"));
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @Transactional(readOnly = true)
    public List<Participation> getParticipationsJoueur(Long joueurId) {
        return participationRepository.findByJoueurIdOrderByIdDesc(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Participation> getParticipationsEntrainement(Long entrainementId) {
        return participationRepository.findByEntrainementIdOrderByJoueurId(entrainementId);
    }

    @Transactional(readOnly = true)
    public Optional<Participation> getParticipation(Long entrainementId, Long joueurId) {
        return participationRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId);
    }

    @Transactional(readOnly = true)
    public List<Participation> getParticipationsJoueurPeriode(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return participationRepository.findParticipationsJoueurPeriode(joueurId, dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Participation> getParticipationsParStatut(Long entrainementId, Participation.StatutParticipation statut) {
        return participationRepository.findByEntrainementAndStatut(entrainementId, statut);
    }

    @Transactional(readOnly = true)
    public List<Participation> getParticipationsRecentes(Long joueurId, int limite) {
        return participationRepository.findParticipationsRecentes(joueurId, limite);
    }

    // =====================================================
    // Statistiques
    // =====================================================

    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesParticipationEntrainement(Long entrainementId) {
        return participationRepository.countParticipationsParStatut(entrainementId);
    }

    @Transactional(readOnly = true)
    public Double getTauxPresenceJoueur(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        Double taux = participationRepository.getTauxPresenceJoueur(joueurId, dateDebut, dateFin);
        return taux != null ? taux : 0.0;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getJoueursLesPlussidus(LocalDate dateDebut, LocalDate dateFin) {
        return participationRepository.getJoueursLesPlussidus(dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesParticipationParMois(Long joueurId) {
        return participationRepository.getStatistiquesParticipationParMois(joueurId);
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @Transactional(readOnly = true)
    public boolean estInscrit(Long entrainementId, Long joueurId) {
        return participationRepository.isJoueurInscrit(entrainementId, joueurId);
    }

    @Transactional(readOnly = true)
    public boolean peutSinscrire(Long entrainementId, Long joueurId) {
        // Vérifier que l'entraînement existe
        Optional<Entrainement> entrainementOpt = entrainementRepository.findById(entrainementId);
        if (entrainementOpt.isEmpty()) {
            return false;
        }

        Entrainement entrainement = entrainementOpt.get();

        // Vérifier que l'entraînement est dans le futur
        if (entrainement.getDateEntrainement().isBefore(LocalDate.now())) {
            return false;
        }

        // Vérifier que l'entraînement n'est pas annulé
        if (entrainement.getStatut() == Entrainement.StatutEntrainement.ANNULE) {
            return false;
        }

        // Vérifier que le joueur n'est pas déjà inscrit
        if (participationRepository.isJoueurInscrit(entrainementId, joueurId)) {
            return false;
        }

        // Vérifier qu'il y a des places disponibles
        return entrainementService.aPlacesDisponibles(entrainementId);
    }

    @Transactional(readOnly = true)
    public boolean peutSeDesinscrire(Long entrainementId, Long joueurId) {
        // Vérifier que la participation existe
        Optional<Participation> participationOpt = participationRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId);
        if (participationOpt.isEmpty()) {
            return false;
        }

        // Vérifier que l'entraînement n'a pas encore commencé
        Entrainement entrainement = participationOpt.get().getEntrainement();
        return entrainement.getStatut() == Entrainement.StatutEntrainement.PLANIFIE;
    }

    // =====================================================
    // Gestion en lot
    // =====================================================

    public void marquerTousPresents(Long entrainementId) {
        List<Participation> participations = participationRepository.findByEntrainementIdOrderByJoueurId(entrainementId);
        participations.forEach(participation -> {
            if (participation.getStatutParticipation() == Participation.StatutParticipation.INSCRIT) {
                participation.setStatutParticipation(Participation.StatutParticipation.PRESENT);
            }
        });
        participationRepository.saveAll(participations);
    }

    public void inscrireJoueurs(Long entrainementId, List<Long> joueurIds) {
        for (Long joueurId : joueurIds) {
            try {
                inscrireJoueur(entrainementId, joueurId);
            } catch (Exception e) {
                // Log l'erreur mais continue avec les autres joueurs
                System.err.println("Erreur lors de l'inscription du joueur " + joueurId + ": " + e.getMessage());
            }
        }
    }

    // =====================================================
    // Rapports
    // =====================================================

    @Transactional(readOnly = true)
    public int getNombreInscrits(Long entrainementId) {
        return getParticipationsParStatut(entrainementId, Participation.StatutParticipation.INSCRIT).size();
    }

    @Transactional(readOnly = true)
    public int getNombrePresents(Long entrainementId) {
        return getParticipationsParStatut(entrainementId, Participation.StatutParticipation.PRESENT).size();
    }

    @Transactional(readOnly = true)
    public int getNombreAbsents(Long entrainementId) {
        return getParticipationsParStatut(entrainementId, Participation.StatutParticipation.ABSENT).size();
    }

    @Transactional(readOnly = true)
    public double getTauxPresenceEntrainement(Long entrainementId) {
        List<Participation> participations = participationRepository.findByEntrainementIdOrderByJoueurId(entrainementId);
        if (participations.isEmpty()) {
            return 0.0;
        }

        long presents = participations.stream()
                .mapToLong(p -> p.getStatutParticipation() == Participation.StatutParticipation.PRESENT ? 1 : 0)
                .sum();

        return (double) presents / participations.size() * 100.0;
    }
}
