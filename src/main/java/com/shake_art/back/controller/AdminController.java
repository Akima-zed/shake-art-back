package com.shake_art.back.controller;


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
public class AdminController {

    @Autowired
    private AdminService service;

    @GetMapping("/reservation-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getReservationEmail() {
        return ResponseEntity.ok(service.getReservationEmail());
    }

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