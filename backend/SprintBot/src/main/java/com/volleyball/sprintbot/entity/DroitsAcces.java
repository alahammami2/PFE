package com.volleyball.sprintbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "droits_acces")
public class DroitsAcces {
    
    @Id
    @Column(name = "id_droit", length = 100)
    private String idDroit;
    
    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissions;
    
    @Column(name = "id_admin", length = 100)
    private String idAdmin;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}

