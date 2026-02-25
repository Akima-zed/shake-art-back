package com.shake_art.back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PartenaireContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    @Column(length = 3000)
    private String texte;

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getTitre() {
        return this.titre;
    }

    public String getTexte() {
        return this.texte;
    }
}
