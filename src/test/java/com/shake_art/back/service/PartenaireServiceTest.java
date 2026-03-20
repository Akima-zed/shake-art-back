package com.shake_art.back.service;

import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.Partenaire;
import com.shake_art.back.model.PartenaireContent;
import com.shake_art.back.repository.PartenaireContentRepository;
import com.shake_art.back.repository.PartenaireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PartenaireServiceTest {

    @Mock
    private PartenaireRepository partenaireRepository;

    @Mock
    private PartenaireContentRepository partenaireContentRepository;

    @InjectMocks
    private PartenaireService service;

    @Test
    void create_sansLogo_persistePartenaire() throws IOException {
        when(partenaireRepository.save(any(Partenaire.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Partenaire saved = service.create("Nom", "Desc", "https://example.com", null);

        assertEquals("Nom", saved.getNom());
        assertEquals("Desc", saved.getDescription());
        assertEquals("https://example.com", saved.getSiteWeb());
    }

    @Test
    void create_avecLogoImage_valideSauvegardeLogo() throws IOException {
        when(partenaireRepository.save(any(Partenaire.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", "img".getBytes());

        Partenaire saved = service.create("Nom", "Desc", "https://example.com", logo);

        assertTrue(saved.getLogo().startsWith("logos/"));

        Path generated = Path.of("uploads", saved.getLogo());
        Files.deleteIfExists(generated);
    }

    @Test
    void create_avecLogoNonImage_lanceBusinessException() {
        MockMultipartFile bad = new MockMultipartFile("logo", "logo.txt", "text/plain", "abc".getBytes());

        assertThrows(BusinessException.class, () -> service.create("Nom", "Desc", null, bad));
    }

    @Test
    void update_lanceNotFound_siPartenaireAbsent() {
        when(partenaireRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> service.update(10L, "Nom", "Desc", null, null));
    }

    @Test
    void delete_supprimeEntite() {
        Partenaire partenaire = new Partenaire();
        partenaire.setNom("P");
        when(partenaireRepository.findById(2L)).thenReturn(Optional.of(partenaire));

        service.delete(2L);

        verify(partenaireRepository).deleteById(2L);
    }

    @Test
    void delete_avecLogo_tenteSuppressionFichierPuisEntite() {
        Partenaire partenaire = new Partenaire();
        partenaire.setNom("P");
        partenaire.setLogo("logos/missing.jpg");
        when(partenaireRepository.findById(4L)).thenReturn(Optional.of(partenaire));

        service.delete(4L);

        verify(partenaireRepository).deleteById(4L);
    }

    @Test
    void update_avecLogoMetAJour_cheminLogo() throws IOException {
        Partenaire existing = new Partenaire();
        existing.setNom("Old");
        existing.setDescription("Old");
        existing.setLogo(null);

        when(partenaireRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(partenaireRepository.save(any(Partenaire.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile logo = new MockMultipartFile("logo", "new.png", "image/png", "img".getBytes());

        Partenaire updated = service.update(8L, "New", "Desc", "https://new.example", logo);

        assertEquals("New", updated.getNom());
        assertTrue(updated.getLogo().startsWith("logos/"));
        Files.deleteIfExists(Path.of("uploads", updated.getLogo()));
    }

    @Test
    void getAll_etGetOne_relayantRepository() {
        Partenaire partenaire = new Partenaire();
        when(partenaireRepository.findAll()).thenReturn(List.of(partenaire));
        when(partenaireRepository.findById(1L)).thenReturn(Optional.of(partenaire));

        assertEquals(1, service.getAll().size());
        assertTrue(service.getOne(1L).isPresent());
    }

    @Test
    void getOne_lanceNpe_siIdNull() {
        assertThrows(NullPointerException.class, () -> service.getOne(null));
        verify(partenaireRepository, never()).findById(any());
    }

    @Test
    void getContent_creeValeurDefaut_siAbsent() {
        when(partenaireContentRepository.findAll()).thenReturn(List.of());
        when(partenaireContentRepository.save(any(PartenaireContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartenaireContent content = service.getContent();

        assertEquals("Nos partenaires", content.getTitre());
    }

    @Test
    void updateContent_metAJourValeurs() {
        PartenaireContent existing = new PartenaireContent();
        existing.setTitre("Avant");
        existing.setTexte("Avant");

        PartenaireContent updated = new PartenaireContent();
        updated.setTitre("Apres");
        updated.setTexte("Texte apres");

        when(partenaireContentRepository.findAll()).thenReturn(List.of(existing));
        when(partenaireContentRepository.save(any(PartenaireContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartenaireContent saved = service.updateContent(updated);

        assertEquals("Apres", saved.getTitre());
        assertEquals("Texte apres", saved.getTexte());
    }
}
