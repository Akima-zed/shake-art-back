package com.shake_art.back.mapper;

import java.util.stream.Collectors;

import com.shake_art.back.dto.ActiviteDto;
import com.shake_art.back.dto.ProgrammationDto;
import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ProgrammationModel;

public class ProgrammationMapper {

    public static ProgrammationDto toDto(ProgrammationModel model) {
        ProgrammationDto dto = new ProgrammationDto();
        dto.setId(model.getId());
        dto.setDate(model.getDate());
        dto.setAnnee(model.getAnnee());
        dto.setActivites(model.getActivites().stream()
                .map(ProgrammationMapper::toDtoActivite)
                .collect(Collectors.toList()));
        return dto;
    }

    public static ProgrammationModel toModel(ProgrammationDto dto) {
        ProgrammationModel model = new ProgrammationModel();
        model.setId(dto.getId());
        model.setDate(dto.getDate());
        model.setAnnee(dto.getAnnee());
        model.setActivites(dto.getActivites().stream()
                .map(ProgrammationMapper::toModelActivite)
                .collect(Collectors.toList()));
        return model;
    }

    private static ActiviteDto toDtoActivite(ActiviteModel model) {
        ActiviteDto dto = new ActiviteDto();
        dto.setId(model.getId());
        dto.setType(model.getType());
        dto.setName(model.getName());
        dto.setHeure(model.getHeure());
        dto.setReservable(model.getReservable());
        dto.setIntervenantId(model.getIntervenantId());
        dto.setIntervenantType(model.getIntervenantType());
        return dto;
    }

    private static ActiviteModel toModelActivite(ActiviteDto dto) {
        ActiviteModel model = new ActiviteModel();
        model.setNom(dto.getNom());
        model.setDescription(dto.getDescription());
        model.setDate(dto.getDate());
        return model;
    }
}