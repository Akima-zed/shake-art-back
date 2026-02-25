package com.shake_art.back.controller;

import com.shake_art.back.model.AccueilContent;
import com.shake_art.back.model.CardPresentation;
import com.shake_art.back.repository.CardPresentationRepository;
import com.shake_art.back.service.AccueilContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer le contenu de la page d'accueil,
 * incluant la récupération, mise à jour, upload vidéo Hero et images des cartes.
 */
@RestController
@RequestMapping("/accueil")
@CrossOrigin("*")
@Tag(name = "Accueil", description = "API pour gérer le contenu de la page d’accueil")
public class AccueilContentController {

    @Autowired
    private AccueilContentService service;

    @Autowired
    private CardPresentationRepository cardRepository;

    /**
     * Répertoire de stockage des images des cartes
     */
    private static final String UPLOAD_DIR_CARDS = "uploads/cards";

    /**
     * Répertoire de stockage des vidéos Hero
     */
    private static final String UPLOAD_DIR_VIDEOS = "uploads/videos";

    /**
     * Récupère le contenu de la page d'accueil (singleton)
     * @return AccueilContent ou 404 si non trouvé
     */
    @Operation(summary = "Récupérer le contenu de la page d’accueil")
    @GetMapping
    public ResponseEntity<AccueilContent> getAccueilContent() {
        return service.getContent()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Met à jour le contenu complet de la page d'accueil
     * @param content contenu mis à jour
     * @return contenu sauvegardé
     */
    @Operation(summary = "Mettre à jour le contenu de la page d’accueil")
    @PutMapping
    public ResponseEntity<AccueilContent> updateAccueilContent(@RequestBody AccueilContent content) {
        return ResponseEntity.ok(service.saveContent(content));
    }

    /**
     * Upload d’une vidéo pour la section Hero
     * @param video fichier vidéo envoyé (clé 'video')
     * @return URL publique de la vidéo ou message d’erreur
     */
    @Operation(summary = "Uploader une vidéo pour la section hero")
    @PostMapping("/upload-video")
    public ResponseEntity<String> uploadHeroVideo(@RequestParam("video") MultipartFile video) {
        if (video.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vidéo manquant");
        }

        try {
            // Création du dossier upload vidéos s'il n'existe pas
            File dir = new File(UPLOAD_DIR_VIDEOS);
            if (!dir.exists()) dir.mkdirs();

            // Génération du nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + Path.of(video.getOriginalFilename()).getFileName().toString();
            File destination = new File(dir, fileName);

            // Sauvegarde physique du fichier
            video.transferTo(destination);

            // URL relative accessible via serveur web
            String fileUrl = "/" + UPLOAD_DIR_VIDEOS + "/" + fileName;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }
    /**
     * Upload d'une image pour une carte précise identifiée par son ID
     * @param id identifiant unique de la carte
     * @param file fichier image (clé 'file' obligatoire)
     * @return URL publique de l'image sauvegardée ou message d'erreur
     */
    @Operation(summary = "Uploader une image pour une carte spécifique")
    @PostMapping("/cards/{id}/image")
    public ResponseEntity<String> uploadCardImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Vérifie que le fichier n'est pas vide
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Fichier vide");
            }

            // Recherche la carte concernée
            CardPresentation card = cardRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Carte non trouvée"));

            // Crée le dossier d'upload s'il n'existe pas
            Path uploadPath = Paths.get(UPLOAD_DIR_CARDS);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Génère un nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + Path.of(file.getOriginalFilename()).getFileName().toString();
            Path filePath = uploadPath.resolve(fileName);

            // Copie le fichier vers le dossier cible
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construit l'URL publique complète de l'image
            String imageRelativePath = "/uploads/cards/" + fileName;
            String fullImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(imageRelativePath)
                    .toUriString();

            // Met à jour la carte et sauvegarde
            card.setImage(fullImageUrl);
            cardRepository.save(card);

            // Renvoie l'URL pour affichage dans le frontend
            return ResponseEntity.ok(fullImageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Créer une nouvelle carte")
    @PostMapping("/cards")
    public ResponseEntity<CardPresentation> createCard(@RequestBody CardPresentation card) {
        Optional<AccueilContent> optional = service.getContent();
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AccueilContent content = optional.get();
        card.setAccueilContent(content);
        CardPresentation saved = cardRepository.save(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }




    // TODO: Ajouter d'autres endpoints si nécessaire (ex: suppression image, gestion cartes...)

}
