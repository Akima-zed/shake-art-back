package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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

    public Partenaire create(String nom, String description, String siteWeb, MultipartFile logoFile) throws IOException {
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

    public Optional<Partenaire> getOne(Long id) {
        Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        return partenaireRepository.findById(id);
    }

    public Partenaire update(Long id, String nom, String description, String siteWeb, MultipartFile logoFile) throws IOException {
        Objects.requireNonNull(id, "L'identifiant ne peut pas être nul");
        Partenaire p = partenaireRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Partenaire introuvable"));

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

    public void delete(Long id) {
        Partenaire p = partenaireRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Partenaire introuvable"));
        if (p.getLogo() != null) {
            deleteLogo(p.getLogo());
        }
        partenaireRepository.deleteById(id);
    }

    /* === Gestion des fichiers logo === */

    private String saveLogo(MultipartFile file) throws IOException {
        validateFile(file);
        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())).replaceAll("\\s+", "_");
        Path directory = Paths.get(LOGO_DIRECTORY);
        Files.createDirectories(directory);
        Path destination = directory.resolve(filename);
        Files.write(destination, file.getBytes());
        return "logos/" + filename;
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale autorisée de 5 Mo.");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Type de fichier non valide. Seuls les fichiers image sont autorisés.");
        }
    }

    private void deleteLogo(String relativePath) {
        try {
            Files.deleteIfExists(Paths.get(LOGO_DIRECTORY, relativePath));
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
