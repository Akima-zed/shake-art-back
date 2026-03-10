package com.shake_art.back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
public class ReservationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le type est obligatoire")
    private String type;        // atelier

    @NotBlank(message = "Le nom de l'activite est obligatoire")
    private String nom;         // nom activité

    @NotBlank(message = "L'heure est obligatoire")
    private String heure;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit etre valide")
    private String email;

    @Min(value = 1, message = "Le nombre de personnes doit etre superieur ou egal a 1")
    private int personnes;
    private String message;

    private boolean validee = false; // validation admin
    private boolean suspendue = false; // suspension admin


    public boolean isValidee() {
        return validee;
    }

    public void setValidee(boolean validee) {
        this.validee = validee;
    }

    public boolean isSuspendue() {
        return suspendue;
    }

    public void setSuspendue(boolean suspendue) {
        this.suspendue = suspendue;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }
}
