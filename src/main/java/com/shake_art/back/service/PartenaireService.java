package com.shake_art.back.service;

import com.shake_art.back.exception.BusinessException;
import com.shake_art.back.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.NonNull;

import com.shake_art.back.model.PartenaireContent;
import com.shake_art.back.repository.PartenaireContentRepository;
import com.shake_art.back.repository.PartenaireRepository;
import com.shake_art.back.model.Partenaire;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PartenaireService {

    @Autowired
    private PartenaireRepository partenaireRepository;
    @Autowired
    private PartenaireContentRepository partenaireContentRepository;

    private static final String LOGO_DIRECTORY = "uploads/logos";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public Partenaire create(String nom, String description, String siteWeb, MultipartFile logoFile)
            throws IOException {
        String logoPath = null;
        if (logoFile != null && !logoFile.isEmpty()) {
            logoPath = saveLogo(logoFile);
        }

        Partenaire p = new Partenaire();
        p.setNom(nom);
        p.setDescription(description);
        p.setSiteWeb(siteWeb);
        p.setLogo(logoPath);

        return partenaireRepository.save(p);
    }

    public List<Partenaire> getAll() {
        return partenaireRepository.findAll();
    }

    public Optional<Partenaire> getOne(@NonNull Long id) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        return partenaireRepository.findById(id);
    }

    public Partenaire update(@NonNull Long id, String nom, String description, String siteWeb, MultipartFile logoFile)
            throws IOException {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        Long validatedId = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        Partenaire p = partenaireRepository.findById(validatedId)
            .orElseThrow(() -> new ResourceNotFoundException("Partenaire introuvable"));

        Objects.requireNonNull(nom, "Le nom ne peut pas être nul");
        Objects.requireNonNull(description, "La description ne peut pas être nulle");

        p.setNom(nom);
        p.setDescription(description);
        p.setSiteWeb(siteWeb);

        if (logoFile != null && !logoFile.isEmpty()) {
            if (p.getLogo() != null) {
                deleteLogo(p.getLogo());
            }
            p.setLogo(saveLogo(logoFile));
        }

        return partenaireRepository.save(p);
    }

    public void delete(@NonNull Long id) {
        id = Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        Partenaire p = partenaireRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partenaire introuvable"));
        if (p.getLogo() != null) {
            deleteLogo(p.getLogo());
        }
        partenaireRepository.deleteById(id);
    }

    /* === Gestion des fichiers logo === */

    private String saveLogo(MultipartFile file) throws IOException {
        validateFile(file);
        String filename = UUID.randomUUID() + "_"
                + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())).replaceAll("\\s+", "_");
        Path directory = Paths.get(LOGO_DIRECTORY);
        Files.createDirectories(directory);
        Path destination = directory.resolve(filename);
        Files.write(destination, file.getBytes());
        return "logos/" + filename;
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Le fichier depasse la taille maximale autorisee de 5 Mo");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new BusinessException("Type de fichier non valide. Seuls les fichiers image sont autorises");
        }
    }

    private void deleteLogo(String relativePath) {
        try {
            String normalized = relativePath.startsWith("logos/")
                    ? relativePath.substring("logos/".length())
                    : relativePath;
            Files.deleteIfExists(Paths.get(LOGO_DIRECTORY, normalized));
        } catch (IOException e) {
            System.err.println("Erreur suppression logo : " + e.getMessage());
        }
    }

    /* === Gestion du contenu éditable de la page partenaires (titre + texte) === */

    public PartenaireContent getContent() {
        // Récupère le premier contenu, ou crée un contenu par défaut s'il n'existe pas
        return partenaireContentRepository.findAll().stream().findFirst().orElseGet(() -> {
            PartenaireContent content = new PartenaireContent();
            content.setTitre("Nos partenaires");
            content.setTexte("Découvrez nos partenaires qui nous soutiennent.");
            return partenaireContentRepository.save(content);
        });
    }

    public PartenaireContent updateContent(PartenaireContent updatedContent) {
        // Charge le contenu existant pour garder l'id
        PartenaireContent existingContent = getContent();
        existingContent.setTitre(updatedContent.getTitre());
        existingContent.setTexte(updatedContent.getTexte());
        return partenaireContentRepository.save(existingContent);
    }

}
