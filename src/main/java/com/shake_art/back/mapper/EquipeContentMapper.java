package com.shake_art.back.mapper;

import com.shake_art.back.dto.EquipeContentDto;
import com.shake_art.back.model.EquipeContent;

public class EquipeContentMapper {

    public static EquipeContentDto toDto(EquipeContent model) {
        EquipeContentDto dto = new EquipeContentDto();
        dto.setId(model.getId());
        dto.setPresentationText(model.getPresentationText());
        dto.setBannerImageUrl(model.getBannerImageUrl());
        return dto;
    }

    public static EquipeContent toModel(EquipeContentDto dto) {
        EquipeContent model = new EquipeContent();
        model.setId(dto.getId());
        model.setPresentationText(dto.getPresentationText());
        model.setBannerImageUrl(dto.getBannerImageUrl());
        return model;
    }
}
