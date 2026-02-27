package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shake_art.back.model.Partenaire;

public interface PartenaireRepository extends JpaRepository<Partenaire, Long> {
}