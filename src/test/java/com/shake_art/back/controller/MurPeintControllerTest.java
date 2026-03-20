package com.shake_art.back.controller;

import com.shake_art.back.dto.ArtisteDto;
import com.shake_art.back.dto.MurPeintDto;
import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.ArtisteModel;
import com.shake_art.back.model.MurPeint;
import com.shake_art.back.repository.ArtisteRepository;
import com.shake_art.back.repository.MurPeintRepository;
import com.shake_art.back.service.ArtisteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class MurPeintControllerTest {

    @Mock
    private MurPeintRepository murPeintRepository;

    @Mock
    private ArtisteService artisteService;

    @Mock
    private ArtisteRepository artisteRepository;

    @InjectMocks
    private MurPeintController controller;

    @Test
    void getAll_sansFiltre_retourneDtos() {
        MurPeint mur = new MurPeint();
        mur.setId(1L);
        mur.setNom("Mur A");
        mur.setPhotoUrl("photos/fresques/a.jpg");
        when(murPeintRepository.findAll()).thenReturn(List.of(mur));

        List<MurPeintDto> result = controller.getAll(null);

        assertEquals(1, result.size());
        assertEquals("Mur A", result.get(0).getNom());
    }

    @Test
    void getMurPeint_retourneDetail() {
        MurPeint mur = new MurPeint();
        mur.setId(5L);
        mur.setNom("Mur B");
        mur.setPhotoUrl("photos/fresques/b.jpg");

        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(3L);
        mur.setArtiste(artiste);
        when(murPeintRepository.findByIdWithArtiste(5L)).thenReturn(Optional.of(mur));

        ArtisteDto artisteDto = new ArtisteDto();
        artisteDto.setId(3L);
        when(artisteService.toDto(artiste)).thenReturn(artisteDto);

        ResponseEntity<MurPeintDto> response = controller.getMurPeint(5L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Mur B", response.getBody().getNom());
    }

    @Test
    void create_associeArtisteEtRetourne201() {
        ArtisteModel artisteRef = new ArtisteModel();
        artisteRef.setId(2L);

        MurPeint payload = new MurPeint();
        payload.setNom("Mur C");
        payload.setArtiste(artisteRef);
        payload.setPhotoUrl("photos/fresques/c.jpg");

        when(artisteRepository.findById(2L)).thenReturn(Optional.of(artisteRef));
        when(murPeintRepository.save(any(MurPeint.class))).thenAnswer(invocation -> {
            MurPeint m = invocation.getArgument(0);
            m.setId(10L);
            return m;
        });

        ResponseEntity<MurPeintDto> response = controller.create(payload);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    void delete_lanceNotFound_siAbsent() {
        when(murPeintRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> controller.delete(99L));
    }

    @Test
    void uploadPhoto_rejetteFichierVide() {
        MockMultipartFile empty = new MockMultipartFile("file", "x.jpg", "image/jpeg", new byte[0]);

        assertThrows(BusinessException.class, () -> controller.uploadPhoto(empty));
    }

    @Test
    void uploadPhoto_retourneNomFichier() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "mur.jpg", "image/jpeg", "abc".getBytes());

        ResponseEntity<MurPeintController.UploadResponse> response = controller.uploadPhoto(file);

        assertEquals(200, response.getStatusCode().value());
        String filename = response.getBody().getFilename();

        Path generated = Path.of("uploads/photos/fresques", filename);
        Files.deleteIfExists(generated);
    }

    @Test
    void test_endpointTechnique() {
        assertEquals("API murs OK", controller.test());
    }

    @Test
    void validerPhotoDepuisGalerie_copieFichier() throws IOException {
        Path sourceDir = Path.of("uploads/photos/galerie");
        Files.createDirectories(sourceDir);
        Path fresquesDir = Path.of("uploads/photos/fresques");
        Files.createDirectories(fresquesDir);

        String filename = "copy-test.jpg";
        Path source = sourceDir.resolve(filename);
        Files.write(source, "abc".getBytes());

        ResponseEntity<String> response = controller.validerPhotoDepuisGalerie(1L, "\"" + filename + "\"");

        assertEquals(200, response.getStatusCode().value());
        Files.deleteIfExists(source);
        Files.deleteIfExists(fresquesDir.resolve(filename));
    }
}
