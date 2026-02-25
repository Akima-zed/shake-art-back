package com.shake_art.back.controller;

import static com.shake_art.back.mapper.EquipeMapper.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.dto.EquipeDto;
import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.service.EquipeService;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    private final EquipeService service;

    public EquipeController(EquipeService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeDto> getById(@PathVariable Long id) {
        EquipeModel equipe = service.getById(id);
        if (equipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(equipe));
    }

    @PostMapping
    public ResponseEntity<EquipeModel> create(@RequestBody EquipeDto dto) {
        EquipeModel model = toModel(dto);
        EquipeModel saved = service.save(model);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipeModel> update(@PathVariable Long id, @RequestBody EquipeDto dto) {
        EquipeModel existing = service.getById(id);
        existing.setNom(dto.getNom());
        existing.setRole(dto.getRole());
        EquipeModel updated = service.save(existing);
        return ResponseEntity.ok(updated);
    }
}