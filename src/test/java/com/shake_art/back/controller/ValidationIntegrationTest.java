package com.shake_art.back.controller;

import com.shake_art.back.repository.ActiviteRepository;
import com.shake_art.back.repository.ProgrammationRepository;
import com.shake_art.back.service.AdminService;
import com.shake_art.back.service.AuthService;
import com.shake_art.back.service.EmailService;
import com.shake_art.back.service.ProgrammationService;
import com.shake_art.back.service.ReservationService;
import com.shake_art.back.security.CustomUserDetailsService;
import com.shake_art.back.security.JwtAuthenticationFilter;
import com.shake_art.back.security.RestAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        ProgrammationController.class,
        AdminController.class,
        ReservationController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(com.shake_art.back.exception.GlobalValidationExceptionHandler.class)
class ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private ProgrammationService programmationService;

    @MockBean
    private ProgrammationRepository programmationRepository;

    @MockBean
    private ActiviteRepository activiteRepository;

    @MockBean
    private AdminService adminService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private EmailService emailService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private CustomUserDetailsService customUserDetailsService;

        @MockBean
        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Test
    void loginInvalide_retourne400_avecJsonClair() throws Exception {
        String payload = """
                {
                  "email": "email-invalide",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Requete invalide"))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    void programmationInvalide_retourne400_avecJsonClair() throws Exception {
        String payload = """
                {
                  "annee": 1990,
                  "activites": []
                }
                """;

        mockMvc.perform(post("/programmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Requete invalide"))
                .andExpect(jsonPath("$.details.date").exists())
                .andExpect(jsonPath("$.details.annee").exists())
                .andExpect(jsonPath("$.details.activites").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEmailInvalide_retourne400_avecJsonClair() throws Exception {
        String payload = """
                {
                  "email": ""
                }
                """;

        mockMvc.perform(put("/admin/reservation-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Requete invalide"))
                .andExpect(jsonPath("$.details.email").exists());
    }
}
