package com.shake_art.back.service;

import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.ReservationModel;
import com.shake_art.back.repository.ReservationRepository;
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
class ReservationServiceTest {

    @Mock
    private ReservationRepository repo;

    @InjectMocks
    private ReservationService service;

    @Test
    void save_retourneReservationSauvegardee() {
        ReservationModel model = new ReservationModel();
        model.setNom("Atelier A");
        when(repo.save(model)).thenReturn(model);

        ReservationModel saved = service.save(model);

        assertSame(model, saved);
        verify(repo).save(model);
    }

    @Test
    void save_null_declencheNpe() {
        assertThrows(NullPointerException.class, () -> service.save(null));
        verify(repo, never()).save(any());
    }

    @Test
    void findAll_retourneListe() {
        when(repo.findAll()).thenReturn(List.of(new ReservationModel(), new ReservationModel()));

        List<ReservationModel> result = service.findAll();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }

    @Test
    void validateReservation_introuvable_declencheException() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.validateReservation(1L));
        verify(repo, never()).save(any());
    }

    @Test
    void validateReservation_positionneValideeEtSauve() {
        ReservationModel model = new ReservationModel();
        model.setValidee(false);
        when(repo.findById(2L)).thenReturn(Optional.of(model));
        when(repo.save(model)).thenReturn(model);

        ReservationModel result = service.validateReservation(2L);

        assertTrue(result.isValidee());
        verify(repo).save(model);
    }

    @Test
    void suspendreReservation_positionneEtatEtSauve() {
        ReservationModel model = new ReservationModel();
        model.setSuspendue(false);
        when(repo.findById(3L)).thenReturn(Optional.of(model));
        when(repo.save(model)).thenReturn(model);

        ReservationModel result = service.suspendreReservation(3L, true);

        assertTrue(result.isSuspendue());
        verify(repo).save(model);
    }

    @Test
    void delete_transmetIdAuRepository() {
        service.delete(9L);
        verify(repo).deleteById(9L);
    }

    @Test
    void findById_retourneOptional() {
        ReservationModel model = new ReservationModel();
        when(repo.findById(10L)).thenReturn(Optional.of(model));

        Optional<ReservationModel> result = service.findById(10L);

        assertTrue(result.isPresent());
        verify(repo).findById(10L);
    }
}
