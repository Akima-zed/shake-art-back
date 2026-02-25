package com.shake_art.back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.shake_art.back.model.CardPresentation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccueilContent {
    @Id
    private Long id;
    private String heroTitle;
    private String heroSubtitle;
    private String heroVideoUrl;
    @Column(length = 312)
    @Size(max = 312, message = "Le texte de présentation ne doit pas dépasser 312 caractères.")
    private String presentationText;
    @OneToMany(mappedBy = "accueilContent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CardPresentation> cards;

    public List<CardPresentation> getCards() {
        return cards;
    }

    public void setCards(List<CardPresentation> cards) {
        this.cards = cards;
    }

    public void setPresentationText(String presentationText) {
        this.presentationText = presentationText;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHeroTitle(String heroTitle) {
        this.heroTitle = heroTitle;
    }

    public void setHeroSubtitle(String heroSubtitle) {
        this.heroSubtitle = heroSubtitle;
    }

    public void setHeroVideoUrl(String heroVideoUrl) {
        this.heroVideoUrl = heroVideoUrl;
    }
}
