package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shake_art.back.model.EquipeContent;

import java.util.List;
import java.util.Optional;

public interface EquipeContentRepository extends JpaRepository<EquipeContent, Long> {
    Optional<EquipeContent> findFirstByOrderByIdAsc();

    @Query("SELECT e FROM EquipeContent e WHERE LOWER(e.presentationText) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<EquipeContent> searchByName(@Param("term") String term);
}
