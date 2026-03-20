package com.shake_art.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.model.ReservationModel;
import com.shake_art.back.service.EmailService;
import com.shake_art.back.service.ReservationService;

import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/reservation")
@CrossOrigin("*")
@Tag(name = "Reservations", description = "Gestion des reservations de places au festival")
public class ReservationController {

    @Autowired
    private ReservationService service;
    @Autowired
    private EmailService emailService;

    @Operation(summary = "Soumettre une reservation",
        description = "Cree une reservation pour une activite du festival et envoie un email de confirmation au visiteur. Acces public.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reservation enregistree, email de confirmation envoye"),
        @ApiResponse(responseCode = "400", description = "Donnees de reservation invalides")
    })
    @PostMapping
    public ResponseEntity<?> reserver(@Valid @RequestBody @NonNull ReservationModel reservation) {
        Objects.requireNonNull(reservation, "La réservation ne peut pas être nulle");
        ReservationModel saved = service.save(reservation);
        emailService.envoyerConfirmation(saved);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(summary = "Lister toutes les reservations",
        description = "Retourne toutes les reservations enregistrees. Necessite ROLE_USER ou ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des reservations retournee"),
        @ApiResponse(responseCode = "401", description = "Token JWT manquant ou invalide")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<ReservationModel> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Valider une reservation",
        description = "Marque une reservation comme validee (confirmee par l'organisateur). Necessite ROLE_ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationModel valider(@PathVariable @NonNull Long id) {
        return service.validateReservation(id);
    }

    @Operation(summary = "Supprimer une reservation",
        description = "Supprime definitivement une reservation. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Reservation supprimee")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable @NonNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Suspendre ou reactiver une reservation",
        description = "Permet de suspendre ou de lever la suspension d'une reservation (ex: surreservation, incident). Necessite ROLE_ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/suspendre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> suspendre(@PathVariable @NonNull Long id, @Valid @RequestBody SuspendRequest request) {
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
