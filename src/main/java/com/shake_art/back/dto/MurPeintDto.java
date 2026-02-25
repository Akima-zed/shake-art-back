package com.shake_art.back.dto;

import lombok.Data;

@Data
public class MurPeintDto {
    private Long id;
    private String nom;
    private double latitude;
    private double longitude;
    private String description;
    private String photoUrl;
    private Integer annee;
    private ArtisteDto artiste;  // ⚡ Inclure les données de l'artiste

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public ArtisteDto getArtiste() {
        return artiste;
    }

    public void setArtiste(ArtisteDto artiste) {
        this.artiste = artiste;
    }

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
