package com.shake_art.back.controller;

import com.shake_art.back.dto.EquipeContentDto;
import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.EquipeContent;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.service.EquipeContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipeContentControllerTest {

    @Mock
    private EquipeContentService service;

    @InjectMocks
    private EquipeContentController controller;

    @Test
    void getContent_retourneDto() {
        EquipeContent content = new EquipeContent();
        content.setId(1L);
        content.setPresentationText("Texte");
        when(service.getContent()).thenReturn(Optional.of(content));

        ResponseEntity<EquipeContentDto> response = controller.getContent();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Texte", response.getBody().getPresentationText());
    }

    @Test
    void updateContent_lanceBusiness_siIdAbsent() {
        EquipeContentDto dto = new EquipeContentDto();

        assertThrows(BusinessException.class, () -> controller.updateContent(dto));
    }

    @Test
    void deleteContent_retourne204_siSupprime() {
        when(service.deleteById(2L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteContent(2L);

        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void deleteContent_lanceNotFound_siAbsent() {
        when(service.deleteById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> controller.deleteContent(2L));
    }

    @Test
    void addMember_retourne201() {
        EquipeModel member = new EquipeModel();
        member.setId(1L);
        when(service.addMember(any(EquipeModel.class))).thenReturn(member);

        ResponseEntity<EquipeModel> response = controller.addMember(member);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void updateMember_lanceBusiness_siIdMismatch() {
        EquipeModel member = new EquipeModel();
        member.setId(10L);

        assertThrows(BusinessException.class, () -> controller.updateMember(11L, member));
    }

    @Test
    void getMembers_retourneListe() {
        when(service.getMembers()).thenReturn(List.of(new EquipeModel(), new EquipeModel()));

        ResponseEntity<List<EquipeModel>> response = controller.getMembers();

        assertEquals(2, response.getBody().size());
    }

    @Test
    void uploadBanner_retourneUrl() throws IOException {
        when(service.uploadBannerImage(any())).thenReturn("/api/equipe/images/banner/x.jpg");
        MockMultipartFile file = new MockMultipartFile("file", "x.jpg", "image/jpeg", "abc".getBytes());

        ResponseEntity<?> response = controller.uploadBanner(file);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getBannerImage_lanceNotFound_siInexistante() {
        assertThrows(ResourceNotFoundException.class, () -> controller.getBannerImage("absent.jpg"));
    }

    @Test
    void getBannerImage_retourneRessource_siFichierPresent() throws IOException {
        Path dir = Path.of("uploads/equipe/banners");
        Files.createDirectories(dir);
        Path file = dir.resolve("test-banner.jpg");
        Files.write(file, "abc".getBytes());

        ResponseEntity<Resource> response = controller.getBannerImage("test-banner.jpg");

        assertEquals(200, response.getStatusCode().value());
        Files.deleteIfExists(file);
    }

    @Test
    void deleteMemberPhoto_retourne204() throws IOException {
        ResponseEntity<Void> response = controller.deleteMemberPhoto(8L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).deleteMemberPhoto(8L);
    }
}
