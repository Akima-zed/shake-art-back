package com.shake_art.back.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import com.shake_art.back.security.CustomUserDetailsService;
import com.shake_art.back.security.JwtAuthenticationFilter;
import com.shake_art.back.security.JwtService;
import com.shake_art.back.security.RestAuthenticationEntryPoint;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExceptionTestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalValidationExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Test
    void businessException_retourne400_formatStandard() throws Exception {
        mockMvc.perform(get("/errors/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE"))
                .andExpect(jsonPath("$.message").value("Regle metier violee"))
                .andExpect(jsonPath("$.path").value("/errors/business"));
    }

    @Test
    void notFound_retourne404_formatStandard() throws Exception {
        mockMvc.perform(get("/errors/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/errors/not-found"));
    }

    @Test
    void accessDenied_retourne403_formatStandard() throws Exception {
        mockMvc.perform(get("/errors/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.path").value("/errors/forbidden"));
    }

    @Test
    void unexpected_retourne500_formatStandard() throws Exception {
        mockMvc.perform(get("/errors/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Une erreur interne est survenue"))
                .andExpect(jsonPath("$.path").value("/errors/unexpected"));
    }

    @Test
    void validation_retourne400_detailsChamps() throws Exception {
        String payload = """
                {
                  "name": ""
                }
                """;

        mockMvc.perform(post("/errors/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.name").exists());
    }

}
