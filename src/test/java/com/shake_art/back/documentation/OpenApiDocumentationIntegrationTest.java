package com.shake_art.back.documentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;

@ActiveProfiles("test")
@SpringBootTest(properties = "management.health.mail.enabled=false")
@AutoConfigureMockMvc
class OpenApiDocumentationIntegrationTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void swaggerUiEstAccessibleDepuisLePointDEntreeConfigure() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    void openApiExposeLesRolesEtLaSecuriteDesEndpointsProteges() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(content).contains("\"bearerAuth\"");
        assertThat(content).contains("\"/reservation\"");
        assertThat(content).contains("Acces protege - roles requis: ROLE_USER, ROLE_ADMIN");
        assertThat(content).contains("ROLE_ADMIN");
        assertThat(content).contains("ROLE_USER");
    }

    @Test
    void dtosSontVisiblesDanslOpenApiDocumentation() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode doc = objectMapper.readTree(content);
        
        // Verifier que les schemas sont exposes dans OpenAPI
        assertThat(content).contains("\"components\"");
        assertThat(content).contains("\"schemas\"");
        
        // S'assurer que des DTOs sont referencés
        JsonNode schemas = doc.get("components").get("schemas");
        assertThat(schemas).isNotNull();
        assertThat(schemas.size()).isGreaterThan(0);
    }

    @Test
    void endpointsCreationHabituellementReturn201Et204() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Verifier que les codes de réponse explicites 201 et 204 sont documentés
        // Ces codes doivent apparaître pour les endpoints POST et DELETE
        JsonNode doc = objectMapper.readTree(content);
        JsonNode paths = doc.get("paths");
        
        assertThat(content).contains("201");
        assertThat(content).contains("204");
    }

    @Test
    void endpointsPrincipaixSontDocumentes() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode doc = objectMapper.readTree(content);
        
        // Verifier la présence des principaux endpoints
        assertThat(doc.toString()).contains("/reservation");
        assertThat(doc.toString()).contains("/partenaires");
        assertThat(doc.toString()).contains("/murs");
        assertThat(doc.toString()).contains("/programmation");
        assertThat(doc.toString()).contains("/equipe");
        
        // Verifier qu'au moins les endpoints ont des operations documentées
        JsonNode paths = doc.get("paths");
        assertThat(paths).isNotNull();
        assertThat(paths.size()).isGreaterThanOrEqualTo(5);
    }
}
