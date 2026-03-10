package com.shake_art.back.service;

import com.shake_art.back.dto.LoginRequest;
import com.shake_art.back.dto.LoginResponse;
import com.shake_art.back.model.AuthUser;
import com.shake_art.back.repository.AuthUserRepository;
import com.shake_art.back.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUserRepository authUserRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        // Delegue la verification des identifiants a Spring Security.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AuthUser authUser = authUserRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        // Le token embarque l'identite + le role et inclut une expiration pour l'auth stateless.
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal(), authUser.getRole().name());
        long expiresAt = jwtService.extractExpiration(token).getTime();

        return new LoginResponse(token, "Bearer", expiresAt, authUser.getRole().name());
    }
}
