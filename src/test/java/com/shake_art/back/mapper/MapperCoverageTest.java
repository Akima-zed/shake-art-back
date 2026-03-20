package com.shake_art.back.mapper;

import com.shake_art.back.dto.ActiviteDto;
import com.shake_art.back.dto.ArtisteDto;
import com.shake_art.back.dto.EquipeContentDto;
import com.shake_art.back.dto.EquipeDto;
import com.shake_art.back.dto.PartenaireDto;
import com.shake_art.back.dto.ProgrammationDto;
import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ArtisteModel;
import com.shake_art.back.model.ArtisteType;
import com.shake_art.back.model.EquipeContent;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.model.Partenaire;
import com.shake_art.back.model.ProgrammationModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperCoverageTest {

    @Test
    void programmationMapper_convertitDansLesDeuxSens() {
        ActiviteModel activite = new ActiviteModel();
        activite.setId(1L);
        activite.setType("atelier");
        activite.setName("Name");
        activite.setHeure("10:00");
        activite.setReservable(true);
        activite.setIntervenantId(5L);
        activite.setIntervenantType("artiste");

        ProgrammationModel model = new ProgrammationModel();
        model.setId(3L);
        model.setDate("2026-07-01");
        model.setAnnee(2026);
        model.setActivites(List.of(activite));

        ProgrammationDto dto = ProgrammationMapper.toDto(model);
        assertEquals(3L, dto.getId());
        assertEquals(1, dto.getActivites().size());

        ProgrammationModel back = ProgrammationMapper.toModel(dto);
        assertEquals("2026-07-01", back.getDate());
        assertEquals(1, back.getActivites().size());
    }

    @Test
    void artisteMapper_convertitDansLesDeuxSens() {
        ArtisteModel model = new ArtisteModel();
        model.setId(8L);
        model.setNom("Nom");
        model.setPrenom("Prenom");
        model.setType(ArtisteType.PAINTER);

        ArtisteDto dto = ArtisteMapper.toDto(model);
        assertEquals("Nom", dto.getNom());

        ArtisteModel back = ArtisteMapper.toModel(dto);
        assertEquals(ArtisteType.PAINTER, back.getType());
    }

    @Test
    void partenaireMapper_convertitDansLesDeuxSens() {
        Partenaire model = new Partenaire();
        model.setId(9L);
        model.setNom("Nom");
        model.setDescription("Desc");
        model.setSiteWeb("site");
        model.setLogo("logo");

        PartenaireDto dto = PartenaireMapper.toDto(model);
        assertEquals("Nom", dto.getNom());

        Partenaire back = PartenaireMapper.toModel(dto);
        assertEquals("Desc", back.getDescription());
    }

    @Test
    void equipeMapper_convertitDansLesDeuxSens() {
        EquipeModel model = new EquipeModel();
        model.setId(2L);
        model.setNom("Team");
        model.setRole("Role");

        EquipeDto dto = EquipeMapper.toDto(model);
        assertEquals("Team", dto.getNom());

        EquipeModel back = EquipeMapper.toModel(dto);
        assertEquals("Role", back.getRole());
    }

    @Test
    void equipeContentMapper_convertitDansLesDeuxSens() {
        EquipeContent model = new EquipeContent();
        model.setId(4L);
        model.setPresentationText("Texte");
        model.setBannerImageUrl("banner.jpg");

        EquipeContentDto dto = EquipeContentMapper.toDto(model);
        assertEquals("Texte", dto.getPresentationText());

        EquipeContent back = EquipeContentMapper.toModel(dto);
        assertEquals("banner.jpg", back.getBannerImageUrl());
    }

    @Test
    void activiteModel_assignIntervenantFields_couvreSwitch() {
        ActiviteModel activite = new ActiviteModel();
        activite.setIntervenantId(10L);
        activite.setIntervenantType("artiste");
        activite.assignIntervenantFields();
        assertEquals(10L, activite.getArtiste().getId());

        activite.setIntervenantType("equipe");
        activite.assignIntervenantFields();
        assertEquals(10L, activite.getEquipe().getId());

        activite.setIntervenantType("other");
        activite.assignIntervenantFields();
        assertEquals(null, activite.getArtiste());
    }

    @Test
    void programmationMapper_toModel_couvreChampsActiviteDto() {
        ActiviteDto activiteDto = new ActiviteDto();
        activiteDto.setName("Nom Legacy");
        activiteDto.setDescription("Desc");
        activiteDto.setDate("2026-07-02");

        ProgrammationDto dto = new ProgrammationDto();
        dto.setDate("2026-07-02");
        dto.setAnnee(2026);
        dto.setActivites(List.of(activiteDto));

        ProgrammationModel model = ProgrammationMapper.toModel(dto);

        assertEquals("Nom Legacy", model.getActivites().get(0).getName());
        assertEquals("Desc", model.getActivites().get(0).getDescription());
    }
}
