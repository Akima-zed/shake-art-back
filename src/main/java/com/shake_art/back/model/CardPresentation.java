package com.shake_art.back.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accueil_cards")
public class CardPresentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String image;
    private String link;
    @ManyToOne
    @JoinColumn(name = "accueil_id", nullable = false)
    @JsonBackReference
    private AccueilContent accueilContent;

    public CardPresentation(String title, String description, String image, String link) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAccueilContent(AccueilContent accueilContent) {
        this.accueilContent = accueilContent;
    }

    public Long getId() {
        return id;
    }
}
