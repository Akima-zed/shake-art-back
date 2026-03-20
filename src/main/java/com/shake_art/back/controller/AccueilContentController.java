package com.shake_art.back.controller;

import com.shake_art.back.model.AccueilContent;
import com.shake_art.back.model.CardPresentation;
import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.exception.TechnicalException;
import com.shake_art.back.repository.CardPresentationRepository;
import com.shake_art.back.service.AccueilContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

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
        AccueilContent content = service.getContent()
            .orElseThrow(() -> new ResourceNotFoundException("Contenu d'accueil introuvable"));
        return ResponseEntity.ok(content);
    }

    /**
     * Met à jour le contenu complet de la page d'accueil
     * @param content contenu mis à jour
     * @return contenu sauvegardé
     */
    @Operation(summary = "Mettre à jour le contenu de la page d’accueil", description = "Met a jour les textes et donnees de la page d'accueil. Necessite ROLE_ADMIN.")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccueilContent> updateAccueilContent(@RequestBody AccueilContent content) {
        return ResponseEntity.ok(service.saveContent(content));
    }

    /**
     * Upload d’une vidéo pour la section Hero
     * @param video fichier vidéo envoyé (clé 'video')
     * @return URL publique de la vidéo ou message d’erreur
     */
    @Operation(summary = "Uploader une vidéo pour la section hero", description = "Charge une nouvelle video hero et retourne son URL publique. Necessite ROLE_ADMIN.")
    @PostMapping("/upload-video")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadHeroVideo(@RequestParam("video") MultipartFile video) {
        if (video.isEmpty()) {
            throw new BusinessException("Fichier video manquant");
        }

        try {
            // Création du dossier upload vidéos s'il n'existe pas
            Path uploadPath = Paths.get(UPLOAD_DIR_VIDEOS);
            Files.createDirectories(uploadPath);

            // Génération du nom de fichier unique
            String originalFilename = Objects.requireNonNull(video.getOriginalFilename(), "Nom de fichier invalide");
            String fileName = System.currentTimeMillis() + "_" + Path.of(originalFilename).getFileName();
            Path destination = uploadPath.resolve(fileName);

            // Sauvegarde physique du fichier
            video.transferTo(destination.toFile());

            // URL relative accessible via serveur web
            String fileUrl = "/" + UPLOAD_DIR_VIDEOS + "/" + fileName;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de l'upload de la video", e);
        }
    }
    /**
     * Upload d'une image pour une carte précise identifiée par son ID
     * @param id identifiant unique de la carte
     * @param file fichier image (clé 'file' obligatoire)
     * @return URL publique de l'image sauvegardée ou message d'erreur
     */
    @Operation(summary = "Uploader une image pour une carte spécifique", description = "Associe une image a une carte de presentation. Necessite ROLE_ADMIN.")
    @PostMapping("/cards/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadCardImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Objects.requireNonNull(id, "ID cannot be null");

        try {
            // Vérifie que le fichier n'est pas vide
            if (file.isEmpty()) {
                throw new BusinessException("Fichier vide");
            }

            // Recherche la carte concernée
            CardPresentation card = cardRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Carte non trouvee"));

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
            throw new TechnicalException("Erreur lors de l'upload de l'image de carte", e);
        }
    }

    @Operation(summary = "Créer une nouvelle carte", description = "Cree une nouvelle carte de presentation sur la page d'accueil. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "201", description = "Carte creee")
    @PostMapping("/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardPresentation> createCard(@RequestBody CardPresentation card) {
        AccueilContent content = service.getContent()
                .orElseThrow(() -> new ResourceNotFoundException("Contenu d'accueil introuvable"));
        card.setAccueilContent(content);
        CardPresentation saved = cardRepository.save(card);
        return ResponseEntity.status(201).body(saved);
    }




    // TODO: Ajouter d'autres endpoints si nécessaire (ex: suppression image, gestion cartes...)

}
