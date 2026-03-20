package com.shake_art.back.mapper;

import com.shake_art.back.dto.PartenaireDto;
import com.shake_art.back.model.Partenaire;

public class PartenaireMapper {

    public static PartenaireDto toDto(Partenaire model) {
        PartenaireDto dto = new PartenaireDto();
        dto.setId(model.getId());
        dto.setNom(model.getNom());
        dto.setSiteWeb(model.getSiteWeb());
        dto.setDescription(model.getDescription());
        dto.setLogo(model.getLogo());
        dto.setTypePartenaire(model.getTypePartenaire());
        return dto;
    }

    public static Partenaire toModel(PartenaireDto dto) {
        Partenaire model = new Partenaire();
        model.setId(dto.getId());
        model.setNom(dto.getNom());
        model.setSiteWeb(dto.getSiteWeb());
        model.setDescription(dto.getDescription());
        model.setLogo(dto.getLogo());
        model.setTypePartenaire(dto.getTypePartenaire());
        return model;
    }
}
