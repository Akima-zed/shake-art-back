package com.shake_art.back.mapper;

import com.shake_art.back.model.ArtisteType;
import com.shake_art.back.dto.ArtisteDto;
import com.shake_art.back.model.ArtisteModel;

public class ArtisteMapper {

    public static ArtisteDto toDto(ArtisteModel model) {
        ArtisteDto dto = new ArtisteDto();
        dto.setId(model.getId());
        dto.setNom(model.getNom());
        dto.setPrenom(model.getPrenom());
        dto.setType(model.getType() != null ? model.getType().name() : null);
        return dto;
    }

    public static ArtisteModel toModel(ArtisteDto dto) {
        ArtisteModel model = new ArtisteModel();
        model.setId(dto.getId());
        model.setNom(dto.getNom());
        model.setPrenom(dto.getPrenom());
        model.setType(dto.getType() != null ? ArtisteType.valueOf(dto.getType()) : null);
        return model;
    }
}