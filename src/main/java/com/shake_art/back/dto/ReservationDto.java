package com.shake_art.back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {
    private String activite;
    private String jour;
    private String heure;
    private int places;
    private String email;
}
