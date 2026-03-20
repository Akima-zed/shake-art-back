package com.shake_art.back.service;

import com.shake_art.back.dto.ProgrammationDto;
import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ProgrammationModel;
import com.shake_art.back.repository.ProgrammationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgrammationServiceTest {

    @Mock
    private ProgrammationRepository repository;

    @InjectMocks
    private ProgrammationService service;

    @Test
    void getAllDto_convertitModelVersDto() {
        ActiviteModel activite = new ActiviteModel();
        activite.setId(1L);
        activite.setType("atelier");
        activite.setName("Graffiti");
        activite.setHeure("10:00");
        activite.setReservable(true);

        ProgrammationModel model = new ProgrammationModel();
        model.setId(5L);
        model.setDate("2026-06-01");
        model.setAnnee(2026);
        model.setActivites(List.of(activite));

        when(repository.findAll()).thenReturn(List.of(model));

        List<ProgrammationDto> result = service.getAllDto();

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
        assertEquals(1, result.get(0).getActivites().size());
    }

    @Test
    void save_dateVide_declencheBusinessException() {
        ProgrammationModel model = new ProgrammationModel();
        model.setDate("");
        model.setAnnee(2026);
        model.setActivites(List.of(new ActiviteModel()));

        assertThrows(BusinessException.class, () -> service.save(model));
        verify(repository, never()).save(any());
    }

    @Test
    void save_anneeInvalide_declencheBusinessException() {
        ProgrammationModel model = new ProgrammationModel();
        model.setDate("2026-06-01");
        model.setAnnee(0);
        model.setActivites(List.of(new ActiviteModel()));

        assertThrows(BusinessException.class, () -> service.save(model));
    }

    @Test
    void save_activitesVides_declencheBusinessException() {
        ProgrammationModel model = new ProgrammationModel();
        model.setDate("2026-06-01");
        model.setAnnee(2026);
        model.setActivites(List.of());

        assertThrows(BusinessException.class, () -> service.save(model));
    }

    @Test
    void save_nominal_sauvegarde() {
        ProgrammationModel model = new ProgrammationModel();
        model.setDate("2026-06-01");
        model.setAnnee(2026);
        model.setActivites(List.of(new ActiviteModel()));
        when(repository.save(model)).thenReturn(model);

        ProgrammationModel result = service.save(model);

        assertSame(model, result);
        verify(repository).save(model);
    }

    @Test
    void delete_inexistant_declencheResourceNotFound() {
        when(repository.existsById(7L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(7L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void delete_existant_supprime() {
        when(repository.existsById(8L)).thenReturn(true);

        service.delete(8L);

        verify(repository).deleteById(8L);
    }

    @Test
    void deleteByDate_supprimeProgrammationsEtVideActivites() {
        ProgrammationModel prog = new ProgrammationModel();
        ActiviteModel a1 = new ActiviteModel();
        a1.setProgrammation(prog);
        prog.setActivites(new java.util.ArrayList<>(List.of(a1)));

        when(repository.getAllByDate("2026-06-01")).thenReturn(List.of(prog));

        service.deleteByDate("2026-06-01");

        assertNull(a1.getProgrammation());
        assertTrue(prog.getActivites().isEmpty());
        verify(repository).delete(prog);
    }

    @Test
    void getById_etGetByAnnee_relayentRepository() {
        ProgrammationModel p = new ProgrammationModel();
        when(repository.findById((Long) 1L)).thenReturn(Optional.of(p));
        when(repository.findByAnnee(2026)).thenReturn(List.of(p));

        assertSame(p, service.getById(1L));
        assertEquals(1, service.getByAnnee(2026).size());
    }
}
