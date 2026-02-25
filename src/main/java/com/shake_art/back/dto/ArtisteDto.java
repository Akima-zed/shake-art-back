package com.shake_art.back.dto;

import java.util.List;

public class ArtisteDto {
    private Long id;
    private String name;
    private String discipline;
    private String bio;
    private String type;
    private String photoProfilUrl;
    private Integer anneeArchive;
    private List<String> galleryUrls;
    private String prenom;
    private String nom;

    // Getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getNom() {
        return name;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotoProfilUrl() {
        return photoProfilUrl;
    }

    public void setPhotoProfilUrl(String photoProfilUrl) {
        this.photoProfilUrl = photoProfilUrl;
    }

    public Integer getAnneeArchive() {
        return anneeArchive;
    }

    public void setAnneeArchive(Integer anneeArchive) {
        this.anneeArchive = anneeArchive;
    }

    public List<String> getGalleryUrls() {
        return galleryUrls;
    }

    public void setGalleryUrls(List<String> galleryUrls) {
        this.galleryUrls = galleryUrls;
    }
}