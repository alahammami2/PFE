package com.sprintbot.planningperformance.service;

import com.sprintbot.planningperformance.entity.Absence;
import com.sprintbot.planningperformance.entity.Entrainement;
import com.sprintbot.planningperformance.entity.Participation;
import com.sprintbot.planningperformance.repository.AbsenceRepository;
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
public class AbsenceService {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private EntrainementRepository entrainementRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private ParticipationService participationService;

    // =====================================================
    // CRUD Operations
    // =====================================================

    public Absence declarerAbsence(Absence absence) {
        // Vérifier que l'entraînement existe
        Entrainement entrainement = entrainementRepository.findById(absence.getEntrainement().getId())
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé"));

        // Vérifier qu'une absence n'existe pas déjà
        if (absenceRepository.hasAbsence(entrainement.getId(), absence.getJoueurId())) {
            throw new IllegalStateException("Une absence est déjà déclarée pour ce joueur et cet entraînement");
        }

        // Vérifier que l'entraînement n'est pas trop ancien (max 3 jours après)
        if (entrainement.getDateEntrainement().isBefore(LocalDate.now().minusDays(3))) {
            throw new IllegalStateException("Impossible de déclarer une absence pour un entraînement trop ancien");
        }

        // Si le joueur était inscrit, marquer sa participation comme absente
        Optional<Participation> participationOpt = participationRepository
                .findByEntrainementIdAndJoueurId(entrainement.getId(), absence.getJoueurId());
        
        if (participationOpt.isPresent()) {
            Participation participation = participationOpt.get();
            if (absence.estJustifiee()) {
                participation.setStatutParticipation(Participation.StatutParticipation.EXCUSE);
            } else {
                participation.setStatutParticipation(Participation.StatutParticipation.ABSENT);
            }
            participationRepository.save(participation);
        }

        return absenceRepository.save(absence);
    }

    @Transactional(readOnly = true)
    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Absence> getAbsence(Long entrainementId, Long joueurId) {
        return absenceRepository.findByEntrainementIdAndJoueurId(entrainementId, joueurId);
    }

    public Absence modifierAbsence(Long id, Absence absenceModifiee) {
        return absenceRepository.findById(id)
                .map(absence -> {
                    // Vérifier que l'entraînement n'est pas trop ancien
                    if (absence.getEntrainement().getDateEntrainement().isBefore(LocalDate.now().minusDays(7))) {
                        throw new IllegalStateException("Impossible de modifier une absence trop ancienne");
                    }

                    // Mise à jour des champs
                    absence.setMotif(absenceModifiee.getMotif());
                    absence.setDescription(absenceModifiee.getDescription());
                    absence.setJustifiee(absenceModifiee.getJustifiee());
                    absence.setJustificatifUrl(absenceModifiee.getJustificatifUrl());

                    // Mettre à jour le statut de participation si nécessaire
                    Optional<Participation> participationOpt = participationRepository
                            .findByEntrainementIdAndJoueurId(absence.getEntrainement().getId(), absence.getJoueurId());
                    
                    if (participationOpt.isPresent()) {
                        Participation participation = participationOpt.get();
                        if (absence.estJustifiee()) {
                            participation.setStatutParticipation(Participation.StatutParticipation.EXCUSE);
                        } else {
                            participation.setStatutParticipation(Participation.StatutParticipation.ABSENT);
                        }
                        participationRepository.save(participation);
                    }

                    return absenceRepository.save(absence);
                })
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'ID: " + id));
    }

    public Absence justifierAbsence(Long id, String justificatifUrl) {
        return absenceRepository.findById(id)
                .map(absence -> {
                    absence.setJustifiee(true);
                    absence.setJustificatifUrl(justificatifUrl);

                    // Mettre à jour le statut de participation
                    Optional<Participation> participationOpt = participationRepository
                            .findByEntrainementIdAndJoueurId(absence.getEntrainement().getId(), absence.getJoueurId());
                    
                    if (participationOpt.isPresent()) {
                        Participation participation = participationOpt.get();
                        participation.setStatutParticipation(Participation.StatutParticipation.EXCUSE);
                        participationRepository.save(participation);
                    }

                    return absenceRepository.save(absence);
                })
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'ID: " + id));
    }

    public void supprimerAbsence(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'ID: " + id));

        // Vérifier que l'entraînement n'est pas trop ancien
        if (absence.getEntrainement().getDateEntrainement().isBefore(LocalDate.now().minusDays(7))) {
            throw new IllegalStateException("Impossible de supprimer une absence trop ancienne");
        }

        // Remettre le statut de participation à "inscrit" si applicable
        Optional<Participation> participationOpt = participationRepository
                .findByEntrainementIdAndJoueurId(absence.getEntrainement().getId(), absence.getJoueurId());
        
        if (participationOpt.isPresent()) {
            Participation participation = participationOpt.get();
            participation.setStatutParticipation(Participation.StatutParticipation.INSCRIT);
            participationRepository.save(participation);
        }

        absenceRepository.deleteById(id);
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesJoueur(Long joueurId) {
        return absenceRepository.findByJoueurIdOrderByIdDesc(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesEntrainement(Long entrainementId) {
        return absenceRepository.findByEntrainementIdOrderByDateDeclaration(entrainementId);
    }

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesJoueurPeriode(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.findAbsencesJoueurPeriode(joueurId, dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesParMotif(Absence.MotifAbsence motif) {
        return absenceRepository.findByMotifOrderByIdDesc(motif);
    }

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesJustifiees(Boolean justifiee) {
        return absenceRepository.findByJustifieeOrderByIdDesc(justifiee);
    }

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesRecentes(Long joueurId, int limite) {
        return absenceRepository.getAbsencesRecentes(joueurId, limite);
    }

    @Transactional(readOnly = true)
    public List<Absence> getAbsencesNonJustifiees() {
        return absenceRepository.getAbsencesNonJustifiees();
    }

    // =====================================================
    // Statistiques et analyses
    // =====================================================

    @Transactional(readOnly = true)
    public List<Object[]> getNombreAbsencesParJoueur(LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.getNombreAbsencesParJoueur(dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAbsencesParMotif(LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.getAbsencesParMotif(dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public Double getTauxAbsencesJustifiees(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        Double taux = absenceRepository.getTauxAbsencesJustifiees(joueurId, dateDebut, dateFin);
        return taux != null ? taux : 0.0;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getJoueursAvecPlusAbsences(LocalDate dateDebut, LocalDate dateFin, int limite) {
        return absenceRepository.getJoueursAvecPlusAbsences(dateDebut, dateFin, limite);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesAbsencesParMois(Long joueurId) {
        return absenceRepository.getStatistiquesAbsencesParMois(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAbsencesParTypeEntrainement(Long joueurId) {
        return absenceRepository.getAbsencesParTypeEntrainement(joueurId);
    }

    // =====================================================
    // Analyses comportementales
    // =====================================================

    @Transactional(readOnly = true)
    public boolean estJoueurProblematique(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        List<Absence> absences = absenceRepository.findAbsencesJoueurPeriode(joueurId, dateDebut, dateFin);
        
        if (absences.isEmpty()) {
            return false;
        }

        // Critères : plus de 3 absences non justifiées ou plus de 30% d'absences
        long absencesNonJustifiees = absences.stream()
                .mapToLong(a -> a.estJustifiee() ? 0 : 1)
                .sum();

        if (absencesNonJustifiees > 3) {
            return true;
        }

        // Calculer le taux d'absence par rapport aux entraînements
        Double tauxPresence = participationService.getTauxPresenceJoueur(joueurId, dateDebut, dateFin);
        return tauxPresence < 70.0; // Moins de 70% de présence
    }

    @Transactional(readOnly = true)
    public String getMotifAbsencePrincipal(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        List<Object[]> absencesParType = absenceRepository.getAbsencesParTypeEntrainement(joueurId);
        
        if (absencesParType.isEmpty()) {
            return "Aucune absence";
        }

        // Retourner le motif le plus fréquent
        Object[] motifPrincipal = absencesParType.get(0);
        return motifPrincipal[0].toString();
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @Transactional(readOnly = true)
    public boolean peutDeclarerAbsence(Long entrainementId, Long joueurId) {
        // Vérifier que l'entraînement existe
        Optional<Entrainement> entrainementOpt = entrainementRepository.findById(entrainementId);
        if (entrainementOpt.isEmpty()) {
            return false;
        }

        Entrainement entrainement = entrainementOpt.get();

        // Vérifier que l'entraînement n'est pas trop ancien
        if (entrainement.getDateEntrainement().isBefore(LocalDate.now().minusDays(3))) {
            return false;
        }

        // Vérifier qu'une absence n'existe pas déjà
        return !absenceRepository.hasAbsence(entrainementId, joueurId);
    }

    @Transactional(readOnly = true)
    public boolean existeAbsence(Long entrainementId, Long joueurId) {
        return absenceRepository.hasAbsence(entrainementId, joueurId);
    }

    // =====================================================
    // Gestion automatique
    // =====================================================

    public void marquerAbsencesAutomatiques(Long entrainementId) {
        // Récupérer tous les joueurs inscrits mais non présents
        List<Participation> participations = participationRepository.findByEntrainementIdOrderByJoueurId(entrainementId);
        
        Entrainement entrainement = entrainementRepository.findById(entrainementId)
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé"));

        for (Participation participation : participations) {
            if (participation.getStatutParticipation() == Participation.StatutParticipation.INSCRIT) {
                // Marquer comme absent si pas déjà déclaré
                if (!absenceRepository.hasAbsence(entrainementId, participation.getJoueurId())) {
                    Absence absence = new Absence();
                    absence.setEntrainement(entrainement);
                    absence.setJoueurId(participation.getJoueurId());
                    absence.setMotif(Absence.MotifAbsence.AUTRE);
                    absence.setDescription("Absence automatique - non présent");
                    absence.setJustifiee(false);
                    absence.setDeclarantId(participation.getJoueurId()); // Auto-déclaré
                    absence.setDateDeclaration(java.time.LocalDateTime.now());
                    absenceRepository.save(absence);
                }
                
                // Mettre à jour le statut de participation
                participation.setStatutParticipation(Participation.StatutParticipation.ABSENT);
                participationRepository.save(participation);
            }
        }
    }

    // =====================================================
    // Rapports
    // =====================================================

    @Transactional(readOnly = true)
    public int getNombreAbsencesJoueur(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.findAbsencesJoueurPeriode(joueurId, dateDebut, dateFin).size();
    }

    @Transactional(readOnly = true)
    public int getNombreAbsencesJustifieesJoueur(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        return (int) absenceRepository.findAbsencesJoueurPeriode(joueurId, dateDebut, dateFin)
                .stream()
                .mapToLong(a -> a.estJustifiee() ? 1 : 0)
                .sum();
    }
}
