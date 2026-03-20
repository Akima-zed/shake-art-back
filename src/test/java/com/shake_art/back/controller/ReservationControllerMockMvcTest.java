package com.shake_art.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shake_art.back.model.ReservationModel;
import com.shake_art.back.security.CustomUserDetailsService;
import com.shake_art.back.security.JwtAuthenticationFilter;
import com.shake_art.back.security.RestAuthenticationEntryPoint;
import com.shake_art.back.service.EmailService;
import com.shake_art.back.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.shake_art.back.exception.GlobalValidationExceptionHandler.class)
class ReservationControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Test
    void reserver_retourne201() throws Exception {
        ReservationModel req = new ReservationModel();
        req.setType("atelier");
        req.setNom("Graffiti");
        req.setHeure("14:00");
        req.setNomComplet("Jean Dupont");
        req.setEmail("jean@example.com");
        req.setPersonnes(2);

        ReservationModel saved = new ReservationModel();
        saved.setId(1L);
        saved.setType(req.getType());
        saved.setNom(req.getNom());
        saved.setHeure(req.getHeure());
        saved.setNomComplet(req.getNomComplet());
        saved.setEmail(req.getEmail());
        saved.setPersonnes(req.getPersonnes());

        when(reservationService.save(any(ReservationModel.class))).thenReturn(saved);
        doNothing().when(emailService).envoyerConfirmation(any(ReservationModel.class));

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Graffiti"));

        verify(emailService).envoyerConfirmation(any(ReservationModel.class));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getAll_retourne200() throws Exception {
        ReservationModel r = new ReservationModel();
        r.setId(10L);
        r.setNom("Atelier");

        when(reservationService.findAll()).thenReturn(List.of(r));

        mockMvc.perform(get("/reservation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void supprimer_retourne204() throws Exception {
        doNothing().when(reservationService).delete(3L);

        mockMvc.perform(delete("/reservation/3"))
                .andExpect(status().isNoContent());
    }
}
