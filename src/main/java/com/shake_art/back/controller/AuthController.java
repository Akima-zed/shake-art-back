package com.shake_art.back.controller;

import com.shake_art.back.dto.LoginRequest;
import com.shake_art.back.dto.LoginResponse;
import com.shake_art.back.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Retourne uniquement les metadonnees de token utiles au client.
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
