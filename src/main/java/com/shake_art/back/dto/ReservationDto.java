package com.shake_art.back.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Reservation", description = "Donnees de reservation pour une activite du festival")
public class ReservationDto {
    @Schema(description = "Nom de l'activite reservee", example = "Atelier peinture murale")
    private String activite;
    
    @Schema(description = "Jour de la participation (format JJ/MM/YYYY)", example = "15/06/2025")
    private String jour;
    
    @Schema(description = "Heure de l'activite (format HH:MM)", example = "14:30")
    private String heure;
    
    @Schema(description = "Nombre de places reservees", example = "2")
    private int places;
    
    @Schema(description = "Adresse email du visiteur", example = "visitor@example.com")
    private String email;
}
