package com.shake_art.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shake_art.back.dto.IntervenantDto;
import com.shake_art.back.service.IntervenantService;

import java.util.List;

@RestController
@Tag(name = "Intervenants", description = "Recherche des intervenants du festival (artistes et equipes)")
public class IntervenantController {

    private final IntervenantService intervenantService;

    public IntervenantController(IntervenantService intervenantService) {
        this.intervenantService = intervenantService;
    }

    @Operation(summary = "Rechercher un intervenant",
        description = "Recherche par nom parmi artistes et equipes. Utile pour l'autocompletion lors de la creation d'activites.")
    @GetMapping("/intervenants/search")
    public List<IntervenantDto> searchIntervenants(
            @Parameter(description = "Terme de recherche (ex: nom de l'artiste)", required = true)
            @RequestParam("q") String query) {
        return intervenantService.search(query);
    }

    @Operation(summary = "Lister tous les intervenants",
        description = "Retourne l'ensemble des artistes et equipes enregistres. Acces public.")
    @GetMapping("/intervenants")
    public List<IntervenantDto> getAllIntervenants() {
        return intervenantService.getAll();
    }

}
