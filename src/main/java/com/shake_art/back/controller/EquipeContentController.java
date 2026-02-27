package com.shake_art.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class EquipeContentController {

    @Autowired
    private EquipeContentService service;

    // Dossiers physiques sur disque où sont stockées les images
    private final Path bannersDir = Paths.get("uploads/equipe/banners");
    private final Path membersDir = Paths.get("uploads/equipe/members");

    // ----------- Contenu principal Équipe -----------

    /** Récupère le contenu principal équipe (texte + url bannière) */
    @GetMapping(value = "/content", produces = "application/json")
    public ResponseEntity<EquipeContent> getContent() {
        return service.getContent()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Crée un nouveau contenu principal équipe */
    @PostMapping("/content")
    public ResponseEntity<EquipeContent> createContent(@RequestBody EquipeContent content) {
        EquipeContent saved = service.saveOrUpdate(content);
        return ResponseEntity.status(201).body(saved);
    }

    /** Met à jour un contenu principal équipe existant */
    @PutMapping("/content")
    public ResponseEntity<?> updateContent(@RequestBody EquipeContent content) {
        if (content.getId() == null) {
            return ResponseEntity.badRequest().body("L'id du contenu est obligatoire pour la mise à jour");
        }
        EquipeContent updated = service.saveOrUpdate(content);
        return ResponseEntity.ok(updated);
    }

    /** Supprime un contenu principal par son ID */
    @DeleteMapping("/content/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    /**
     * Upload d'une image bannière pour la page équipe
     * Création automatique du dossier si nécessaire.
     * @param file fichier image (champ form-data "file")
     * @return URL publique (endpoint) pour accéder à cette image
     */
    @PostMapping("/content/upload-banner")
    public ResponseEntity<?> uploadBanner(@RequestParam("file") MultipartFile file) {
        try {
            // Upload physique + mise à jour url dans la base
            String imageUrl = service.uploadBannerImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de l'upload de la bannière : " + e.getMessage());
        }
    }

    /**
     * Suppression de la bannière :
     * - supprime physiquement le fichier image sur disque
     * - supprime l'URL dans la base
     */
    @DeleteMapping("/content/delete-banner")
    public ResponseEntity<?> deleteBanner() throws IOException {
        service.deleteBannerImage();
        return ResponseEntity.ok("Bannière supprimée avec succès");
    }

    // ----------- Gestion des membres -----------

    /** Récupère la liste complète des membres de l'équipe */
    @GetMapping(value = "/members", produces = "application/json")
    public ResponseEntity<List<EquipeModel>> getMembers() {
        List<EquipeModel> members = service.getMembers();
        return ResponseEntity.ok(members);
    }

    /** Ajoute un nouveau membre */
    @PostMapping("/members")
    public ResponseEntity<EquipeModel> addMember(@RequestBody EquipeModel member) {
        EquipeModel saved = service.addMember(member);
        return ResponseEntity.status(201).body(saved);
    }

    /** Met à jour un membre existant */
    @PutMapping("/members/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody EquipeModel member) {
        if (!id.equals(member.getId())) {
            return ResponseEntity.badRequest().body("L'id du membre dans le corps ne correspond pas à l'id de l'URL");
        }
        EquipeModel updated = service.updateMember(member);
        return ResponseEntity.ok(updated);
    }

    /** Supprime un membre par son ID */
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        service.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload de la photo d'un membre.
     * Crée le dossier si nécessaire.
     * Met à jour la photo dans la base (URL) et stocke l'image sur disque.
     * @param id ID du membre
     * @param file fichier image multipart/form-data "file"
     * @return URL publique d'accès à la photo uploadée
     */
    @PostMapping("/members/{id}/upload-photo")
    public ResponseEntity<?> uploadMemberPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = service.uploadMemberPhoto(id, file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de l'upload de la photo du membre : " + e.getMessage());
        }
    }

    /**
     * Suppression de la photo d'un membre :
     * - supprime physiquement le fichier sur disque
     * - supprime l'URL photo dans la base
     */
    @DeleteMapping("/members/{id}/delete-photo")
    public ResponseEntity<?> deleteMemberPhoto(@PathVariable Long id) throws IOException {
        service.deleteMemberPhoto(id);
        return ResponseEntity.ok("Photo du membre supprimée avec succès");
    }

    // ----------- Endpoints pour servir les images (bannière et photos membres) -----------

    /**
     * Sert une image bannière à partir du nom de fichier.
     * Exemple URL : GET /api/equipe/images/banner/nomFichier.jpg
     */
    @GetMapping("/images/banner/{filename:.+}")
    public ResponseEntity<Resource> getBannerImage(@PathVariable @NonNull String filename) {
        return serveImageFile(bannersDir, filename);
    }

    /**
     * Sert une photo de membre à partir du nom de fichier.
     * Exemple URL : GET /api/equipe/images/members/nomFichier.jpg
     */
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
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toUri());

            // Tentative de détermination du type MIME
            String contentType = "application/octet-stream";
            try {
                contentType = Files.probeContentType(file);
            } catch (IOException ignored) {}

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
