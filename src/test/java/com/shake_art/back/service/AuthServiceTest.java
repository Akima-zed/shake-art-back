package com.shake_art.back.service;

import com.shake_art.back.dto.LoginRequest;
import com.shake_art.back.dto.LoginResponse;
import com.shake_art.back.model.AuthRole;
import com.shake_art.back.model.AuthUser;
import com.shake_art.back.repository.AuthUserRepository;
import com.shake_art.back.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService service;

    @Test
    void login_nominal_retourneTokenEtRole() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("secret");

        UserDetails userDetails = User.withUsername("admin@test.com").password("x").roles("ADMIN").build();
        AuthUser user = new AuthUser();
        user.setEmail("admin@test.com");
        user.setRole(AuthRole.ADMIN);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authUserRepository.findByEmailIgnoreCase("admin@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(eq(userDetails), eq("ADMIN"))).thenReturn("token123");
        when(jwtService.extractExpiration("token123")).thenReturn(new Date(1700000000000L));

        LoginResponse response = service.login(request);

        assertEquals("token123", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("ADMIN", response.getRole());
        assertEquals(1700000000000L, response.getExpiresAt());
    }

    @Test
    void login_utilisateurAbsent_declencheBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@test.com");
        request.setPassword("secret");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authUserRepository.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> service.login(request));
    }
}
