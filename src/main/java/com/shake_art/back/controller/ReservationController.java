package com.shake_art.back.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.model.ReservationModel;
import com.shake_art.back.service.EmailService;
import com.shake_art.back.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@CrossOrigin("*")
public class ReservationController {

    @Autowired
    private ReservationService service;
    @Autowired private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> reserver(@RequestBody ReservationModel reservation) {
        ReservationModel saved = service.save(reservation);
        emailService.envoyerConfirmation(saved);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<ReservationModel> getAll() {
        return service.findAll();
    }

    @PutMapping("/{id}/valider")
    public ReservationModel valider(@PathVariable Long id) {
        return service.validateReservation(id);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id) {
        service.delete(id);
    }

    // Nouveau endpoint pour suspendre/réactiver une réservation
    @PutMapping("/{id}/suspendre")
    public ResponseEntity<?> suspendre(@PathVariable Long id, @RequestBody SuspendRequest request) {
        ReservationModel updated = service.suspendreReservation(id, request.isSuspendue());
        return ResponseEntity.ok(updated);
    }

    // Classe interne DTO pour la requête PUT suspendre
    public static class SuspendRequest {
        private boolean suspendue;

        public boolean isSuspendue() {
            return suspendue;
        }

        public void setSuspendue(boolean suspendue) {
            this.suspendue = suspendue;
        }
    }

}
