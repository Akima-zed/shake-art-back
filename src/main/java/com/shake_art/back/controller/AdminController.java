package com.shake_art.back.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminService service;

    @GetMapping("/reservation-email")
    public ResponseEntity<String> getReservationEmail() {
        return ResponseEntity.ok(service.getReservationEmail());
    }

    @PutMapping("/reservation-email")
    public ResponseEntity<?> updateReservationEmail(@RequestBody EmailRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("L'email ne peut pas être vide");
        }
        service.setReservationEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    // DTO interne pour la requête PUT
    public static class EmailRequest {
        private String email;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }
}