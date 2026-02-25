package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shake_art.back.model.Photo;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByArtisteId(Long artisteId);
}