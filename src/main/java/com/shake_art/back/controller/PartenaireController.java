package com.shake_art.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.shake_art.back.partenaire.Partenaire;
import com.shake_art.back.model.PartenaireContent;
import com.shake_art.back.service.PartenaireService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/partenaires")
@CrossOrigin("*")
@Tag(name = "Partenaires", description = "Gestion des partenaires (CRUD + upload de logo)")
public class PartenaireController {

    @Autowired
    private PartenaireService partenaireService;

    @Operation(summary = "Créer un partenaire", description = "Ajoute un nouveau partenaire avec un logo optionnel.")
    @PostMapping
    public ResponseEntity<Partenaire> create(
            @Parameter(description = "Nom du partenaire", required = true)
            @RequestParam String nom,

            @Parameter(description = "Description du partenaire", required = true)
            @RequestParam String description,

            @Parameter(description = "URL du site web du partenaire", required = true)
            @RequestParam String siteWeb,

            @Parameter(description = "Logo du partenaire (fichier image)")
            @RequestParam(required = false) MultipartFile logo
    ) throws IOException {
        Partenaire p = partenaireService.create(nom, description, siteWeb, logo);
        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Liste tous les partenaires")
    @GetMapping
    public ResponseEntity<List<Partenaire>> getAll() {
        return ResponseEntity.ok(partenaireService.getAll());
    }

    @Operation(summary = "Récupère un partenaire par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<Partenaire> getOne(
            @Parameter(description = "ID du partenaire à récupérer")
            @PathVariable Long id
    ) {
        return partenaireService.getOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Met à jour un partenaire existant", description = "Modifie les informations du partenaire, y compris le logo.")
    @PutMapping("/{id}")
    public ResponseEntity<Partenaire> update(
            @Parameter(description = "ID du partenaire à mettre à jour", required = true)
            @PathVariable Long id,

            @Parameter(description = "Nouveau nom", required = true)
            @RequestParam String nom,

            @Parameter(description = "Nouvelle description", required = true)
            @RequestParam String description,

            @Parameter(description = "Nouveau site web", required = true)
            @RequestParam String siteWeb,

            @Parameter(description = "Nouveau logo (optionnel)")
            @RequestParam(required = false) MultipartFile logo
    ) throws IOException {
        Partenaire p = partenaireService.update(id, nom, description, siteWeb, logo);
        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Supprime un partenaire par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du partenaire à supprimer", required = true)
            @PathVariable Long id
    ) {
        partenaireService.delete(id);
        return ResponseEntity.ok().build();
    }

    /* === Nouveaux endpoints pour gérer le contenu éditable (titre + texte) === */

    @Operation(summary = "Récupère le contenu éditable de la page partenaires")
    @GetMapping("/content")
    public ResponseEntity<PartenaireContent> getContent() {
        return ResponseEntity.ok(partenaireService.getContent());
    }

    @Operation(summary = "Met à jour le contenu éditable de la page partenaires")
    @PutMapping("/content")
    public ResponseEntity<PartenaireContent> updateContent(@RequestBody PartenaireContent content) {
        return ResponseEntity.ok(partenaireService.updateContent(content));
    }



}
