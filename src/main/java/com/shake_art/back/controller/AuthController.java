package com.shake_art.back.controller;

import com.shake_art.back.dto.LoginRequest;
import com.shake_art.back.dto.LoginResponse;
import com.shake_art.back.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentification et obtention du token JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Connexion administrateur",
        description = "Authentifie un utilisateur avec ses identifiants et retourne un token JWT Bearer.\n\n"
            + "Utiliser ce token dans Swagger via le bouton **Authorize** (valeur: `<token>`)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion reussie - token JWT retourne",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Email ou mot de passe invalide (format incorrect)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))),
        @ApiResponse(responseCode = "401", description = "Identifiants incorrects",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse")))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
