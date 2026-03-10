package com.shake_art.back.service;

import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.AccueilContent;
import com.shake_art.back.model.CardPresentation;
import com.shake_art.back.repository.AccueilContentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class AccueilContentService {

    @Autowired
    private AccueilContentRepository repository;



    public Optional<AccueilContent> getContent() {
        Optional<AccueilContent> optional = repository.findById(1L);

        if (optional.isEmpty()) {
            AccueilContent content = new AccueilContent();
            content.setId(1L);
            content.setHeroTitle("Shake Art");
            content.setHeroSubtitle("Festival International de Street Art à Saint-Brieuc");
            content.setHeroVideoUrl("assets/videos/fresques.mp4");
            content.setPresentationText("« festival shake art … »");

            List<CardPresentation> cards = List.of(
                    new CardPresentation("L’équipe", "Rencontrez l’équipe", "assets/images/team.jpg", "/equipe"),
                    new CardPresentation("Édition 2023", "Retour sur 2023", "assets/images/edition2023.jpg", "/edition/2023"),
                    new CardPresentation("Partenaires", "Nos partenaires", "assets/images/partenaires.jpg", "/partenaires")
            );

            for (CardPresentation card : cards) {
                card.setAccueilContent(content); // lie la carte à son parent
            }

            content.setCards(cards);
            repository.save(content);

            return Optional.of(content);
        }

        return optional;
    }

    public AccueilContent saveContent(AccueilContent content) {
        content.setId(1L); // Forcer l'ID unique
        if (content.getCards() != null) {
            for (CardPresentation card : content.getCards()) {
                card.setAccueilContent(content); // important pour lier les cartes
            }
        }
        return repository.save(content);
    }

    public String uploadImageForCard(Long cardId, MultipartFile file) throws IOException {
        AccueilContent content = repository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu d'accueil introuvable"));

        CardPresentation card = content.getCards()
                .stream()
                .filter(c -> c.getId() != null && c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Carte non trouvee"));

        String uploadDir = "uploads/cards";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = System.currentTimeMillis() + "_" + Path.of(file.getOriginalFilename()).getFileName().toString();
        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        card.setImage("/" + uploadDir + "/" + fileName);
        repository.save(content);

        return "/" + uploadDir + "/" + fileName;
    }
}
