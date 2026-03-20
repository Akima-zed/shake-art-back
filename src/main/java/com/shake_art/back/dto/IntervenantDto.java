package com.shake_art.back.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Intervenant", description = "Intervenant du festival (artiste ou membre d'equipe)")
public class IntervenantDto {

    private Long id;
    private String fullName;
    private String type; // "artiste" ou "equipe"

    public IntervenantDto(Long id, String fullName, String type) {
        this.id = id;
        this.fullName = fullName;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
