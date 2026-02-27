package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shake_art.back.dto.ArtisteDto;
import com.shake_art.back.model.ArtisteModel;
import com.shake_art.back.model.ArtisteType;
import com.shake_art.back.model.Photo;
import com.shake_art.back.repository.ArtisteRepository;
import com.shake_art.back.repository.PhotoRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Objects;

@Service
public class ArtisteService {

    @Autowired
    private ArtisteRepository artisteRepository;

    @Autowired
    private PhotoRepository photoRepository;


    /**
     * Crée un nouvel artiste avec photo de profil et galerie.
     */
    public ArtisteModel createArtiste(String name, String discipline, String bio, String type, MultipartFile photoProfil, List<MultipartFile> galleryPhotos) throws IOException {
        String profileFilename = null;

        if (photoProfil != null && !photoProfil.isEmpty()) {
            profileFilename = savePhoto(photoProfil, "profil", null);
        }

        ArtisteModel artiste = new ArtisteModel();
        artiste.setName(name);
        artiste.setDiscipline(discipline);
        artiste.setBio(bio);
        artiste.setType(ArtisteType.valueOf(type));
        artiste.setPhotoProfil(profileFilename);
        artiste = artisteRepository.save(artiste);

        if (galleryPhotos != null) {
            for (MultipartFile galleryPhoto : galleryPhotos) {
                if (!galleryPhoto.isEmpty()) {
                    String galleryFilename = savePhoto(galleryPhoto, "galerie", artiste.getId());
                    Photo photo = new Photo();
                    photo.setFilename(galleryFilename);
                    photo.setArtiste(artiste);
                    photoRepository.save(photo);
                }
            }
        }

        return artiste;
    }

    public Optional<ArtisteModel> getArtisteById(Long id) {
        Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        return artisteRepository.findById(id);
    }

    public List<ArtisteModel> getAllArtistes() {
        return artisteRepository.findAll();
    }

    public ArtisteModel updateArtiste(Long id, String name, String discipline, String bio, String type, MultipartFile photoProfil) throws IOException {
        Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        Objects.requireNonNull(name, "Le nom ne peut pas être nul");
        Objects.requireNonNull(discipline, "La discipline ne peut pas être nulle");
        Objects.requireNonNull(bio, "La biographie ne peut pas être nulle");
        Objects.requireNonNull(type, "Le type ne peut pas être nul");

        ArtisteModel artiste = artisteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Artiste non trouvé"));

        artiste.setName(name);
        artiste.setDiscipline(discipline);
        artiste.setBio(bio);
        artiste.setType(ArtisteType.valueOf(type));

        if (photoProfil != null && !photoProfil.isEmpty()) {
            if (artiste.getPhotoProfil() != null) {
                deletePhoto(artiste.getPhotoProfil());
            }
            String newPhoto = savePhoto(photoProfil, "profil", null);
            artiste.setPhotoProfil(newPhoto);
        }

        return artisteRepository.save(artiste);
    }

    public void deleteArtiste(Long id) {
        Objects.requireNonNull(id, "id cannot be null");
        ArtisteModel artiste = artisteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Artiste non trouvé"));

        // Supprime photo de profil
        if (artiste.getPhotoProfil() != null) {
            deletePhoto(artiste.getPhotoProfil());
        }

        // Supprime galerie
        List<Photo> photos = photoRepository.findByArtisteId(id);
        Objects.requireNonNull(photos, "photos cannot be null");
        for (Photo photo : photos) {
            deletePhoto(photo.getFilename());
        }

        photoRepository.deleteAll(photos);
        artisteRepository.deleteById(id);
    }

    public Photo addGalleryPhoto(Long artisteId, MultipartFile file) throws IOException {
        Objects.requireNonNull(artisteId, "artisteId cannot be null");
        Objects.requireNonNull(file, "file cannot be null");

        ArtisteModel artiste = artisteRepository.findById(artisteId).orElseThrow(() -> new IllegalArgumentException("Artiste non trouvé"));

        String filename = savePhoto(file, "galerie", artisteId);
        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setArtiste(artiste);
        return photoRepository.save(photo);
    }

    public List<Photo> getGalleryPhotos(Long artisteId) {
        Objects.requireNonNull(artisteId, "artisteId cannot be null");
        return photoRepository.findByArtisteId(artisteId);
    }

    public void deleteGalleryPhoto(Long photoId) {
        Objects.requireNonNull(photoId, "photoId cannot be null");
        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> new IllegalArgumentException("Photo non trouvée"));

        deletePhoto(photo.getFilename());
        photoRepository.deleteById(photoId);
    }

    public byte[] getPhotoProfilData(Long artisteId) throws IOException {
        Objects.requireNonNull(artisteId, "artisteId cannot be null");
        ArtisteModel artiste = artisteRepository.findById(artisteId).orElseThrow(() -> new IllegalArgumentException("Artiste non trouvé"));
        if (artiste.getPhotoProfil() == null) throw new IllegalArgumentException("Photo de profil non trouvée");

        Path path = Paths.get("uploads", artiste.getPhotoProfil());
        return Files.readAllBytes(path);
    }

    public String getPhotoProfilContentType(Long artisteId) throws IOException {
        Objects.requireNonNull(artisteId, "artisteId cannot be null");
        ArtisteModel artiste = artisteRepository.findById(artisteId).orElseThrow(() -> new IllegalArgumentException("Artiste non trouvé"));
        if (artiste.getPhotoProfil() == null) throw new IllegalArgumentException("Photo de profil non trouvée");

        Path path = Paths.get("uploads", artiste.getPhotoProfil());
        return Files.probeContentType(path);
    }

    private String savePhoto(MultipartFile file, String type, Long artisteId) throws IOException {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())).replaceAll("\\s+", "_");
        String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String baseName = UUID.randomUUID() + "_" + originalFilename.replace(extension, "");
        String filename = baseName + extension;

        Path baseDir = switch (type) {
            case "profil" -> Paths.get("uploads/photos/profil");
            case "galerie" -> Paths.get("uploads/photos/galerie", String.valueOf(artisteId));
            default -> throw new IllegalArgumentException("Type de dossier inconnu");
        };

        Files.createDirectories(baseDir);
        Path destination = baseDir.resolve(filename);
        Files.write(destination, file.getBytes());

        // 🔥 Retourne le chemin relatif pour accéder à l'image via /uploads/**
        return baseDir.subpath(1, baseDir.getNameCount()).resolve(filename).toString().replace("\\", "/");
    }

    private void deletePhoto(String relativePath) {
        try {
            Files.deleteIfExists(Paths.get("uploads", relativePath));
        } catch (IOException e) {
            System.err.println("Erreur suppression photo : " + e.getMessage());
        }
    }

    public ArtisteModel saveArtiste(ArtisteModel artiste) {
        Objects.requireNonNull(artiste, "ArtisteModel cannot be null");
        return artisteRepository.save(artiste);
    }

    public ArtisteModel save(ArtisteModel artiste) {
        Objects.requireNonNull(artiste, "ArtisteModel cannot be null");
        return artisteRepository.save(artiste);
    }

    /**
     * Convertit le modèle vers un DTO en générant les URL publiques.
     */
    public ArtisteDto toDto(ArtisteModel artiste) {
        ArtisteDto dto = new ArtisteDto();
        dto.setId(artiste.getId());
        dto.setName(artiste.getName());
        dto.setDiscipline(artiste.getDiscipline());
        dto.setBio(artiste.getBio());
        dto.setType(artiste.getType().name());
        dto.setAnneeArchive(artiste.getAnneeArchive());

        // URL de la photo de profil
        if (artiste.getPhotoProfil() != null) {
            dto.setPhotoProfilUrl("/uploads/" + artiste.getPhotoProfil());
        }

        // URLs des photos de galerie
        dto.setGalleryUrls(
                artiste.getPhotos().stream()
                        .map(photo -> "/uploads/" + photo.getFilename())
                        .toList()
        );

        return dto;
    }
}
