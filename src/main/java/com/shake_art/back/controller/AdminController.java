package com.shake_art.back.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
@Tag(name = "Admin", description = "Endpoints proteges - necessite ROLE_ADMIN + token JWT Bearer")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService service;

    @Operation(summary = "Lire l'email de reservation",
        description = "Retourne l'adresse email cible pour les confirmations de reservation. Acces ROLE_ADMIN requis.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email de reservation retourne"),
        @ApiResponse(responseCode = "401", description = "Token JWT manquant ou invalide"),
        @ApiResponse(responseCode = "403", description = "Role ADMIN requis")
    })
    @GetMapping("/reservation-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getReservationEmail() {
        return ResponseEntity.ok(service.getReservationEmail());
    }

    @Operation(summary = "Modifier l'email de reservation",
        description = "Met a jour l'adresse email cible pour les confirmations de reservation. Acces ROLE_ADMIN requis.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email mis a jour avec succes"),
        @ApiResponse(responseCode = "400", description = "Format email invalide"),
        @ApiResponse(responseCode = "401", description = "Token JWT manquant ou invalide"),
        @ApiResponse(responseCode = "403", description = "Role ADMIN requis")
    })
    @PutMapping("/reservation-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateReservationEmail(@Valid @RequestBody EmailRequest request) {
        service.setReservationEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    // DTO interne pour la requête PUT
    public static class EmailRequest {

        @NotBlank(message = "L'email ne peut pas etre vide")
        @Email(message = "Le format de l'email est invalide")
        private String email;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }
}