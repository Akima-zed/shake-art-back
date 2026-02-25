package com.shake_art.back.mapper;

import com.shake_art.back.dto.EquipeDto;
import com.shake_art.back.model.EquipeModel;

public class EquipeMapper {

    public static EquipeDto toDto(EquipeModel model) {
        EquipeDto dto = new EquipeDto();
        dto.setId(model.getId());
        dto.setNom(model.getNom());
        dto.setRole(model.getRole());
        return dto;
    }

    public static EquipeModel toModel(EquipeDto dto) {
        EquipeModel model = new EquipeModel();
        model.setId(dto.getId());
        model.setNom(dto.getNom());
        model.setRole(dto.getRole());
        return model;
    }
}