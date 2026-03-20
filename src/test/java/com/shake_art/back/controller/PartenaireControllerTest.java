package com.shake_art.back.controller;

import com.shake_art.back.dto.PartenaireDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.Partenaire;
import com.shake_art.back.model.PartenaireContent;
import com.shake_art.back.service.PartenaireService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartenaireControllerTest {

    @Mock
    private PartenaireService partenaireService;

    @InjectMocks
    private PartenaireController controller;

    @Test
    void create_retourne201() throws IOException {
        Partenaire partenaire = new Partenaire();
        partenaire.setNom("P");
        when(partenaireService.create("P", "D", "https://p.com", null)).thenReturn(partenaire);

        ResponseEntity<PartenaireDto> response = controller.create("P", "D", "https://p.com", null);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("P", response.getBody().getNom());
    }

    @Test
    void getAll_retourneListeDtos() {
        when(partenaireService.getAll()).thenReturn(List.of(new Partenaire(), new Partenaire()));

        ResponseEntity<List<PartenaireDto>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getOne_lanceNotFound_siAbsent() {
        when(partenaireService.getOne(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controller.getOne(7L));
    }

    @Test
    void update_retourneDto() throws IOException {
        Partenaire partenaire = new Partenaire();
        partenaire.setNom("New");
        when(partenaireService.update(1L, "New", "Desc", "site", null)).thenReturn(partenaire);

        ResponseEntity<PartenaireDto> response = controller.update(1L, "New", "Desc", "site", null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("New", response.getBody().getNom());
    }

    @Test
    void delete_retourne204() {
        ResponseEntity<Void> response = controller.delete(3L);

        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void content_endpoints_retournentValeursService() {
        PartenaireContent content = new PartenaireContent();
        content.setTitre("Titre");

        when(partenaireService.getContent()).thenReturn(content);
        when(partenaireService.updateContent(any(PartenaireContent.class))).thenReturn(content);

        assertEquals("Titre", controller.getContent().getBody().getTitre());
        assertEquals("Titre", controller.updateContent(new PartenaireContent()).getBody().getTitre());
    }
}
