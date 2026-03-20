package com.shake_art.back.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(name = "Programmation", description = "Programmation d'un jour avec ses activites")
public class ProgrammationDto {
    private Long id;

    @NotBlank(message = "La date est obligatoire")
    private String date;

    @NotNull(message = "L'annee est obligatoire")
    @Min(value = 2000, message = "L'annee doit etre superieure ou egale a 2000")
    private Integer annee;

    @NotEmpty(message = "Au moins une activite est requise")
    @Valid
    private List<ActiviteDto> activites;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public void setActivites(List<ActiviteDto> activites) {
        this.activites = activites;
    }

    public String getDate() {
        return date;
    }

    public Integer getAnnee() {
        return annee;
    }

    public List<ActiviteDto> getActivites() {
        return activites;
    }
}
