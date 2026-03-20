package com.shake_art.back.controller;

import com.shake_art.back.dto.ActiviteDto;
import com.shake_art.back.dto.ProgrammationDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ProgrammationModel;
import com.shake_art.back.repository.ActiviteRepository;
import com.shake_art.back.repository.ProgrammationRepository;
import com.shake_art.back.service.ProgrammationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgrammationControllerTest {

    @Mock
    private ProgrammationService service;

    @Mock
    private ProgrammationRepository programmationRepository;

    @Mock
    private ActiviteRepository activiteRepository;

    @InjectMocks
    private ProgrammationController controller;

    @Test
    void getAll_retourneDtosService() {
        ProgrammationDto dto = new ProgrammationDto();
        dto.setId(1L);
        when(service.getAllDto()).thenReturn(List.of(dto));

        List<ProgrammationDto> result = controller.getAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getById_retourneDto() {
        ProgrammationModel model = new ProgrammationModel();
        model.setId(1L);
        model.setDate("2026-07-01");
        model.setAnnee(2026);

        ActiviteModel activite = new ActiviteModel();
        activite.setId(2L);
        activite.setName("Concert");
        activite.setType("concert");
        model.setActivites(List.of(activite));

        when(service.getById(1L)).thenReturn(model);

        ResponseEntity<ProgrammationDto> response = controller.getById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("2026-07-01", response.getBody().getDate());
        assertEquals(1, response.getBody().getActivites().size());
    }

    @Test
    void getById_lanceNotFound_siAbsent() {
        when(service.getById(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> controller.getById(99L));
    }

    @Test
    void create_persisteEtRetourne201() {
        ProgrammationDto input = new ProgrammationDto();
        input.setDate("2026-08-01");
        input.setAnnee(2026);

        ActiviteDto activiteDto = new ActiviteDto();
        activiteDto.setName("Atelier");
        activiteDto.setType("atelier");
        input.setActivites(List.of(activiteDto));

        ProgrammationModel saved = new ProgrammationModel();
        saved.setId(10L);
        saved.setDate("2026-08-01");
        saved.setAnnee(2026);
        ActiviteModel activiteModel = new ActiviteModel();
        activiteModel.setId(77L);
        activiteModel.setName("Atelier");
        activiteModel.setType("atelier");
        saved.setActivites(List.of(activiteModel));

        when(service.save(any(ProgrammationModel.class))).thenReturn(saved);

        ResponseEntity<ProgrammationDto> response = controller.create(input);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    void update_lanceNotFound_siAbsent() {
        when(service.getById(3L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> controller.update(3L, new ProgrammationDto()));
    }

    @Test
    void deleteActivite_supprimeActiviteSiPresente() {
        ProgrammationModel prog = new ProgrammationModel();
        prog.setId(1L);
        ActiviteModel a = new ActiviteModel();
        a.setId(5L);
        prog.setActivites(new java.util.ArrayList<>(List.of(a)));

        when(programmationRepository.findById((Long) 1L)).thenReturn(Optional.of(prog));

        ResponseEntity<Void> response = controller.deleteActivite(1L, 5L);

        assertEquals(204, response.getStatusCode().value());
        verify(programmationRepository).save(prog);
        verify(activiteRepository).deleteById(5L);
    }

    @Test
    void updateActivite_metAJourEtSauvegarde() {
        ActiviteModel existing = new ActiviteModel();
        existing.setId(8L);

        ActiviteDto dto = new ActiviteDto();
        dto.setName("New Name");
        dto.setType("atelier");
        dto.setHeure("10:00");
        dto.setReservable(true);

        when(activiteRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(activiteRepository.save(any(ActiviteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActiviteModel saved = controller.updateActivite(8L, dto);

        assertEquals("New Name", saved.getName());
        assertEquals("atelier", saved.getType());
    }
}
