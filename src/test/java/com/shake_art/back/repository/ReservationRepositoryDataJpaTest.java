package com.shake_art.back.repository;

import com.shake_art.back.model.ReservationModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryDataJpaTest {

    @Autowired
    private ReservationRepository repository;

    @Test
    void saveEtFindById_fonctionnent() {
        ReservationModel reservation = new ReservationModel();
        reservation.setType("atelier");
        reservation.setNom("Graffiti");
        reservation.setHeure("15:00");
        reservation.setNomComplet("Alice Martin");
        reservation.setEmail("alice@example.com");
        reservation.setPersonnes(2);

        ReservationModel saved = repository.save(reservation);

        Optional<ReservationModel> loaded = repository.findById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals("Graffiti", loaded.get().getNom());
        assertEquals("alice@example.com", loaded.get().getEmail());
    }

    @Test
    void deleteById_supprimeBienEntite() {
        ReservationModel reservation = new ReservationModel();
        reservation.setType("atelier");
        reservation.setNom("Photo");
        reservation.setHeure("12:00");
        reservation.setNomComplet("Bob Durant");
        reservation.setEmail("bob@example.com");
        reservation.setPersonnes(1);

        ReservationModel saved = repository.save(reservation);
        Long id = saved.getId();

        repository.deleteById(id);

        assertTrue(repository.findById(id).isEmpty());
    }
}
