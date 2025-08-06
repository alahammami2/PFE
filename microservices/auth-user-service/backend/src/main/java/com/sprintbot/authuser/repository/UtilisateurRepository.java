package com.sprintbot.authuser.repository;

import com.sprintbot.authuser.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs
 * Fournit les opérations CRUD et les requêtes personnalisées
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    /**
     * Trouve un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<Utilisateur> findByEmail(String email);
    
    /**
     * Trouve tous les utilisateurs par rôle
     * @param role le rôle recherché
     * @return liste des utilisateurs avec ce rôle
     */
    List<Utilisateur> findByRole(String role);
    
    /**
     * Trouve tous les utilisateurs actifs
     * @return liste des utilisateurs actifs
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.actif = true")
    List<Utilisateur> findActiveUsers();
    
    /**
     * Trouve tous les utilisateurs inactifs
     * @return liste des utilisateurs inactifs
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.actif = false")
    List<Utilisateur> findInactiveUsers();
    
    /**
     * Recherche par nom ou prénom (insensible à la casse)
     * @param nom le nom à rechercher
     * @param prenom le prénom à rechercher
     * @return liste des utilisateurs correspondants
     */
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Utilisateur> findByNomOrPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
    
    /**
     * Compte le nombre d'utilisateurs par rôle
     * @param role le rôle à compter
     * @return nombre d'utilisateurs avec ce rôle
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    Long countByRole(@Param("role") String role);
    
    /**
     * Trouve les utilisateurs créés après une date donnée
     * @param date la date de référence
     * @return liste des utilisateurs créés après cette date
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.dateCreation >= :date")
    List<Utilisateur> findUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    /**
     * Trouve les utilisateurs connectés récemment
     * @param date la date de référence
     * @return liste des utilisateurs connectés après cette date
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.derniereConnexion >= :date")
    List<Utilisateur> findUsersLoggedInAfter(@Param("date") LocalDateTime date);
    
    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouve les utilisateurs par rôle et statut actif
     * @param role le rôle recherché
     * @param actif le statut actif
     * @return liste des utilisateurs correspondants
     */
    List<Utilisateur> findByRoleAndActif(String role, Boolean actif);
    
    /**
     * Recherche textuelle dans nom, prénom et email
     * @param searchTerm le terme de recherche
     * @return liste des utilisateurs correspondants
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Utilisateur> searchUsers(@Param("searchTerm") String searchTerm);
    
    /**
     * Trouve les utilisateurs par type (discriminator)
     * @param type le type d'utilisateur
     * @return liste des utilisateurs de ce type
     */
    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) = :type")
    List<Utilisateur> findByType(@Param("type") Class<? extends Utilisateur> type);
    
    /**
     * Statistiques des utilisateurs par rôle
     * @return liste des statistiques [role, count]
     */
    @Query("SELECT u.role, COUNT(u) FROM Utilisateur u GROUP BY u.role")
    List<Object[]> getUserStatsByRole();
    
    /**
     * Trouve les utilisateurs sans connexion récente
     * @param date la date limite
     * @return liste des utilisateurs inactifs
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.derniereConnexion IS NULL OR u.derniereConnexion < :date")
    List<Utilisateur> findInactiveUsersSince(@Param("date") LocalDateTime date);
}
