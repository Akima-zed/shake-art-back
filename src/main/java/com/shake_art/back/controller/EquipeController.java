package com.shake_art.back.controller;

import static com.shake_art.back.mapper.EquipeMapper.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.dto.EquipeDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.service.EquipeService;

@RestController
@RequestMapping("/api/equipes")
@Tag(name = "Equipes API", description = "Gestion API des equipes")
public class EquipeController {

    private final EquipeService service;

    public EquipeController(EquipeService service) {
        this.service = service;
    }

    @Operation(summary = "Recuperer une equipe", description = "Retourne une equipe par son identifiant. Acces public.")
    @GetMapping("/{id}")
    public ResponseEntity<EquipeDto> getById(@PathVariable Long id) {
        EquipeModel equipe = service.getById(id);
        if (equipe == null) {
            throw new ResourceNotFoundException("Equipe introuvable avec l'id " + id);
        }
        return ResponseEntity.ok(toDto(equipe));
    }

    @Operation(summary = "Creer une equipe", description = "Cree une nouvelle equipe. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "201", description = "Equipe creee")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeDto> create(@RequestBody EquipeDto dto) {
        EquipeModel model = toModel(dto);
        EquipeModel saved = service.save(model);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @Operation(summary = "Mettre a jour une equipe", description = "Met a jour une equipe existante. Necessite ROLE_ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeDto> update(@PathVariable Long id, @RequestBody EquipeDto dto) {
        EquipeModel existing = service.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Equipe introuvable avec l'id " + id);
        }
        existing.setNom(dto.getNom());
        existing.setRole(dto.getRole());
        EquipeModel updated = service.save(existing);
        return ResponseEntity.ok(toDto(updated));
    }
}