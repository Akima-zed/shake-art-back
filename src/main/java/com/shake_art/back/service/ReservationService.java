package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shake_art.back.model.ReservationModel;
import com.shake_art.back.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repo;

    public ReservationModel save(ReservationModel r) {
        return repo.save(r);
    }

    public List<ReservationModel> findAll() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Optional<ReservationModel> findById(Long id) {
        return repo.findById(id);
    }

    public ReservationModel validateReservation(Long id) {
        ReservationModel r = repo.findById(id).orElseThrow();
        r.setValidee(true);
        return repo.save(r);
    }
    public ReservationModel suspendreReservation(Long id, boolean suspendue) {
        ReservationModel r = repo.findById(id).orElseThrow();
        r.setSuspendue(suspendue);
        return repo.save(r);
    }

}
