package com.shake_art.back.repository;

import com.shake_art.back.model.EquipeModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class EquipeMemberRepositoryDataJpaTest {

    @Autowired
    private EquipeMemberRepository repository;

    @Test
    void searchByName_retourneLesMembresCorrespondants() {
        EquipeModel memberA = createMember("Camille Dubois", "Coordination");
        EquipeModel memberB = createMember("Nora Lemaire", "Communication");

        repository.saveAll(List.of(memberA, memberB));

        List<EquipeModel> result = repository.searchByName("dubo");

        assertEquals(1, result.size());
        assertEquals("Camille Dubois", result.get(0).getFullName());
    }

    private EquipeModel createMember(String fullName, String role) {
        EquipeModel member = new EquipeModel();
        member.setFullName(fullName);
        member.setRole(role);
        member.setEmail(fullName.toLowerCase().replace(" ", ".") + "@shake-art.test");
        member.setPhotoUrl("photo.jpg");
        member.setBio("bio");
        return member;
    }
}
