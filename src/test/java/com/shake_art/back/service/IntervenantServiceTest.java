package com.shake_art.back.service;

import com.shake_art.back.dto.IntervenantDto;
import com.shake_art.back.model.ArtisteModel;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.repository.ArtisteRepository;
import com.shake_art.back.repository.EquipeMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntervenantServiceTest {

    @Mock
    private ArtisteRepository artisteRepository;

    @Mock
    private EquipeMemberRepository equipeRepository;

    @InjectMocks
    private IntervenantService service;

    @Test
    void search_retourneArtistesEtEquipes() {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(1L);
        artiste.setName("Banksy");

        EquipeModel equipe = new EquipeModel();
        equipe.setId(2L);
        equipe.setFullName("Team A");

        when(artisteRepository.searchByName("ba")).thenReturn(List.of(artiste));
        when(equipeRepository.searchByName("ba")).thenReturn(List.of(equipe));

        List<IntervenantDto> result = service.search("ba");

        assertEquals(2, result.size());
        assertEquals("artiste", result.get(0).getType());
        assertEquals("equipe", result.get(1).getType());
    }

    @Test
    void getAll_retourneTousIntervenants() {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setId(11L);
        artiste.setName("Art A");

        EquipeModel equipe = new EquipeModel();
        equipe.setId(22L);
        equipe.setFullName("Equipe B");

        when(artisteRepository.findAll()).thenReturn(List.of(artiste));
        when(equipeRepository.findAll()).thenReturn(List.of(equipe));

        List<IntervenantDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("Art A", result.get(0).getFullName());
        assertEquals("Equipe B", result.get(1).getFullName());
    }
}
