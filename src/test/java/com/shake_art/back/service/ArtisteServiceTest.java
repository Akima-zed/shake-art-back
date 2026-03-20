package com.shake_art.back.service;

import com.shake_art.back.dto.ArtisteDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.ArtisteModel;
import com.shake_art.back.model.ArtisteType;
import com.shake_art.back.model.Photo;
import com.shake_art.back.repository.ArtisteRepository;
import com.shake_art.back.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtisteServiceTest {

    @Mock
    private ArtisteRepository artisteRepository;

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private ArtisteService service;

    @Test
    void createArtiste_sansPhotos_sauvegardeEntite() throws IOException {
        when(artisteRepository.save(any(ArtisteModel.class))).thenAnswer(invocation -> {
            ArtisteModel model = invocation.getArgument(0);
            model.setId(42L);
            return model;
        });

        ArtisteModel created = service.createArtiste(
            "Alyx", "Graffiti", "Bio", "PAINTER", null, null
        );

        assertEquals(42L, created.getId());
        assertEquals("Alyx", created.getName());
        assertEquals(ArtisteType.PAINTER, created.getType());
        verify(photoRepository, never()).save(any(Photo.class));
    }

    @Test
    void updateArtiste_sansNouvellePhoto_metAJourChamps() throws IOException {
        ArtisteModel existing = new ArtisteModel();
        existing.setId(5L);
        existing.setName("Avant");
        existing.setDiscipline("Avant");
        existing.setBio("Avant");
        existing.setType(ArtisteType.PAINTER);

        when(artisteRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(artisteRepository.save(any(ArtisteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArtisteModel updated = service.updateArtiste(5L, "Apres", "Photo", "Nouvelle bio", "MUSICIAN", null);

        assertEquals("Apres", updated.getName());
        assertEquals("Photo", updated.getDiscipline());
        assertEquals("Nouvelle bio", updated.getBio());
        assertEquals(ArtisteType.MUSICIAN, updated.getType());
    }

    @Test
    void deleteArtiste_supprimePhotosEtEntite() {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(7L);
        artiste.setPhotoProfil("photos/profil/nonexistent.jpg");

        Photo p1 = new Photo();
        p1.setFilename("photos/galerie/7/nonexistent1.jpg");
        Photo p2 = new Photo();
        p2.setFilename("photos/galerie/7/nonexistent2.jpg");

        when(artisteRepository.findById(7L)).thenReturn(Optional.of(artiste));
        when(photoRepository.findByArtisteId(7L)).thenReturn(List.of(p1, p2));

        service.deleteArtiste(7L);

        verify(photoRepository).deleteAll(List.of(p1, p2));
        verify(artisteRepository).deleteById(7L);
    }

    @Test
    void addGalleryPhoto_enregistrePhotoEtRetourneEntite() throws IOException {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(11L);

        when(artisteRepository.findById(11L)).thenReturn(Optional.of(artiste));
        when(photoRepository.save(any(Photo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.png", "image/png", "abc".getBytes()
        );

        Photo saved = service.addGalleryPhoto(11L, file);

        assertNotNull(saved.getFilename());
        assertTrue(saved.getFilename().contains("galerie/11"));
        verify(photoRepository).save(any(Photo.class));

        Path generated = Path.of("uploads", saved.getFilename());
        Files.deleteIfExists(generated);
    }

    @Test
    void deleteGalleryPhoto_supprimeFichierEtRecord() {
        Photo photo = new Photo();
        photo.setFilename("photos/galerie/11/nonexistent.jpg");

        when(photoRepository.findById(99L)).thenReturn(Optional.of(photo));

        service.deleteGalleryPhoto(99L);

        verify(photoRepository).deleteById(99L);
    }

    @Test
    void getPhotoProfilData_lanceErreur_siPhotoAbsente() {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(1L);
        artiste.setPhotoProfil(null);
        when(artisteRepository.findById(1L)).thenReturn(Optional.of(artiste));

        assertThrows(ResourceNotFoundException.class, () -> service.getPhotoProfilData(1L));
    }

    @Test
    void toDto_mappeChampsEtUrls() {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(3L);
        artiste.setName("Nina");
        artiste.setDiscipline("Street art");
        artiste.setBio("Bio");
        artiste.setType(ArtisteType.SCULPTOR);
        artiste.setAnneeArchive(2025);
        artiste.setPhotoProfil("photos/profil/profil.png");

        Photo photo = new Photo();
        photo.setFilename("photos/galerie/3/a.png");
        artiste.setPhotos(List.of(photo));

        ArtisteDto dto = service.toDto(artiste);

        assertEquals("Nina", dto.getName());
        assertEquals("SCULPTOR", dto.getType());
        assertEquals("/uploads/photos/profil/profil.png", dto.getPhotoProfilUrl());
        assertEquals(1, dto.getGalleryUrls().size());
    }

    @Test
    void saveArtiste_etSave_persistentViaRepository() {
        ArtisteModel model = new ArtisteModel();
        when(artisteRepository.save(any(ArtisteModel.class))).thenReturn(model);

        service.saveArtiste(model);
        service.save(model);

        ArgumentCaptor<ArtisteModel> captor = ArgumentCaptor.forClass(ArtisteModel.class);
        verify(artisteRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertEquals(2, captor.getAllValues().size());
    }

    @Test
    void getArtisteById_lanceNpe_siIdNull() {
        assertThrows(NullPointerException.class, () -> service.getArtisteById(null));
        verify(artisteRepository, never()).findById(anyLong());
    }
}
