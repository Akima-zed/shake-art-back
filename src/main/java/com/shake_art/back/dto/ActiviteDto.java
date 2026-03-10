package com.shake_art.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiviteDto {
    private Long id;

    @NotBlank(message = "Le type de l'activite est obligatoire")
    private String type;

    @NotBlank(message = "Le nom de l'activite est obligatoire")
    private String name;
    private String description;
    private String heure;

    @NotNull(message = "Le champ reservable est obligatoire")
    private Boolean reservable;
    private Long artisteId;
    private Long equipeId;
    private Long intervenantId;
    private String intervenantType; // "artiste" ou "equipe"
    private String date;

    public String getNom() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Boolean getReservable() {
        return reservable;
    }

    public void setReservable(Boolean reservable) {
        this.reservable = reservable;
    }

    public Long getIntervenantId() {
        return intervenantId;
    }

    public void setIntervenantId(Long intervenantId) {
        this.intervenantId = intervenantId;
    }

    public String getIntervenantType() {
        return intervenantType;
    }

    public void setIntervenantType(String intervenantType) {
        this.intervenantType = intervenantType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setArtisteId(Long artisteId) {
        this.artisteId = artisteId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }
}
