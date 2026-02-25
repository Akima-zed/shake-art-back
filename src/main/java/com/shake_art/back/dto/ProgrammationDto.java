package com.shake_art.back.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProgrammationDto {
    private Long id;
    private String date;
    private int annee;
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
