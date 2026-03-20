package com.shake_art.back.controller;

import com.shake_art.back.dto.EquipeDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.service.EquipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipeControllerTest {

    @Mock
    private EquipeService service;

    @InjectMocks
    private EquipeController controller;

    @Test
    void getById_retourneDto() {
        EquipeModel model = new EquipeModel();
        model.setId(1L);
        model.setNom("Team");
        model.setRole("orga");
        when(service.getById(1L)).thenReturn(model);

        ResponseEntity<EquipeDto> response = controller.getById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Team", response.getBody().getNom());
    }

    @Test
    void getById_lanceNotFound_siAbsent() {
        when(service.getById(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> controller.getById(99L));
    }

    @Test
    void create_retourne201() {
        EquipeModel saved = new EquipeModel();
        saved.setId(2L);
        saved.setNom("Nouveau");
        saved.setRole("role");
        when(service.save(any(EquipeModel.class))).thenReturn(saved);

        EquipeDto dto = new EquipeDto();
        dto.setNom("Nouveau");
        dto.setRole("role");

        ResponseEntity<EquipeDto> response = controller.create(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Nouveau", response.getBody().getNom());
    }

    @Test
    void update_metAJourEquipe() {
        EquipeModel existing = new EquipeModel();
        existing.setId(1L);
        existing.setNom("Avant");
        existing.setRole("avant");

        when(service.getById(1L)).thenReturn(existing);
        when(service.save(any(EquipeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquipeDto dto = new EquipeDto();
        dto.setNom("Apres");
        dto.setRole("apres");

        ResponseEntity<EquipeDto> response = controller.update(1L, dto);

        assertEquals("Apres", response.getBody().getNom());
        assertEquals("apres", response.getBody().getRole());
    }
}
