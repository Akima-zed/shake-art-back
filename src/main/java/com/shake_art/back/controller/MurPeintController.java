package com.shake_art.back.controller;

import com.shake_art.back.dto.MurPeintDto;
import com.shake_art.back.model.MurPeint;
import com.shake_art.back.repository.ArtisteRepository;
import com.shake_art.back.repository.MurPeintRepository;
import com.shake_art.back.service.ArtisteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Objects;

import com.shake_art.back.model.ArtisteModel;

@RestController
@RequestMapping("/murs")
@CrossOrigin("*")
public class MurPeintController {

    private static final String UPLOAD_DIR = "uploads/photos/fresques"; // Dossier de destination des fresques

    private final MurPeintRepository murPeintRepository;
    private final ArtisteService artisteService;
    private final ArtisteRepository artisteRepository;

    public MurPeintController(MurPeintRepository murPeintRepository,    ArtisteService artisteService,                              ArtisteRepository artisteRepository) {
        this.murPeintRepository = murPeintRepository;
        this.artisteService = artisteService;
        this.artisteRepository = artisteRepository;
    }

    // 📌 GET: Tous les murs ou par année
    @GetMapping
    public List<MurPeintDto> getAll(@RequestParam(required = false) Integer annee) {
        List<MurPeint> murs = (annee != null) ? murPeintRepository.findByAnnee(annee) : murPeintRepository.findAll();
        return murs.stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MurPeintDto> getMurPeint(@PathVariable @NonNull Long id) {
        Optional<MurPeint> mur = murPeintRepository.findByIdWithArtiste(id);
        return mur.map(value -> ResponseEntity.ok(toDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 📌 POST: Création d’un mur peint
    @PostMapping
    public ResponseEntity<MurPeintDto> create(@RequestBody MurPeint mur) {
        if (mur.getArtiste() != null && mur.getArtiste().getId() != null) {
            Objects.requireNonNull(mur.getArtiste().getId(), "L'identifiant de l'artiste ne peut pas être nul");
            Optional<ArtisteModel> artiste = artisteRepository.findById(mur.getArtiste().getId());
            artiste.ifPresent(mur::setArtiste);
        } else {
            mur.setArtiste(null);
        }
        MurPeint saved = murPeintRepository.save(mur);
        return ResponseEntity.ok(toDto(saved));
    }


    // 📌 PUT: Mise à jour d’un mur peint
    @PutMapping("/{id}")
    public ResponseEntity<MurPeintDto> update(@PathVariable @NonNull Long id, @RequestBody MurPeint murDetails) {
        Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        return murPeintRepository.findById(id).map(mur -> {
            mur.setNom(murDetails.getNom());
            mur.setLatitude(murDetails.getLatitude());
            mur.setLongitude(murDetails.getLongitude());
            mur.setDescription(murDetails.getDescription());
            mur.setPhotoUrl(murDetails.getPhotoUrl());
            mur.setAnnee(murDetails.getAnnee());

            if (murDetails.getArtiste() != null && murDetails.getArtiste().getId() != null) {
                Objects.requireNonNull(murDetails.getArtiste().getId(), "L'identifiant de l'artiste ne peut pas être nul");
                Optional<ArtisteModel> artiste = artisteRepository.findById(murDetails.getArtiste().getId());
                artiste.ifPresent(mur::setArtiste);
            } else {
                mur.setArtiste(null);
            }

            MurPeint updated = murPeintRepository.save(mur);
            return ResponseEntity.ok(toDto(updated));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    // 📌 DELETE: Suppression d’un mur peint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        if (!murPeintRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        murPeintRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 📌 GET: Test API
    @GetMapping("/test")
    public String test() {
        return "API murs OK";
    }

    // 📌 POST: Upload manuel d’une photo de fresque
    @PostMapping("/upload-photo")
    public ResponseEntity<UploadResponse> uploadPhoto(@RequestParam("file") MultipartFile file) {
        Objects.requireNonNull(file, "File cannot be null");
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename(), "Original filename cannot be null"));
            String ext = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalFilename.substring(dotIndex);
            }
            String filename = UUID.randomUUID().toString() + ext;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(new UploadResponse(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ ✅ ✅ POST: Copier une photo depuis la galerie vers le dossier des fresques
    @PostMapping("/{id}/valider-photo")
    public ResponseEntity<String> validerPhotoDepuisGalerie(@PathVariable @NonNull Long id, @RequestBody String nomFichier) {
        Objects.requireNonNull(id, "ID cannot be null");
        try {
            nomFichier = nomFichier.replace("\"", ""); // Supprimer les guillemets éventuels

            Path sourcePath = Paths.get("uploads/photos/galerie", nomFichier); // Source = galerie
            Path destinationPath = Paths.get("uploads/photos/fresques", nomFichier); // Destination = fresques

            Files.createDirectories(destinationPath.getParent()); // Crée le dossier cible si besoin
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING); // Copie le fichier

            return ResponseEntity.ok("Photo copiée dans le dossier fresques.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la copie du fichier : " + e.getMessage());
        }
    }

    // 📌 DTO Upload (réponse après upload)
    public static class UploadResponse {
        private String filename;

        public UploadResponse(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    // 📌 Conversion Entity vers DTO
    private MurPeintDto toDto(MurPeint mur) {
        MurPeintDto dto = new MurPeintDto();
        dto.setId(mur.getId());
        dto.setNom(mur.getNom());
        dto.setLatitude(mur.getLatitude());
        dto.setLongitude(mur.getLongitude());
        dto.setDescription(mur.getDescription());
        dto.setPhotoUrl("/uploads/" + mur.getPhotoUrl());
        dto.setAnnee(mur.getAnnee());

        if (mur.getArtiste() != null) {
            dto.setArtiste(artisteService.toDto(mur.getArtiste()));
        }
        return dto;
    }
}
