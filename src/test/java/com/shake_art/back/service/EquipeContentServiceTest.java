package com.shake_art.back.service;

import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.EquipeContent;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.repository.EquipeContentRepository;
import com.shake_art.back.repository.EquipeMemberRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class EquipeContentServiceTest {

    @Mock
    private EquipeContentRepository contentRepository;

    @Mock
    private EquipeMemberRepository memberRepository;

    @InjectMocks
    private EquipeContentService service;

    @Test
    void getContent_creeValeurDefaut_siAbsente() {
        when(contentRepository.findById(1L)).thenReturn(Optional.empty());
        when(contentRepository.save(any(EquipeContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<EquipeContent> content = service.getContent();

        assertEquals(1L, content.orElseThrow().getId());
    }

    @Test
    void deleteById_retourneTrue_siExistant() {
        when(contentRepository.existsById(5L)).thenReturn(true);

        boolean deleted = service.deleteById(5L);

        assertEquals(true, deleted);
        verify(contentRepository).deleteById(5L);
    }

    @Test
    void updateMember_lanceNotFound_siInexistant() {
        EquipeModel member = new EquipeModel();
        member.setId(3L);
        when(memberRepository.existsById(3L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.updateMember(member));
    }

    @Test
    void uploadMemberPhoto_sauvegardeEtRetourneUrl() throws IOException {
        EquipeModel member = new EquipeModel();
        member.setId(2L);
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(EquipeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("file", "member.png", "image/png", "abc".getBytes());

        String url = service.uploadMemberPhoto(2L, file);

        assertEquals(true, url.startsWith("/api/equipe/images/members/"));

        String filename = url.substring(url.lastIndexOf('/') + 1);
        Path generated = Path.of("uploads/equipe/members", filename);
        Files.deleteIfExists(generated);
    }

    @Test
    void uploadMemberPhoto_lanceBusiness_siFichierVide() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThrows(BusinessException.class, () -> service.uploadMemberPhoto(2L, empty));
    }

    @Test
    void deleteMemberPhoto_effaceReferencePhoto() throws IOException {
        EquipeModel member = new EquipeModel();
        member.setId(9L);
        member.setPhotoUrl("missing.jpg");

        when(memberRepository.findById(9L)).thenReturn(Optional.of(member));

        service.deleteMemberPhoto(9L);

        assertEquals(null, member.getPhotoUrl());
        verify(memberRepository).save(member);
    }

    @Test
    void getMembers_retourneListe() {
        when(memberRepository.findAll()).thenReturn(List.of(new EquipeModel(), new EquipeModel()));

        List<EquipeModel> members = service.getMembers();

        assertEquals(2, members.size());
    }

    @Test
    void saveOrUpdate_forceIdSingleton() {
        EquipeContent content = new EquipeContent();
        content.setId(99L);
        when(contentRepository.save(any(EquipeContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquipeContent saved = service.saveOrUpdate(content);

        assertEquals(1L, saved.getId());
    }

    @Test
    void uploadBannerImage_sauvegardeNomFichier() throws IOException {
        EquipeContent content = new EquipeContent();
        content.setId(1L);
        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(contentRepository.save(any(EquipeContent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("file", "banner.jpg", "image/jpeg", "abc".getBytes());

        String url = service.uploadBannerImage(file);

        assertEquals(true, url.startsWith("/api/equipe/images/banner/"));
        String filename = url.substring(url.lastIndexOf('/') + 1);
        Files.deleteIfExists(Path.of("uploads/equipe/banners", filename));
    }

    @Test
    void deleteBannerImage_effaceValeurSiPresente() throws IOException {
        EquipeContent content = new EquipeContent();
        content.setId(1L);
        content.setBannerImageUrl("missing.jpg");
        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

        service.deleteBannerImage();

        assertEquals(null, content.getBannerImageUrl());
        verify(contentRepository).save(content);
    }

    @Test
    void updateMember_persisteSiExistant() {
        EquipeModel member = new EquipeModel();
        member.setId(3L);
        member.setFullName("Camille");
        when(memberRepository.existsById(3L)).thenReturn(true);
        when(memberRepository.save(any(EquipeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquipeModel saved = service.updateMember(member);

        assertEquals("Camille", saved.getFullName());
    }

    @Test
    void deleteMember_supprimeSansPhoto() {
        EquipeModel member = new EquipeModel();
        member.setId(12L);
        member.setPhotoUrl(null);
        when(memberRepository.findById(12L)).thenReturn(Optional.of(member));

        service.deleteMember(12L);

        verify(memberRepository).deleteById(12L);
        verify(memberRepository, never()).save(any(EquipeModel.class));
    }

    @Test
    void equipeMapper_convertitDansLesDeuxSens() {
        EquipeContent content = new EquipeContent();
        content.setId(7L);
        content.setPresentationText("Texte");
        content.setBannerImageUrl("banner.jpg");

        EquipeModel model = EquipeContentService.EquipeMapper.toEquipeModel(content);
        assertEquals(7L, model.getId());
        assertEquals("Texte", model.getFullName());

        EquipeContent back = EquipeContentService.EquipeMapper.toEquipeContent(model);
        assertEquals(7L, back.getId());
        assertEquals("Texte", back.getPresentationText());
    }
}
