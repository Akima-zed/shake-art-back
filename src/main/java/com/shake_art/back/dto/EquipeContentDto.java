package com.shake_art.back.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour l'exposition publique du contenu de la page Equipe.
 * Limite l'acces aux champs necessaires et masque la structure interne de l'entite.
 */
@Getter
@Setter
@Schema(name = "EquipeContent", description = "Contenu de la page equipe (texte + banniere)")
public class EquipeContentDto {
    @Schema(description = "Identifiant unique du contenu", example = "1")
    private Long id;
    
    @Schema(description = "Texte de presentation de l'equipe", example = "Nous sommes une equipe de passionnes...")
    private String presentationText;
    
    @Schema(description = "URL ou chemin de la banniere de la page equipe", example = "/uploads/equipe/banners/banner-2025.jpg")
    private String bannerImageUrl;
}
