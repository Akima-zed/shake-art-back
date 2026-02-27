package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shake_art.back.model.EquipeContent;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.repository.EquipeContentRepository;
import com.shake_art.back.repository.EquipeMemberRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class EquipeContentService {

    // Répertoires physiques d'upload (à adapter si besoin)
    private static final String UPLOAD_DIR_BANNERS = "uploads/equipe/banners";
    private static final String UPLOAD_DIR_MEMBERS = "uploads/equipe/members";

    @Autowired
    private EquipeContentRepository contentRepository;

    @Autowired
    private EquipeMemberRepository memberRepository;

    // --- Gestion contenu principal équipe ---

    /**
     * Récupère le contenu équipe, crée un contenu par défaut si absent
     */
    public Optional<EquipeContent> getContent() {
        Optional<EquipeContent> optional = contentRepository.findById(1L);

        if (optional.isEmpty()) {
            EquipeContent content = new EquipeContent();
            content.setId(1L);
            content.setPresentationText("Présentation de l'équipe...");
            content.setBannerImageUrl(null);
            contentRepository.save(content);
            return Optional.of(content);
        }

        return optional;
    }

    /**
     * Sauvegarde ou met à jour le contenu équipe (singleton ID=1)
     */
    public EquipeContent saveOrUpdate(com.shake_art.back.model.EquipeContent content) {
        content.setId(1L);
        return contentRepository.save(content);
    }

    /**
     * Supprime un contenu par son ID
     */
    public boolean deleteById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        if (contentRepository.existsById(id)) {
            contentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- Gestion membres ---

    /**
     * Liste tous les membres
     */
    public List<EquipeModel> getMembers() {
        return memberRepository.findAll();
    }

    /**
     * Ajoute un nouveau membre
     */
    public EquipeModel addMember(EquipeModel member) {
        return memberRepository.save(member);
    }

    /**
     * Met à jour un membre existant
     */
    public EquipeModel updateMember(EquipeModel member) {
        if (member.getId() == null || !memberRepository.existsById(member.getId())) {
            throw new IllegalArgumentException("Membre non trouvé ou id manquant");
        }
        return memberRepository.save(member);
    }

    /**
     * Supprime un membre par son ID
     * Note : supprime aussi la photo sur disque si existante
     */
    public void deleteMember(Long id) {
        memberRepository.findById(id).ifPresent(member -> {
            if (member.getPhotoUrl() != null) {
                deleteFileIfExists(member.getPhotoUrl());
            }
        });
        memberRepository.deleteById(id);
    }

    // --- Upload bannière équipe ---

    /**
     * Upload physique de la bannière + sauvegarde uniquement le nom du fichier dans contenu équipe.
     * Retourne l'URL complète publique pour accéder à cette bannière (endpoint REST).
     *
     * @param file fichier image multipart/form-data
     * @return URL complète d'accès à la bannière
     * @throws IOException
     */
    public String uploadBannerImage(MultipartFile file) throws IOException {
        // Crée le dossier s'il n'existe pas
        Files.createDirectories(Paths.get(UPLOAD_DIR_BANNERS));

        // Génère un nom de fichier unique basé sur timestamp + nom original
        String fileName = System.currentTimeMillis() + "_" + Path.of(file.getOriginalFilename()).getFileName().toString();
        Path filePath = Paths.get(UPLOAD_DIR_BANNERS, fileName);

        // Enregistre le fichier sur disque (remplace s'il existe)
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Récupère le contenu actuel
        EquipeContent content = getContent().orElseThrow(() -> new IllegalStateException("Contenu équipe introuvable"));

        // Supprime l'ancienne bannière physique si différente pour éviter accumulation
        String oldFileName = content.getBannerImageUrl();
        if (oldFileName != null && !oldFileName.equals(fileName)) {
            deleteFileIfExists("/api/equipe/images/banner/" + oldFileName);
        }

        // Sauvegarde uniquement le nom du fichier en base (pas l’URL complète)
        content.setBannerImageUrl(fileName);
        contentRepository.save(content);

        // Retourne l'URL complète (endpoint REST) pour affichage côté frontend
        return "/api/equipe/images/banner/" + fileName;
    }

    /**
     * Supprime la bannière :
     * - supprime l'image physique si existante
     * - supprime l'URL dans la base
     */
    public void deleteBannerImage() throws IOException {
        EquipeContent content = getContent().orElseThrow(() -> new IllegalStateException("Contenu équipe introuvable"));

        if (content.getBannerImageUrl() != null) {
            deleteFileIfExists("/api/equipe/images/banner/" + content.getBannerImageUrl());
        }

        content.setBannerImageUrl(null);
        contentRepository.save(content);
    }

    // --- Upload photo membre ---

    /**
     * Upload physique photo membre + sauvegarde uniquement le nom du fichier dans base.
     * Retourne l'URL complète publique pour accéder à la photo.
     *
     * @param memberId ID du membre
     * @param file fichier image multipart/form-data
     * @return URL complète d'accès à la photo du membre
     * @throws IOException
     */
    public String uploadMemberPhoto(Long memberId, MultipartFile file) throws IOException {
        EquipeModel member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));

        Files.createDirectories(Paths.get(UPLOAD_DIR_MEMBERS));

        String fileName = System.currentTimeMillis() + "_" + Path.of(file.getOriginalFilename()).getFileName().toString();
        Path filePath = Paths.get(UPLOAD_DIR_MEMBERS, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Supprime ancienne photo physique si différente
        if (member.getPhotoUrl() != null && !member.getPhotoUrl().equals(fileName)) {
            deleteFileIfExists("/api/equipe/images/members/" + member.getPhotoUrl());
        }

        // Sauvegarde uniquement le nom du fichier en base (pas l’URL complète)
        member.setPhotoUrl(fileName);
        memberRepository.save(member);

        // Retourne l'URL complète (endpoint REST) pour affichage côté frontend
        return "/api/equipe/images/members/" + fileName;
    }

    /**
     * Supprime la photo d'un membre :
     * - supprime le fichier physique si existant
     * - supprime l'URL dans la base
     *
     * @param memberId ID du membre
     * @throws IOException
     */
    public void deleteMemberPhoto(Long memberId) throws IOException {
        EquipeModel member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));

        if (member.getPhotoUrl() != null) {
            deleteFileIfExists("/api/equipe/images/members/" + member.getPhotoUrl());
            member.setPhotoUrl(null);
            memberRepository.save(member);
        }
    }

    // --- Méthode utilitaire pour supprimer un fichier à partir d'une URL stockée ---

    /**
     * Supprime un fichier physique si il existe.
     * La méthode extrait le nom de fichier à partir de l'URL relative utilisée.
     *
     * Exemple d'URL : "/api/equipe/images/banner/12345_image.jpg"
     * On extrait "12345_image.jpg" et supprime dans le dossier correspondant.
     *
     * @param fileUrl URL relative stockée en base (doit contenir /banner/ ou /members/)
     */
    private void deleteFileIfExists(String fileUrl) {
        try {
            String filename = Paths.get(fileUrl).getFileName().toString();

            Path dir;
            if (fileUrl.contains("/banner/")) {
                dir = Paths.get(UPLOAD_DIR_BANNERS);
            } else if (fileUrl.contains("/members/")) {
                dir = Paths.get(UPLOAD_DIR_MEMBERS);
            } else {
                // URL ne correspond pas aux dossiers attendus, on ne supprime rien
                return;
            }

            Path filePath = dir.resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log l'erreur si tu as un logger, ou ignore silencieusement
            System.err.println("Erreur suppression fichier " + fileUrl + " : " + e.getMessage());
        }
    }

    // Adding a mapper utility for EquipeContent and EquipeModel
    public class EquipeMapper {
        public static EquipeModel toEquipeModel(EquipeContent content) {
            EquipeModel model = new EquipeModel();
            model.setId(content.getId());
            model.setFullName(content.getPresentationText());
            model.setPhotoUrl(content.getBannerImageUrl());
            return model;
        }

        public static EquipeContent toEquipeContent(EquipeModel model) {
            EquipeContent content = new EquipeContent();
            content.setId(model.getId());
            content.setPresentationText(model.getFullName());
            content.setBannerImageUrl(model.getPhotoUrl());
            return content;
        }
    }
}
