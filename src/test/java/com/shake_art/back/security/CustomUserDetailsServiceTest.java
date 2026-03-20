package com.shake_art.back.security;

import com.shake_art.back.model.AuthRole;
import com.shake_art.back.model.AuthUser;
import com.shake_art.back.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_retourneUserDetails() {
        AuthUser user = new AuthUser();
        user.setEmail("admin@test.com");
        user.setPasswordHash("hash");
        user.setRole(AuthRole.ADMIN);
        user.setEnabled(true);

        when(authUserRepository.findByEmailIgnoreCase("admin@test.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin@test.com");

        assertEquals("admin@test.com", details.getUsername());
        assertEquals("hash", details.getPassword());
        assertEquals(1, details.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_lanceException_siAbsent() {
        when(authUserRepository.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@test.com"));
    }
}
