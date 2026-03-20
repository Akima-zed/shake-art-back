package com.shake_art.back.security;

import com.shake_art.back.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityErrorResponseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    void adminSansAuthentification_retourne401_formatStandard() throws Exception {
        mockMvc.perform(get("/admin/reservation-email"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Authentification requise"))
                .andExpect(jsonPath("$.path").value("/admin/reservation-email"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminAvecRoleUser_retourne403_formatStandard() throws Exception {
        mockMvc.perform(get("/admin/reservation-email"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("Acces refuse"))
                .andExpect(jsonPath("$.path").value("/admin/reservation-email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminAvecRoleAdmin_retourne200_etEmail() throws Exception {
        given(adminService.getReservationEmail()).willReturn("admin@shake-art.fr");

        mockMvc.perform(get("/admin/reservation-email"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin@shake-art.fr"));
    }
}
