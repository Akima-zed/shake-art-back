package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shake_art.back.model.CardPresentation;

public interface CardPresentationRepository extends JpaRepository<CardPresentation, Long> {
}
