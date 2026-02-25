package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shake_art.back.model.EquipeModel;

import java.util.List;

public interface EquipeMemberRepository extends JpaRepository<EquipeModel, Long> {

    @Query("SELECT e FROM EquipeModel e WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<EquipeModel> searchByName(@Param("term") String term);

}