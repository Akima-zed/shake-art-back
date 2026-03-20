package com.shake_art.back.repository;

import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ProgrammationModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
class ProgrammationRepositoryDataJpaTest {

    @Autowired
    private ProgrammationRepository repository;

    @Test
    void findByAnnee_retourneUniquementAnneeDemandee() {
        ProgrammationModel p2025 = createProgrammation("2025-06-10", 2025, "Atelier 2025");
        ProgrammationModel p2026 = createProgrammation("2026-06-10", 2026, "Atelier 2026");

        repository.saveAll(List.of(p2025, p2026));

        List<ProgrammationModel> result = repository.findByAnnee(2026);

        assertEquals(1, result.size());
        assertEquals(2026, result.get(0).getAnnee());
        assertEquals("Atelier 2026", result.get(0).getActivites().get(0).getName());
    }

    @Test
    void getAllByDate_retourneProgrammationsEtActivites() {
        ProgrammationModel p1 = createProgrammation("2026-07-01", 2026, "Concert");
        repository.save(p1);

        List<ProgrammationModel> result = repository.getAllByDate("2026-07-01");

        assertEquals(1, result.size());
        assertEquals("2026-07-01", result.get(0).getDate());
        assertFalse(result.get(0).getActivites().isEmpty());
    }

    private ProgrammationModel createProgrammation(String date, Integer annee, String activiteName) {
        ProgrammationModel p = new ProgrammationModel();
        p.setDate(date);
        p.setAnnee(annee);

        ActiviteModel activite = new ActiviteModel();
        activite.setType("atelier");
        activite.setName(activiteName);
        activite.setHeure("10:00");
        activite.setReservable(true);
        activite.setProgrammation(p);

        p.setActivites(new java.util.ArrayList<>(List.of(activite)));
        return p;
    }
}
