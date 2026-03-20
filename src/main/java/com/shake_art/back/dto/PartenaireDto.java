package com.shake_art.back.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour l'exposition publique du modele Partenaire.
 * Masque les champs internes et expose uniquement les donnes necessaires au client.
 */
@Getter
@Setter
@Schema(name = "Partenaire", description = "Informations publiques d'un partenaire")
public class PartenaireDto {
    @Schema(description = "Identifiant unique du partenaire", example = "1")
    private Long id;
    
    @Schema(description = "Nom du partenaire", example = "Societe XYZ")
    private String nom;
    
    @Schema(description = "Site web du partenaire", example = "https://www.partenaire.com")
    private String siteWeb;
    
    @Schema(description = "Description detaillee du partenaire", example = "Partenaire de soutien pour l'edition 2025")
    private String description;
    
    @Schema(description = "URL ou chemin du logo du partenaire", example = "/uploads/logos/partenaire-2025.png")
    private String logo;
    
    @Schema(description = "Type de partenariat", example = "SPONSOR_OR")
    private String typePartenaire;
}
