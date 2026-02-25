package com.shake_art.back.repository;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shake_art.back.model.ProgrammationModel;

import java.util.List;

public interface ProgrammationRepository extends JpaRepository<ProgrammationModel, Long> {

    // ✅ Charge aussi les artistes et équipes des activités
    @EntityGraph(attributePaths = {
            "activites",
            "activites.artiste",
            "activites.equipe"
    })
    List<ProgrammationModel> findAll();

    @EntityGraph(attributePaths = {
            "activites",
            "activites.artiste",
            "activites.equipe"
    })
    List<ProgrammationModel> findByAnnee(Integer annee);

    @EntityGraph(attributePaths = {
            "activites",
            "activites.artiste",
            "activites.equipe"
    })
    ProgrammationModel findById(long id);

    @EntityGraph(attributePaths = {
            "activites",
            "activites.artiste",
            "activites.equipe"
    })
    @Query("SELECT DISTINCT p FROM ProgrammationModel p LEFT JOIN FETCH p.activites WHERE p.date = :date")
    List<ProgrammationModel> getAllByDate(@Param("date") String date);

    @EntityGraph(attributePaths = {
            "activites",
            "activites.artiste",
            "activites.equipe"
    })
    List<ProgrammationModel> findAllByDate(String date);
}
