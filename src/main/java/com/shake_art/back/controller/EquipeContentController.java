package com.shake_art.back.controller;

import com.shake_art.back.dto.EquipeContentDto;
import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.exception.TechnicalException;
import com.shake_art.back.mapper.EquipeContentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.NonNull;

import com.shake_art.back.model.EquipeContent;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.service.EquipeContentService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Contrôleur REST pour la gestion complète de la page Équipe :
 * - contenu principal (texte + bannière)
 * - membres de l'équipe (CRUD + upload/suppression photo)
 * - accès aux images (bannière + photos membres)
 *
 * URL préfixe : /api/equipe
 * Cross-Origin autorisé depuis n'importe quelle origine (adapter en prod)
 */
@RestController
@RequestMapping("equipe")
@CrossOrigin(origins = "*") // À restreindre en production pour la sécurité
@Tag(name = "Equipe", description = "Gestion du contenu de la page equipe et des membres")
public class EquipeContentController {

    @Autowired
    private EquipeContentService service;

    // Dossiers physiques sur disque où sont stockées les images
    private final Path bannersDir = Paths.get("uploads/equipe/banners");
    private final Path membersDir = Paths.get("uploads/equipe/members");

    // ----------- Contenu principal Équipe -----------

    /** Récupère le contenu principal équipe (texte + url bannière) */
    @Operation(summary = "Recuperer le contenu equipe", description = "Retourne le contenu principal de la page equipe avec son texte et l'URL de banniere. Acces public.")
    @GetMapping(value = "/content", produces = "application/json")
    public ResponseEntity<EquipeContentDto> getContent() {
        EquipeContent content = service.getContent()
            .orElseThrow(() -> new ResourceNotFoundException("Contenu equipe introuvable"));
        return ResponseEntity.ok(EquipeContentMapper.toDto(content));
    }

    /** Crée un nouveau contenu principal équipe */
    @Operation(summary = "Creer le contenu equipe", description = "Cree le contenu principal de la page equipe. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "201", description = "Contenu equipe cree")
    @PostMapping("/content")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeContentDto> createContent(@RequestBody EquipeContentDto contentDto) {
        EquipeContent content = EquipeContentMapper.toModel(contentDto);
        EquipeContent saved = service.saveOrUpdate(content);
        return ResponseEntity.status(201).body(EquipeContentMapper.toDto(saved));
    }

    /** Met à jour un contenu principal équipe existant */
    @Operation(summary = "Mettre a jour le contenu equipe", description = "Met a jour le contenu principal de la page equipe. Necessite ROLE_ADMIN.")
    @PutMapping("/content")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeContentDto> updateContent(@RequestBody EquipeContentDto contentDto) {
        if (contentDto.getId() == null) {
            throw new BusinessException("L'id du contenu est obligatoire pour la mise a jour");
        }
        EquipeContent content = EquipeContentMapper.toModel(contentDto);
        EquipeContent updated = service.saveOrUpdate(content);
        return ResponseEntity.ok(EquipeContentMapper.toDto(updated));
    }

    /** Supprime un contenu principal par son ID */
    @Operation(summary = "Supprimer le contenu equipe", description = "Supprime le contenu principal par son identifiant. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Contenu equipe supprime")
    @DeleteMapping("/content/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Contenu equipe introuvable avec l'id " + id);
    }

    /**
     * Upload d'une image bannière pour la page équipe
     * Création automatique du dossier si nécessaire.
     * 
     * @param file fichier image (champ form-data "file")
     * @return URL publique (endpoint) pour accéder à cette image
     */
    @PostMapping("/content/upload-banner")
    @Operation(summary = "Uploader la banniere equipe", description = "Charge une nouvelle image de banniere pour la page equipe. Necessite ROLE_ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadBanner(@RequestParam("file") MultipartFile file) {
        try {
            // Upload physique + mise à jour url dans la base
            String imageUrl = service.uploadBannerImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de l'upload de la banniere", e);
        }
    }

    /**
     * Suppression de la bannière :
     * - supprime physiquement le fichier image sur disque
     * - supprime l'URL dans la base
     */
    @DeleteMapping("/content/delete-banner")
    @Operation(summary = "Supprimer la banniere equipe", description = "Supprime l'image de banniere actuelle de la page equipe. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Banniere supprimee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBanner() {
        try {
            service.deleteBannerImage();
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de la suppression de la banniere", e);
        }
        return ResponseEntity.noContent().build();
    }

    // ----------- Gestion des membres -----------

    /** Récupère la liste complète des membres de l'équipe */
    @Operation(summary = "Lister les membres", description = "Retourne la liste complete des membres de l'equipe. Acces public.")
    @GetMapping(value = "/members", produces = "application/json")
    public ResponseEntity<List<EquipeModel>> getMembers() {
        List<EquipeModel> members = service.getMembers();
        return ResponseEntity.ok(members);
    }

    /** Ajoute un nouveau membre */
    @Operation(summary = "Ajouter un membre", description = "Ajoute un membre a l'equipe. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "201", description = "Membre cree")
    @PostMapping("/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeModel> addMember(@RequestBody @NonNull EquipeModel member) {
        Objects.requireNonNull(member, "EquipeModel cannot be null");
        EquipeModel saved = service.addMember(member);
        return ResponseEntity.status(201).body(saved);
    }

    /** Met à jour un membre existant */
    @Operation(summary = "Mettre a jour un membre", description = "Met a jour les informations d'un membre de l'equipe. Necessite ROLE_ADMIN.")
    @PutMapping("/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMember(@PathVariable @NonNull Long id, @RequestBody @NonNull EquipeModel member) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(member, "EquipeModel cannot be null");
        if (!id.equals(member.getId())) {
            throw new BusinessException("L'id du membre dans le corps ne correspond pas a l'id de l'URL");
        }
        EquipeModel updated = service.updateMember(member);
        return ResponseEntity.ok(updated);
    }

    /** Supprime un membre par son ID */
    @Operation(summary = "Supprimer un membre", description = "Supprime un membre de l'equipe. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Membre supprime")
    @DeleteMapping("/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable @NonNull Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        service.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload de la photo d'un membre.
     * Crée le dossier si nécessaire.
     * Met à jour la photo dans la base (URL) et stocke l'image sur disque.
     * 
     * @param id   ID du membre
     * @param file fichier image multipart/form-data "file"
     * @return URL publique d'accès à la photo uploadée
     */
    @PostMapping("/members/{id}/upload-photo")
    @Operation(summary = "Uploader la photo d'un membre", description = "Charge une photo pour un membre et met a jour son URL. Necessite ROLE_ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadMemberPhoto(@PathVariable @NonNull Long id,
            @RequestParam("file") MultipartFile file) {
        Objects.requireNonNull(id, "ID cannot be null");
        try {
            String imageUrl = service.uploadMemberPhoto(id, file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de l'upload de la photo du membre", e);
        }
    }

    /**
     * Suppression de la photo d'un membre :
     * - supprime physiquement le fichier sur disque
     * - supprime l'URL photo dans la base
     */
    @DeleteMapping("/members/{id}/delete-photo")
    @Operation(summary = "Supprimer la photo d'un membre", description = "Supprime la photo associee a un membre. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Photo du membre supprimee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMemberPhoto(@PathVariable @NonNull Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        try {
            service.deleteMemberPhoto(id);
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de la suppression de la photo du membre", e);
        }
        return ResponseEntity.noContent().build();
    }

    // ----------- Endpoints pour servir les images (bannière et photos membres)
    // -----------

    /**
     * Sert une image bannière à partir du nom de fichier.
     * Exemple URL : GET /api/equipe/images/banner/nomFichier.jpg
     */
    @Operation(summary = "Lire une image de banniere", description = "Sert une image de banniere equipe par son nom de fichier. Acces public.")
    @GetMapping("/images/banner/{filename:.+}")
    public ResponseEntity<Resource> getBannerImage(@PathVariable @NonNull String filename) {
        return serveImageFile(bannersDir, filename);
    }

    /**
     * Sert une photo de membre à partir du nom de fichier.
     * Exemple URL : GET /api/equipe/images/members/nomFichier.jpg
     */
    @Operation(summary = "Lire une photo de membre", description = "Sert la photo d'un membre de l'equipe par son nom de fichier. Acces public.")
    @GetMapping("/images/members/{filename:.+}")
    public ResponseEntity<Resource> getMemberPhoto(@PathVariable @NonNull String filename) {
        return serveImageFile(membersDir, filename);
    }

    /**
     * Méthode utilitaire pour servir un fichier image à partir d'un dossier donné.
     * Gère les erreurs (fichier non trouvé, non lisible).
     */
    private ResponseEntity<Resource> serveImageFile(Path directory, @NonNull String filename) {
        Objects.requireNonNull(directory, "Directory path cannot be null");
        Path file = directory.resolve(filename);
        try {
            if (!Files.exists(file) || !Files.isReadable(file)) {
                throw new ResourceNotFoundException("Image introuvable");
            }

            Resource resource = new UrlResource(Objects.requireNonNull(file.toUri(), "URI cannot be null"));

            // Tentative de détermination du type MIME
            String contentType = "application/octet-stream";
            try {
                contentType = Objects.requireNonNull(Files.probeContentType(file), "Content type cannot be null");
            } catch (IOException ignored) {
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new BusinessException("Chemin d'image invalide");
        }
    }
}
