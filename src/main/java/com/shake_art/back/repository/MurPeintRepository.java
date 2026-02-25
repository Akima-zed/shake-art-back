package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shake_art.back.model.MurPeint;

import java.util.List;
import java.util.Optional;

@Repository
public interface MurPeintRepository extends JpaRepository<MurPeint, Long> {
    List<MurPeint> findByAnnee(Integer annee);

    @Query("SELECT m FROM MurPeint m LEFT JOIN FETCH m.artiste WHERE m.id = :id")
    Optional<MurPeint> findByIdWithArtiste(@Param("id") Long id);
}
