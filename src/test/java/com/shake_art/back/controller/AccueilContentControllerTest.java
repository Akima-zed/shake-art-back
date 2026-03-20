package com.shake_art.back.controller;

import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.AccueilContent;
import com.shake_art.back.model.CardPresentation;
import com.shake_art.back.repository.CardPresentationRepository;
import com.shake_art.back.service.AccueilContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccueilContentControllerTest {

    @Mock
    private AccueilContentService service;

    @Mock
    private CardPresentationRepository cardRepository;

    @InjectMocks
    private AccueilContentController controller;

    @Test
    void getAccueilContent_retourneContenu() {
        AccueilContent content = new AccueilContent();
        content.setId(1L);
        when(service.getContent()).thenReturn(Optional.of(content));

        ResponseEntity<AccueilContent> response = controller.getAccueilContent();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAccueilContent_lanceNotFound_siAbsent() {
        when(service.getContent()).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controller.getAccueilContent());
    }

    @Test
    void uploadHeroVideo_lanceBusiness_siVide() {
        MockMultipartFile empty = new MockMultipartFile("video", "video.mp4", "video/mp4", new byte[0]);

        assertThrows(BusinessException.class, () -> controller.uploadHeroVideo(empty));
    }

    @Test
    void uploadHeroVideo_retourneUrl() throws IOException {
        MockMultipartFile video = new MockMultipartFile("video", "hero.mp4", "video/mp4", "abc".getBytes());

        ResponseEntity<String> response = controller.uploadHeroVideo(video);

        assertEquals(200, response.getStatusCode().value());
        String relativePath = response.getBody().substring(1);
        Files.deleteIfExists(Path.of(relativePath));
    }

    @Test
    void uploadCardImage_metAJourCarte() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CardPresentation card = new CardPresentation();
        card.setId(3L);
        when(cardRepository.findById(3L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(CardPresentation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("file", "card.jpg", "image/jpeg", "abc".getBytes());

        ResponseEntity<String> response = controller.uploadCardImage(3L, file);

        assertEquals(200, response.getStatusCode().value());
        verify(cardRepository).save(card);

        String imageUrl = card.getImage();
        int uploadsIdx = imageUrl.indexOf("/uploads/cards/");
        String relativePath = imageUrl.substring(uploadsIdx + 1);
        Files.deleteIfExists(Path.of(relativePath));
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void createCard_retourne201() {
        AccueilContent content = new AccueilContent();
        content.setId(1L);
        CardPresentation card = new CardPresentation();

        when(service.getContent()).thenReturn(Optional.of(content));
        when(cardRepository.save(any(CardPresentation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<CardPresentation> response = controller.createCard(card);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(content, response.getBody().getAccueilContent());
    }
}
