package com.shake_art.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.shake_art.back.dto.PartenaireDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.mapper.PartenaireMapper;
import com.shake_art.back.model.Partenaire;
import com.shake_art.back.model.PartenaireContent;
import com.shake_art.back.service.PartenaireService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/partenaires")
@CrossOrigin("*")
@Tag(name = "Partenaires", description = "Gestion des partenaires (CRUD + upload de logo)")
public class PartenaireController {

    @Autowired
    private PartenaireService partenaireService;

    @Operation(summary = "Créer un partenaire", description = "Ajoute un nouveau partenaire avec un logo optionnel. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "201", description = "Partenaire cree")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartenaireDto> create(
            @Parameter(description = "Nom du partenaire", required = true) @RequestParam String nom,

            @Parameter(description = "Description du partenaire", required = true) @RequestParam String description,

            @Parameter(description = "URL du site web du partenaire", required = true) @RequestParam String siteWeb,

            @Parameter(description = "Logo du partenaire (fichier image)") @RequestParam(required = false) MultipartFile logo)
            throws IOException {
        Partenaire p = partenaireService.create(nom, description, siteWeb, logo);
        return ResponseEntity.status(201).body(PartenaireMapper.toDto(p));
    }

    @Operation(summary = "Liste tous les partenaires")
    @GetMapping
    public ResponseEntity<List<PartenaireDto>> getAll() {
        return ResponseEntity.ok(
            partenaireService.getAll()
                .stream()
                .map(PartenaireMapper::toDto)
                .collect(Collectors.toList())
        );
    }

    @Operation(summary = "Récupère un partenaire par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<PartenaireDto> getOne(
            @Parameter(description = "ID du partenaire à récupérer") @PathVariable @NonNull Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        Partenaire partenaire = partenaireService.getOne(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partenaire introuvable avec l'id " + id));
        return ResponseEntity.ok(PartenaireMapper.toDto(partenaire));
    }

    @Operation(summary = "Met à jour un partenaire existant", description = "Modifie les informations du partenaire, y compris le logo. Necessite ROLE_ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartenaireDto> update(
            @Parameter(description = "ID du partenaire à mettre à jour", required = true) @PathVariable @NonNull Long id,

            @Parameter(description = "Nouveau nom", required = true) @RequestParam String nom,

            @Parameter(description = "Nouvelle description", required = true) @RequestParam String description,

            @Parameter(description = "Nouveau site web", required = true) @RequestParam String siteWeb,

            @Parameter(description = "Nouveau logo (optionnel)") @RequestParam(required = false) MultipartFile logo)
            throws IOException {
        Objects.requireNonNull(id, "ID cannot be null");
        Partenaire p = partenaireService.update(id, nom, description, siteWeb, logo);
        return ResponseEntity.ok(PartenaireMapper.toDto(p));
    }

    @Operation(summary = "Supprime un partenaire par son ID", description = "Supprime un partenaire et son logo associe. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Partenaire supprime")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du partenaire à supprimer", required = true) @PathVariable @NonNull Long id) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        partenaireService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* === Nouveaux endpoints pour gérer le contenu éditable (titre + texte) === */

    @Operation(summary = "Récupère le contenu éditable de la page partenaires")
    @GetMapping("/content")
    public ResponseEntity<PartenaireContent> getContent() {
        return ResponseEntity.ok(partenaireService.getContent());
    }

    @Operation(summary = "Met à jour le contenu éditable de la page partenaires", description = "Met a jour le titre/texte de la section partenaires. Necessite ROLE_ADMIN.")
    @PutMapping("/content")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartenaireContent> updateContent(@RequestBody PartenaireContent content) {
        return ResponseEntity.ok(partenaireService.updateContent(content));
    }

}
