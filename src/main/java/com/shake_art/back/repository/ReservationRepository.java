package com.shake_art.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shake_art.back.model.ReservationModel;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationModel, Long> {
}