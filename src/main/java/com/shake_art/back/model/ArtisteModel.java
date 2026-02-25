package com.shake_art.back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "artistes")
public class ArtisteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String discipline;

    @Column(length = 2000)
    private String bio;

    @Enumerated(EnumType.STRING)  // Sauvegarde l’enum en clair dans la base
    private ArtisteType type;       // Nouveau champ pour le type d’artiste

    @OneToMany(mappedBy = "artiste", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "artiste", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "artiste-activites")
    private List<ActiviteModel> activites = new ArrayList<>();

    private Integer anneeArchive; // null = actif, sinon = année d'archive
    private String photoProfil;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public ArtisteType getType() { return type; }
    public void setType(ArtisteType type) { this.type = type; }
    public List<Photo> getPhotos() { return photos; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }
    public List<ActiviteModel> getActivites() { return activites; }
    public void setActivites(List<ActiviteModel> activites) { this.activites = activites; }
    public Integer getAnneeArchive() { return anneeArchive; }
    public void setAnneeArchive(Integer anneeArchive) { this.anneeArchive = anneeArchive; }
    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public String getNom() {
        return name;
    }

    public String getPrenom() {
        return discipline;
    }

    public void setNom(String nom) {
        this.name = nom;
    }

    public void setPrenom(String prenom) {
        this.discipline = prenom;
    }
}
