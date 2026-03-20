package com.shake_art.back.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    @Test
    void generateAndValidateToken_withBase64Secret() {
        String base64Secret = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
        JwtService jwtService = new JwtService(base64Secret, 3_600_000L);

        UserDetails user = User.withUsername("user@test.com").password("x").roles("ADMIN").build();
        String token = jwtService.generateToken(user, "ADMIN");

        assertNotNull(token);
        assertEquals("user@test.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
        assertNotNull(jwtService.extractExpiration(token));
    }

    @Test
    void generateToken_withInvalidSecret_lanceException() {
        String invalidSecret = "plain-secret-with-at-least-thirty-two-bytes-value";
        JwtService jwtService = new JwtService(invalidSecret, 3_600_000L);

        UserDetails user = User.withUsername("other@test.com").password("x").roles("USER").build();

        assertThrows(RuntimeException.class, () -> jwtService.generateToken(user, "USER"));
    }
}
