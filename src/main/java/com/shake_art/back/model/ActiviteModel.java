package com.shake_art.back.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
public class ActiviteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;         // Type d’activité : atelier, concert, animation, etc.
    private String name;         // Nom de l’activité
    private String heure;        // Heure de début ("HH:mm")
    private String description;  // Description de l’activité
    private String date;         // Date de l’activité
    private Boolean reservable = false; // Si l’activité est réservable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artiste_id")
    @JsonBackReference(value = "artiste-activites")
    private ArtisteModel artiste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    @JsonBackReference(value = "equipe-activites")
    private EquipeModel equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programmation_id")
    @JsonBackReference(value = "programmation-activites")
    private ProgrammationModel programmation;

    // Permet de lier automatiquement artiste ou équipe selon intervenantType
    @Transient
    private Long intervenantId;

    @Transient
    private String intervenantType;

    @PrePersist
    @PreUpdate
    public void assignIntervenantFields() {
        if (intervenantId != null && intervenantType != null) {
            switch (intervenantType.toLowerCase()) {
                case "artiste" -> {
                    this.artiste = new ArtisteModel();
                    this.artiste.setId(intervenantId);
                    this.equipe = null;
                }
                case "equipe" -> {
                    this.equipe = new EquipeModel();
                    this.equipe.setId(intervenantId);
                    this.artiste = null;
                }
                default -> {
                    this.artiste = null;
                    this.equipe = null;
                }
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHeure() { return heure; }
    public void setHeure(String heure) { this.heure = heure; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Boolean getReservable() { return reservable; }
    public void setReservable(Boolean reservable) { this.reservable = reservable; }
    public ArtisteModel getArtiste() { return artiste; }
    public void setArtiste(ArtisteModel artiste) { this.artiste = artiste; }
    public EquipeModel getEquipe() { return equipe; }
    public void setEquipe(EquipeModel equipe) { this.equipe = equipe; }
    public ProgrammationModel getProgrammation() { return programmation; }
    public void setProgrammation(ProgrammationModel programmation) { this.programmation = programmation; }
    public Long getIntervenantId() { return intervenantId; }
    public void setIntervenantId(Long intervenantId) { this.intervenantId = intervenantId; }
    public String getIntervenantType() { return intervenantType; }
    public void setIntervenantType(String intervenantType) { this.intervenantType = intervenantType; }
    public void setNom(String nom) {
        this.name = nom;
    }
}
