package com.shake_art.back.service;

import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.AccueilContent;
import com.shake_art.back.model.CardPresentation;
import com.shake_art.back.repository.AccueilContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccueilContentServiceTest {

    @Mock
    private AccueilContentRepository repository;

    @InjectMocks
    private AccueilContentService service;

    @Test
    void getContent_creeContenuDefaut_siAbsent() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(repository.save(any(AccueilContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<AccueilContent> content = service.getContent();

        assertNotNull(content.orElse(null));
        assertEquals(1L, content.orElseThrow().getId());
        assertEquals(3, content.orElseThrow().getCards().size());
    }

    @Test
    void saveContent_forceIdEtLieCartes() {
        AccueilContent input = new AccueilContent();
        CardPresentation card = new CardPresentation("Titre", "Desc", "img.jpg", "/link");
        input.setCards(new ArrayList<>(List.of(card)));

        when(repository.save(any(AccueilContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccueilContent saved = service.saveContent(input);

        assertEquals(1L, saved.getId());
        assertEquals(saved, saved.getCards().get(0).getAccueilContent());
    }

    @Test
    void uploadImageForCard_sauvegardeImageEtRetourneUrl() throws IOException {
        AccueilContent content = new AccueilContent();
        content.setId(1L);

        CardPresentation card = new CardPresentation("Titre", "Desc", "img.jpg", "/link");
        card.setId(8L);
        content.setCards(new ArrayList<>(List.of(card)));

        when(repository.findById(1L)).thenReturn(Optional.of(content));
        when(repository.save(any(AccueilContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("file", "banner.png", "image/png", "abc".getBytes());

        String url = service.uploadImageForCard(8L, file);

        assertEquals(true, url.startsWith("/uploads/cards/"));
        verify(repository).save(content);

        Path generated = Path.of(url.substring(1));
        Files.deleteIfExists(generated);
    }

    @Test
    void uploadImageForCard_lanceNotFound_siCardAbsente() {
        AccueilContent content = new AccueilContent();
        content.setId(1L);
        content.setCards(new ArrayList<>());

        when(repository.findById(1L)).thenReturn(Optional.of(content));

        MockMultipartFile file = new MockMultipartFile("file", "banner.png", "image/png", "abc".getBytes());

        assertThrows(ResourceNotFoundException.class, () -> service.uploadImageForCard(99L, file));
    }
}
