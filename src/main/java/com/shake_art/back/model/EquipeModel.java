package com.shake_art.back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import java.util.List;



@Entity
@Table(name = "equipe_members")
public class EquipeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String role;

    private String email;

    private String photoUrl;

    @Column(length = 1000)
    private String bio;

    @OneToMany(mappedBy = "equipe")
    @JsonManagedReference(value = "equipe-activites")
    private List<ActiviteModel> activites;

    public EquipeModel() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public java.util.List<ActiviteModel> getActivites() { return activites; }
    public void setActivites(java.util.List<ActiviteModel> activites) { this.activites = activites; }

    // Pour compatibilité avec certains contrôleurs
    public String getNom() { return fullName; }
    public void setNom(String nom) { this.fullName = nom; }

}
