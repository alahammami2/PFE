package com.volleyball.sprintbot.repository;

import com.volleyball.sprintbot.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    List<Utilisateur> findByRole(String role);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.nom LIKE %:nom% OR u.prenom LIKE %:prenom%")
    List<Utilisateur> findByNomOrPrenom(@Param("nom") String nom, @Param("prenom") String prenom);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.actif = true")
    List<Utilisateur> findActiveUsers();
    
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    Long countByRole(@Param("role") String role);
}

