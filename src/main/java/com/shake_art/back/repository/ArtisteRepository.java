package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shake_art.back.model.ArtisteModel;

import java.util.List;

public interface ArtisteRepository extends JpaRepository<ArtisteModel, Long> {
    List<ArtisteModel> findByType(String type);

    // ✅ Méthode correctement déclarée (pas static, pas implémentée ici)
    List<ArtisteModel> findByAnneeArchive(Integer annee);

    @Query("SELECT a FROM ArtisteModel a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<ArtisteModel> searchByName(@Param("term") String term);
}

