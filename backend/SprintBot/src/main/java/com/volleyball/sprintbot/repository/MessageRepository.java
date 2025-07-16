package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Message;
import com.volleyball.sprintbot.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Recherche par expéditeur
    List<Message> findByExpediteur(Utilisateur expediteur);

    // Recherche par destinataire
    List<Message> findByDestinataire(Utilisateur destinataire);

    // Recherche des messages non lus par destinataire
    List<Message> findByDestinataireAndLuFalse(Utilisateur destinataire);

    // Recherche des messages lus par destinataire
    List<Message> findByDestinataireAndLuTrue(Utilisateur destinataire);

    // Recherche par date d'envoi après
    List<Message> findByDateEnvoiAfter(LocalDateTime date);

    // Recherche par contenu contenant (insensible à la casse)
    List<Message> findByContenuContainingIgnoreCase(String contenu);

    // Compter les messages non lus par destinataire
    long countByDestinataireAndLuFalse(Utilisateur destinataire);

    // Compter les messages envoyés par expéditeur
    long countByExpediteur(Utilisateur expediteur);

    // Requête personnalisée pour obtenir la conversation entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE " +
           "(m.expediteur = :utilisateur1 AND m.destinataire = :utilisateur2) OR " +
           "(m.expediteur = :utilisateur2 AND m.destinataire = :utilisateur1) " +
           "ORDER BY m.dateEnvoi ASC")
    List<Message> findConversation(@Param("utilisateur1") Utilisateur utilisateur1, 
                                   @Param("utilisateur2") Utilisateur utilisateur2);

    // Requête personnalisée pour obtenir les messages récents (dernières 24h)
    @Query("SELECT m FROM Message m WHERE m.dateEnvoi >= :dateDebut ORDER BY m.dateEnvoi DESC")
    List<Message> findMessagesRecents(@Param("dateDebut") LocalDateTime dateDebut);

    // Requête personnalisée pour obtenir les derniers messages par destinataire
    @Query("SELECT m FROM Message m WHERE m.destinataire = :destinataire " +
           "ORDER BY m.dateEnvoi DESC")
    List<Message> findDerniersMessages(@Param("destinataire") Utilisateur destinataire);
}
