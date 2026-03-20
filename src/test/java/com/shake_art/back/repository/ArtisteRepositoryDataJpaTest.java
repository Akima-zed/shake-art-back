package com.shake_art.back.repository;

import com.shake_art.back.model.ArtisteModel;
import com.shake_art.back.model.ArtisteType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class ArtisteRepositoryDataJpaTest {

    @Autowired
    private ArtisteRepository repository;

    @Test
    void findByAnneeArchive_retourneUniquementAnneeDemandee() {
        ArtisteModel archive2024 = createArtiste("Mina Colors", 2024);
        ArtisteModel archive2025 = createArtiste("Leo Ink", 2025);

        repository.saveAll(List.of(archive2024, archive2025));

        List<ArtisteModel> result = repository.findByAnneeArchive(2025);

        assertEquals(1, result.size());
        assertEquals("Leo Ink", result.get(0).getName());
        assertEquals(2025, result.get(0).getAnneeArchive());
    }

    @Test
    void searchByName_estInsensitiveALaCasseEtPartiel() {
        ArtisteModel first = createArtiste("Alyx Martin", null);
        ArtisteModel second = createArtiste("Noah Stone", null);

        repository.saveAll(List.of(first, second));

        List<ArtisteModel> result = repository.searchByName("alyx");

        assertEquals(1, result.size());
        assertEquals("Alyx Martin", result.get(0).getName());
    }

    private ArtisteModel createArtiste(String name, Integer archiveYear) {
        ArtisteModel artiste = new ArtisteModel();
        artiste.setName(name);
        artiste.setDiscipline("Graffiti");
        artiste.setBio("bio");
        artiste.setType(ArtisteType.PAINTER);
        artiste.setAnneeArchive(archiveYear);
        return artiste;
    }
}
