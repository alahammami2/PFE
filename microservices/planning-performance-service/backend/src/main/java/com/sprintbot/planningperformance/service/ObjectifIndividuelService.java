package com.sprintbot.planningperformance.service;

import com.sprintbot.planningperformance.entity.ObjectifIndividuel;
import com.sprintbot.planningperformance.repository.ObjectifIndividuelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ObjectifIndividuelService {

    @Autowired
    private ObjectifIndividuelRepository objectifRepository;

    // =====================================================
    // CRUD Operations
    // =====================================================

    public ObjectifIndividuel creerObjectif(ObjectifIndividuel objectif) {
        // Validation des dates
        if (objectif.getDateEcheance() != null && 
            objectif.getDateEcheance().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'échéance ne peut pas être dans le passé");
        }

        // Validation de la progression
        if (objectif.getProgression() != null &&
            (objectif.getProgression() < 0 || objectif.getProgression() > 100)) {
            throw new IllegalArgumentException("La progression doit être entre 0 et 100");
        }

        // Initialiser le statut si non défini
        if (objectif.getStatut() == null) {
            objectif.setStatut(ObjectifIndividuel.StatutObjectif.EN_COURS);
        }

        // Initialiser la progression si non définie
        if (objectif.getProgression() == null) {
            objectif.setProgression(0);
        }

        return objectifRepository.save(objectif);
    }

    @Transactional(readOnly = true)
    public Optional<ObjectifIndividuel> getObjectifById(Long id) {
        return objectifRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getAllObjectifs() {
        return objectifRepository.findAll();
    }

    public ObjectifIndividuel modifierObjectif(Long id, ObjectifIndividuel objectifModifie) {
        return objectifRepository.findById(id)
                .map(objectif -> {
                    // Vérifier que l'objectif peut être modifié
                    if (objectif.getStatut() == ObjectifIndividuel.StatutObjectif.ATTEINT ||
                        objectif.getStatut() == ObjectifIndividuel.StatutObjectif.ABANDONNE) {
                        throw new IllegalStateException("Impossible de modifier un objectif atteint ou abandonné");
                    }

                    // Mise à jour des champs
                    objectif.setTitre(objectifModifie.getTitre());
                    objectif.setDescription(objectifModifie.getDescription());
                    objectif.setTypeObjectif(objectifModifie.getTypeObjectif());
                    objectif.setDateEcheance(objectifModifie.getDateEcheance());
                    objectif.setCritereReussite(objectifModifie.getCritereReussite());

                    // Validation de la nouvelle date d'échéance
                    if (objectif.getDateEcheance() != null && 
                        objectif.getDateEcheance().isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("La nouvelle date d'échéance ne peut pas être dans le passé");
                    }

                    return objectifRepository.save(objectif);
                })
                .orElseThrow(() -> new RuntimeException("Objectif non trouvé avec l'ID: " + id));
    }

    public ObjectifIndividuel mettreAJourProgression(Long id, BigDecimal nouvelleProgression) {
        return objectifRepository.findById(id)
                .map(objectif -> {
                    // Validation de la progression
                    if (nouvelleProgression.compareTo(BigDecimal.ZERO) < 0 || 
                        nouvelleProgression.compareTo(BigDecimal.valueOf(100)) > 0) {
                        throw new IllegalArgumentException("La progression doit être entre 0 et 100");
                    }

                    objectif.setProgression(nouvelleProgression.intValue());

                    // Mettre à jour automatiquement le statut si nécessaire
                    if (nouvelleProgression.compareTo(BigDecimal.valueOf(100)) == 0) {
                        objectif.setStatut(ObjectifIndividuel.StatutObjectif.ATTEINT);
                        objectif.setDateAtteinte(LocalDate.now());
                    } else if (objectif.getStatut() == ObjectifIndividuel.StatutObjectif.ATTEINT) {
                        // Si la progression diminue, remettre en cours
                        objectif.setStatut(ObjectifIndividuel.StatutObjectif.EN_COURS);
                        objectif.setDateAtteinte(null);
                    }

                    return objectifRepository.save(objectif);
                })
                .orElseThrow(() -> new RuntimeException("Objectif non trouvé avec l'ID: " + id));
    }

    public ObjectifIndividuel marquerAtteint(Long id) {
        return objectifRepository.findById(id)
                .map(objectif -> {
                    objectif.setStatut(ObjectifIndividuel.StatutObjectif.ATTEINT);
                    objectif.setProgression(100);
                    objectif.setDateAtteinte(LocalDate.now());
                    return objectifRepository.save(objectif);
                })
                .orElseThrow(() -> new RuntimeException("Objectif non trouvé avec l'ID: " + id));
    }

    public ObjectifIndividuel abandonnerObjectif(Long id) {
        return objectifRepository.findById(id)
                .map(objectif -> {
                    objectif.setStatut(ObjectifIndividuel.StatutObjectif.ABANDONNE);
                    return objectifRepository.save(objectif);
                })
                .orElseThrow(() -> new RuntimeException("Objectif non trouvé avec l'ID: " + id));
    }

    public ObjectifIndividuel remettrEnCours(Long id) {
        return objectifRepository.findById(id)
                .map(objectif -> {
                    if (objectif.getStatut() == ObjectifIndividuel.StatutObjectif.ATTEINT) {
                        throw new IllegalStateException("Impossible de remettre en cours un objectif atteint");
                    }
                    objectif.setStatut(ObjectifIndividuel.StatutObjectif.EN_COURS);
                    objectif.setDateAtteinte(null);
                    return objectifRepository.save(objectif);
                })
                .orElseThrow(() -> new RuntimeException("Objectif non trouvé avec l'ID: " + id));
    }

    public void supprimerObjectif(Long id) {
        ObjectifIndividuel objectif = objectifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Objectif non trouvé avec l'ID: " + id));

        // Vérifier que l'objectif peut être supprimé
        if (objectif.getStatut() == ObjectifIndividuel.StatutObjectif.ATTEINT) {
            throw new IllegalStateException("Impossible de supprimer un objectif atteint");
        }

        objectifRepository.deleteById(id);
    }

    // =====================================================
    // Queries spécialisées
    // =====================================================

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsJoueur(Long joueurId) {
        return objectifRepository.findByJoueurIdOrderByDateCreationDesc(joueurId);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsCoach(Long coachId) {
        return objectifRepository.findByCoachIdOrderByDateCreationDesc(coachId);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsParType(ObjectifIndividuel.TypeObjectif type) {
        return objectifRepository.findByTypeObjectifOrderByDateCreationDesc(type);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsParStatut(ObjectifIndividuel.StatutObjectif statut) {
        return objectifRepository.findByStatutOrderByDateCreationDesc(statut);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsEnCours(Long joueurId) {
        return objectifRepository.getObjectifsEnCours(joueurId);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsEchus() {
        return objectifRepository.getObjectifsEchus(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsProchesEcheance(int nombreJours) {
        LocalDate dateLimite = LocalDate.now().plusDays(nombreJours);
        return objectifRepository.getObjectifsProchesEcheance(LocalDate.now(), dateLimite);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsRecents(Long joueurId, int limite) {
        return objectifRepository.getObjectifsRecents(joueurId, limite);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsLesPlusAvances(Long joueurId, int limite) {
        return objectifRepository.getObjectifsLesPlusAvances(joueurId, limite);
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> rechercherObjectifs(String motCle) {
        return objectifRepository.rechercherParMotsCles(motCle);
    }

    // =====================================================
    // Statistiques et analyses
    // =====================================================

    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesObjectifsJoueur(Long joueurId) {
        return objectifRepository.getStatistiquesObjectifsJoueur(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getProgressionMoyenneParType(Long joueurId) {
        return objectifRepository.getProgressionMoyenneParType(joueurId);
    }

    @Transactional(readOnly = true)
    public Double getTauxReussiteObjectifs(Long joueurId, LocalDate dateDebut, LocalDate dateFin) {
        Double taux = objectifRepository.getTauxReussiteObjectifs(joueurId, dateDebut, dateFin);
        return taux != null ? taux : 0.0;
    }

    @Transactional(readOnly = true)
    public List<ObjectifIndividuel> getObjectifsCoachPeriode(Long coachId, LocalDate dateDebut, LocalDate dateFin) {
        return objectifRepository.getObjectifsCoachPeriode(coachId, dateDebut, dateFin);
    }

    // =====================================================
    // Analyses comportementales
    // =====================================================

    @Transactional(readOnly = true)
    public boolean estJoueurMotivé(Long joueurId) {
        List<ObjectifIndividuel> objectifsEnCours = objectifRepository.getObjectifsEnCours(joueurId);
        
        if (objectifsEnCours.isEmpty()) {
            return false;
        }

        // Calculer la progression moyenne
        BigDecimal progressionMoyenne = BigDecimal.valueOf(
            objectifsEnCours.stream()
                .mapToInt(ObjectifIndividuel::getProgression)
                .average()
                .orElse(0.0)
        );

        // Considérer comme motivé si progression moyenne > 30%
        return progressionMoyenne.compareTo(BigDecimal.valueOf(30)) > 0;
    }

    @Transactional(readOnly = true)
    public ObjectifIndividuel.TypeObjectif getTypeObjectifPrefere(Long joueurId) {
        List<Object[]> progressionParType = objectifRepository.getProgressionMoyenneParType(joueurId);
        
        if (progressionParType.isEmpty()) {
            return null;
        }

        // Retourner le type avec la meilleure progression moyenne
        Object[] meilleurType = progressionParType.stream()
                .max((a, b) -> ((BigDecimal) a[1]).compareTo((BigDecimal) b[1]))
                .orElse(null);

        return meilleurType != null ? (ObjectifIndividuel.TypeObjectif) meilleurType[0] : null;
    }

    // =====================================================
    // Gestion automatique
    // =====================================================

    public void verifierEcheances() {
        List<ObjectifIndividuel> objectifsEchus = getObjectifsEchus();
        
        for (ObjectifIndividuel objectif : objectifsEchus) {
            // Marquer comme échu si pas encore atteint
            if (objectif.getStatut() == ObjectifIndividuel.StatutObjectif.EN_COURS) {
                objectif.setStatut(ObjectifIndividuel.StatutObjectif.ECHU);
                objectifRepository.save(objectif);
            }
        }
    }

    public List<ObjectifIndividuel> genererRappelsEcheance(int nombreJours) {
        return getObjectifsProchesEcheance(nombreJours);
    }

    // =====================================================
    // Validation métier
    // =====================================================

    @Transactional(readOnly = true)
    public boolean peutEtreModifie(Long objectifId) {
        return objectifRepository.findById(objectifId)
                .map(objectif -> objectif.getStatut() == ObjectifIndividuel.StatutObjectif.EN_COURS ||
                               objectif.getStatut() == ObjectifIndividuel.StatutObjectif.ECHU)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean estEchu(Long objectifId) {
        return objectifRepository.findById(objectifId)
                .map(objectif -> objectif.getDateEcheance() != null && 
                               objectif.getDateEcheance().isBefore(LocalDate.now()))
                .orElse(false);
    }

    // =====================================================
    // Rapports
    // =====================================================

    @Transactional(readOnly = true)
    public int getNombreObjectifsJoueur(Long joueurId, ObjectifIndividuel.StatutObjectif statut) {
        return objectifRepository.findByJoueurIdAndStatutOrderByDateCreationDesc(joueurId, statut).size();
    }

    @Transactional(readOnly = true)
    public BigDecimal getProgressionMoyenneJoueur(Long joueurId) {
        List<ObjectifIndividuel> objectifs = objectifRepository.getObjectifsEnCours(joueurId);
        
        if (objectifs.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double moyenneProgression = objectifs.stream()
                .mapToInt(ObjectifIndividuel::getProgression)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(moyenneProgression);
    }
}
