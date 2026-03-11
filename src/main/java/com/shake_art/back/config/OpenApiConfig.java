package com.shake_art.back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final Pattern HAS_ROLE_PATTERN = Pattern.compile("hasRole\\('([A-Za-z0-9_]+)'\\)");
    private static final Pattern HAS_ANY_ROLE_PATTERN = Pattern.compile("hasAnyRole\\(([^)]*)\\)");

    @Bean
    public OpenAPI shakeArtOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Shake Art Back API")
                .version("v1")
                .description("API backoffice du festival Shake Art.\\n\\n" +
                    "Roles metier:\\n" +
                    "- ROLE_ADMIN : administration et endpoints proteges backoffice\\n" +
                    "- ROLE_USER : role utilisateur authentifie (aucun endpoint dedie a ce role pour le moment)")
                .contact(new Contact().name("Equipe Shake Art"))
                .license(new License().name("Proprietary")))
            .components(new Components().addSecuritySchemes(
                SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Bearer token. Format: Bearer <token>")))
            .addTagsItem(new Tag().name("Authentication").description("Authentification et generation du token JWT"))
            .addTagsItem(new Tag().name("Admin").description("Endpoints proteges necessitant ROLE_ADMIN"))
            .addTagsItem(new Tag().name("Public API").description("Endpoints accessibles sans authentification"));
    }

    @Bean
    public OperationCustomizer documentSecurityAndResponses() {
        return (operation, handlerMethod) -> {
            Set<String> roles = extractRequiredRoles(handlerMethod);
            String tag = resolveTag(handlerMethod);

            operation.setTags(java.util.List.of(tag));

            if (operation.getSummary() == null || operation.getSummary().isBlank()) {
                operation.setSummary(humanizeMethodName(handlerMethod.getMethod().getName()));
            }

            boolean protectedEndpoint = !roles.isEmpty();
            if (protectedEndpoint) {
                operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
            }

            String accessLine = protectedEndpoint
                ? "Acces protege - roles requis: " + String.join(", ", roles)
                : "Acces public - aucun role requis";

            String description = operation.getDescription();
            operation.setDescription(description == null || description.isBlank()
                ? accessLine
                : description + "\n\n" + accessLine);

            addStandardResponses(operation.getResponses(), protectedEndpoint, handlerMethod.getMethod());
            return operation;
        };
    }

    private String resolveTag(HandlerMethod handlerMethod) {
        String controllerName = handlerMethod.getBeanType().getSimpleName();
        if (controllerName.contains("Auth")) {
            return "Authentication";
        }
        if (controllerName.contains("Admin")) {
            return "Admin";
        }
        return "Public API";
    }

    private String humanizeMethodName(String methodName) {
        String withSpaces = methodName.replaceAll("([a-z])([A-Z])", "$1 $2");
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1);
    }

    private Set<String> extractRequiredRoles(HandlerMethod handlerMethod) {
        Set<String> roles = new LinkedHashSet<>();
        PreAuthorize methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), PreAuthorize.class);
        PreAuthorize classAnnotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), PreAuthorize.class);

        if (methodAnnotation != null) {
            roles.addAll(parseRoles(methodAnnotation.value()));
        }
        if (classAnnotation != null) {
            roles.addAll(parseRoles(classAnnotation.value()));
        }
        return roles;
    }

    private Set<String> parseRoles(String expression) {
        Set<String> roles = new LinkedHashSet<>();

        Matcher hasRoleMatcher = HAS_ROLE_PATTERN.matcher(expression);
        while (hasRoleMatcher.find()) {
            roles.add("ROLE_" + hasRoleMatcher.group(1));
        }

        Matcher hasAnyRoleMatcher = HAS_ANY_ROLE_PATTERN.matcher(expression);
        while (hasAnyRoleMatcher.find()) {
            String[] rawRoles = hasAnyRoleMatcher.group(1).split(",");
            for (String rawRole : rawRoles) {
                String cleanRole = rawRole.replace("'", "").replace("\"", "").trim();
                if (!cleanRole.isEmpty()) {
                    roles.add("ROLE_" + cleanRole);
                }
            }
        }

        return roles;
    }

    private void addStandardResponses(ApiResponses responses, boolean protectedEndpoint, Method method) {
        if (responses == null) {
            return;
        }

        responses.computeIfAbsent("200", code -> new ApiResponse().description("Requete traitee avec succes"));
        responses.computeIfAbsent("400", code -> new ApiResponse().description("Requete invalide (erreur de validation ou format)"));
        responses.computeIfAbsent("404", code -> new ApiResponse().description("Ressource non trouvee"));
        responses.computeIfAbsent("500", code -> new ApiResponse().description("Erreur interne du serveur"));

        if (protectedEndpoint) {
            responses.computeIfAbsent("401", code -> new ApiResponse().description("Authentification requise (token JWT manquant ou invalide)"));
            responses.computeIfAbsent("403", code -> new ApiResponse().description("Acces refuse (role insuffisant)"));
        }

        if (method.getAnnotation(PostMapping.class) != null) {
            responses.computeIfAbsent("201", code -> new ApiResponse().description("Ressource creee"));
        }
        if (method.getAnnotation(DeleteMapping.class) != null) {
            responses.computeIfAbsent("204", code -> new ApiResponse().description("Ressource supprimee"));
        }
        if (method.getAnnotation(PutMapping.class) != null || method.getAnnotation(PatchMapping.class) != null) {
            responses.computeIfAbsent("200", code -> new ApiResponse().description("Ressource mise a jour"));
        }
        if (method.getAnnotation(GetMapping.class) != null) {
            responses.computeIfAbsent("200", code -> new ApiResponse().description("Donnees recuperees avec succes"));
        }
    }
}
