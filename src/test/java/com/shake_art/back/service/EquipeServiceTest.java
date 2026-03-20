package com.shake_art.back.service;

import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.repository.EquipeModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeModelRepository equipeRepository;

    @InjectMocks
    private EquipeService service;

    @Test
    void getById_retourneEquipe_siPresente() {
        EquipeModel equipe = new EquipeModel();
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));

        EquipeModel result = service.getById(1L);

        assertSame(equipe, result);
        verify(equipeRepository).findById(1L);
    }

    @Test
    void getById_null_declencheNpe() {
        assertThrows(NullPointerException.class, () -> service.getById(null));
    }

    @Test
    void save_retourneEquipeSauvee() {
        EquipeModel equipe = new EquipeModel();
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        EquipeModel result = service.save(equipe);

        assertSame(equipe, result);
        verify(equipeRepository).save(equipe);
    }
}
