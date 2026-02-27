package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import com.shake_art.back.model.ReservationModel;
import com.shake_art.back.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repo;

    public ReservationModel save(@NonNull ReservationModel r) {
        Objects.requireNonNull(r, "La réservation ne peut pas être nulle");
        return repo.save(r);
    }

    public List<ReservationModel> findAll() {
        return repo.findAll();
    }

    public void delete(@NonNull Long id) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        repo.deleteById(id);
    }

    public Optional<ReservationModel> findById(@NonNull Long id) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        return repo.findById(id);
    }

    public ReservationModel validateReservation(@NonNull Long id) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        ReservationModel r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));
        r.setValidee(true);
        return repo.save(r);
    }

    public ReservationModel suspendreReservation(@NonNull Long id, boolean suspendue) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        ReservationModel r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));
        r.setSuspendue(suspendue);
        return repo.save(r);
    }

}
