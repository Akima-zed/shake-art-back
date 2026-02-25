package com.shake_art.back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shake_art.back.dto.IntervenantDto;
import com.shake_art.back.service.IntervenantService;

import java.util.List;

@RestController
public class IntervenantController {

    private final IntervenantService intervenantService;

    public IntervenantController(IntervenantService intervenantService) {
        this.intervenantService = intervenantService;
    }

    @GetMapping("/intervenants/search")
    public List<IntervenantDto> searchIntervenants(@RequestParam("q") String query) {
        return intervenantService.search(query);
    }

    @GetMapping("/intervenants")
    public List<IntervenantDto> getAllIntervenants() {
        return intervenantService.getAll();
    }

}
