package com.sprintbot.planningperformance.service;

import com.sprintbot.planningperformance.entity.Entrainement;
import com.sprintbot.planningperformance.entity.Participation;
import com.sprintbot.planningperformance.repository.EntrainementRepository;
import com.sprintbot.planningperformance.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EntrainementService {

    @Autowired
    private EntrainementRepository entrainementRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    // =====================================================
    // CRUD Operations
    // =====================================================

    public Entrainement creerEntrainement(Entrainement entrainement) {
        // Validation des horaires
        if (entrainement.getHeureDebut().isAfter(entrainement.getHeureFin())) {
            throw new IllegalArgumentException("L'heure de début doit être antérieure à l'heure de fin");
        }

        // Validation de la date (pas dans le passé)
        if (entrainement.getDateEntrainement().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible de créer un entraînement dans le passé");
        }

        return entrainementRepository.save(entrainement);
    }

    @Transactional(readOnly = true)
    public Optional<Entrainement> getEntrainementById(Long id) {
        return entrainementRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Entrainement> getAllEntrainements() {
        return entrainementRepository.findAll();
    }

    public Entrainement modifierEntrainement(Long id, Entrainement entrainementModifie) {
        return entrainementRepository.findById(id)
                .map(entrainement -> {
                    // Vérifier si l'entraînement peut être modifié
                    if (entrainement.getStatut() == Entrainement.StatutEntrainement.TERMINE) {
                        throw new IllegalStateException("Impossible de modifier un entraînement terminé");
                    }

                    // Mise à jour des champs
                    entrainement.setTitre(entrainementModifie.getTitre());
                    entrainement.setDescription(entrainementModifie.getDescription());
                    entrainement.setDateEntrainement(entrainementModifie.getDateEntrainement());
                    entrainement.setHeureDebut(entrainementModifie.getHeureDebut());
                    entrainement.setHeureFin(entrainementModifie.getHeureFin());
                    entrainement.setLieu(entrainementModifie.getLieu());
                    entrainement.setTypeEntrainement(entrainementModifie.getTypeEntrainement());
                    entrainement.setNiveauIntensite(entrainementModifie.getNiveauIntensite());
                    entrainement.setObjectifs(entrainementModifie.getObjectifs());
                    entrainement.setMaterielRequis(entrainementModifie.getMaterielRequis());
                    entrainement.setNombreMaxJoueurs(entrainementModifie.getNombreMaxJoueurs());

                    return entrainementRepository.save(entrainement);
                })
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé avec l'ID: " + id));
    }

    public void supprimerEntrainement(Long id) {
        Entrainement entrainement = entrainementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé avec l'ID: " + id));

        // Vérifier si l'entraînement peut être supprimé
        if (entrainement.getStatut() == Entrainement.StatutEntrainement.EN_COURS ||
            entrainement.getStatut() == Entrainement.StatutEntrainement.TERMINE) {
            throw new IllegalStateException("Impossible de supprimer un entraînement en cours ou terminé");
        }

        entrainementRepository.deleteById(id);
    }

    // =====================================================
    // Business Logic
    // =====================================================

    public Entrainement demarrerEntrainement(Long id) {
        return entrainementRepository.findById(id)
                .map(entrainement -> {
                    if (entrainement.getStatut() != Entrainement.StatutEntrainement.PLANIFIE) {
                        throw new IllegalStateException("Seuls les entraînements planifiés peuvent être démarrés");
                    }
                    entrainement.setStatut(Entrainement.StatutEntrainement.EN_COURS);
                    return entrainementRepository.save(entrainement);
                })
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé avec l'ID: " + id));
    }

    public Entrainement terminerEntrainement(Long id) {
        return entrainementRepository.findById(id)
                .map(entrainement -> {
                    if (entrainement.getStatut() != Entrainement.StatutEntrainement.EN_COURS) {
                        throw new IllegalStateException("Seuls les entraînements en cours peuvent être terminés");
                    }
                    entrainement.setStatut(Entrainement.StatutEntrainement.TERMINE);
                    return entrainementRepository.save(entrainement);
                })
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé avec l'ID: " + id));
    }

    public Entrainement annulerEntrainement(Long id) {
        return entrainementRepository.findById(id)
                .map(entrainement -> {
                    if (entrainement.getStatut() == Entrainement.StatutEntrainement.TERMINE) {
                        throw new IllegalStateException("Impossible d'annuler un entraînement terminé");
                    }
                    entrainement.setStatut(Entrainement.StatutEntrainement.ANNULE);
                    return entrainementRepository.save(entrainement);
                })
                .orElseThrow(() -> new RuntimeException("Entraînement non trouvé avec l'ID: " + id));
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @Transactional(readOnly = true)
    public List<Entrainement> getEntrainementsByCoach(Long coachId) {
        return entrainementRepository.findByCoachIdOrderByDateEntrainementDesc(coachId);
    }

    @Transactional(readOnly = true)
    public List<Entrainement> getEntrainementsPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return entrainementRepository.findByDateEntrainementBetweenOrderByDateEntrainementAsc(dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Entrainement> getEntrainementsDuJour(LocalDate date) {
        return entrainementRepository.findEntrainementsDuJour(date);
    }

    @Transactional(readOnly = true)
    public List<Entrainement> getEntrainementsDeLaSemaine(LocalDate date) {
        LocalDate lundi = date.with(DayOfWeek.MONDAY);
        LocalDate dimanche = date.with(DayOfWeek.SUNDAY);
        return entrainementRepository.findEntrainementsDeLaSemaine(lundi, dimanche);
    }

    @Transactional(readOnly = true)
    public List<Entrainement> getEntrainementsFuturs() {
        return entrainementRepository.findEntrainementsFuturs(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Entrainement> getEntrainementsAvecPlacesDisponibles() {
        return entrainementRepository.findEntrainementsAvecPlacesDisponibles(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Entrainement> rechercherEntrainements(String motCle) {
        return entrainementRepository.rechercherParMotsCles(motCle);
    }

    // =====================================================
    // Statistiques
    // =====================================================

    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesParType(LocalDate dateDebut, LocalDate dateFin) {
        return entrainementRepository.getStatistiquesParType(dateDebut, dateFin);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getNombreEntrainementsParCoach(LocalDate dateDebut, LocalDate dateFin) {
        return entrainementRepository.getNombreEntrainementsParCoach(dateDebut, dateFin);
    }

    // =====================================================
    // Gestion des places
    // =====================================================

    @Transactional(readOnly = true)
    public boolean aPlacesDisponibles(Long entrainementId) {
        Optional<Entrainement> entrainementOpt = entrainementRepository.findById(entrainementId);
        if (entrainementOpt.isEmpty()) {
            return false;
        }

        Entrainement entrainement = entrainementOpt.get();
        if (entrainement.getNombreMaxJoueurs() == null) {
            return true; // Pas de limite
        }

        Long placesPrises = participationRepository.countPlacesPrises(entrainementId);
        return placesPrises < entrainement.getNombreMaxJoueurs();
    }

    @Transactional(readOnly = true)
    public int getNombrePlacesRestantes(Long entrainementId) {
        Optional<Entrainement> entrainementOpt = entrainementRepository.findById(entrainementId);
        if (entrainementOpt.isEmpty()) {
            return 0;
        }

        Entrainement entrainement = entrainementOpt.get();
        if (entrainement.getNombreMaxJoueurs() == null) {
            return Integer.MAX_VALUE; // Pas de limite
        }

        Long placesPrises = participationRepository.countPlacesPrises(entrainementId);
        return Math.max(0, entrainement.getNombreMaxJoueurs() - placesPrises.intValue());
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @Transactional(readOnly = true)
    public boolean peutEtreModifie(Long entrainementId) {
        return entrainementRepository.findById(entrainementId)
                .map(entrainement -> entrainement.getStatut() == Entrainement.StatutEntrainement.PLANIFIE)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean estDansLeFutur(Long entrainementId) {
        return entrainementRepository.findById(entrainementId)
                .map(entrainement -> entrainement.getDateEntrainement().isAfter(LocalDate.now()) ||
                                   entrainement.getDateEntrainement().equals(LocalDate.now()))
                .orElse(false);
    }
}
