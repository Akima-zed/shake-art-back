package com.shake_art.back.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artiste_id")
    @JsonBackReference
    private ArtisteModel artiste;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setArtiste(ArtisteModel artiste) {
        this.artiste = artiste;
    }
}
