package com.shake_art.back.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Equipe", description = "Membre de l'equipe du festival")
public class EquipeDto {
    private Long id;
    private String nom;
    private String role;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}