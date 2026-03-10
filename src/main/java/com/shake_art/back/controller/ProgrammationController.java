package com.shake_art.back.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.shake_art.back.dto.ActiviteDto;
import com.shake_art.back.dto.ProgrammationDto;
import com.shake_art.back.exception.ResourceNotFoundException;
import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ProgrammationModel;
import com.shake_art.back.repository.ActiviteRepository;
import com.shake_art.back.repository.ProgrammationRepository;
import com.shake_art.back.service.ProgrammationService;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour gérer la programmation avec conversion DTO <-> Model.
 */
@RestController
@RequestMapping("/programmation")
@CrossOrigin("*")
public class ProgrammationController {

    private final ProgrammationService service;
    private final ProgrammationRepository programmationRepository;
    private final ActiviteRepository activiteRepository;

    public ProgrammationController(ProgrammationService service,
            ProgrammationRepository programmationRepository,
            ActiviteRepository activiteRepository) {
        this.service = service;
        this.programmationRepository = programmationRepository;
        this.activiteRepository = activiteRepository;
    }

    /** Retourne toutes les programmations (entités complètes). */
    @GetMapping
    public List<ProgrammationDto> getAll() {
        return service.getAllDto();
    }

    /** Retourne une programmation par son ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ProgrammationDto> getById(@PathVariable @NonNull Long id) {
        ProgrammationModel prog = service.getById(id);
        if (prog == null) {
            return ResponseEntity.notFound().build();
        }
        ProgrammationDto dto = new ProgrammationDto();
        dto.setId(prog.getId());
        dto.setDate(prog.getDate());
        dto.setAnnee(prog.getAnnee());
        dto.setActivites(prog.getActivites().stream()
                .filter(a -> Objects.nonNull(a) && Objects.nonNull(a.getId()))
                .map(this::modelToDtoActivite)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(dto);
    }

    /** Retourne les programmations d'une année donnée. */
    @GetMapping("/annee/{annee}")
    public List<ProgrammationModel> getByAnnee(@PathVariable @NonNull Integer annee) {
        return service.getByAnnee(annee);
    }

    /** Création d'une programmation depuis un DTO JSON. */
    @PostMapping(consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<ProgrammationModel> create(@Valid @RequestBody ProgrammationDto dto) {
        ProgrammationModel model = dtoToModel(dto);
        model = Objects.requireNonNull(model, "Le modèle de programmation ne peut pas être nul");
        ProgrammationModel saved = service.save(model);
        return ResponseEntity.ok(saved);
    }

    /** Mise à jour d'une programmation existante depuis un DTO JSON. */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<ProgrammationModel> update(@PathVariable @NonNull Long id,
            @Valid @RequestBody ProgrammationDto dto) {
        Objects.requireNonNull(id, "ID cannot be null");
        ProgrammationModel existing = service.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Programmation introuvable avec l'id " + id);
        }
        existing.setDate(dto.getDate());
        existing.setAnnee(dto.getAnnee());
        existing.setActivites(
                dto.getActivites()
                        .stream()
                        .map(this::dtoToModelActivite)
                        .collect(Collectors.toList()));
        ProgrammationModel updated = service.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{programmationId}/activites/{activiteId}")
    @Transactional
    public ResponseEntity<Void> deleteActivite(@PathVariable @NonNull Long programmationId,
            @PathVariable @NonNull Long activiteId) {
        ProgrammationModel prog = programmationRepository.findById(programmationId)
            .orElseThrow(() -> new ResourceNotFoundException("Programmation introuvable avec l'id " + programmationId));

        boolean removed = prog.getActivites().removeIf(a -> a.getId() != null && a.getId().equals(activiteId));

        if (removed) {
            programmationRepository.save(prog);
            activiteRepository.deleteById(activiteId); // facultatif si suppression en cascade pas activée
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/date/{date}")
    @Transactional
    public ResponseEntity<?> deleteByDate(@PathVariable String date) {
        service.deleteByDate(date);
        return ResponseEntity.ok().build();
    }

    /**
     * Création d'une activité indépendante (rarement utilisé, préférer via
     * programmation).
     */
    @PostMapping("/activites")
    public ActiviteModel createActivite(@Valid @RequestBody ActiviteDto dto) {
        Objects.requireNonNull(dto, "ActiviteDto cannot be null");
        ActiviteModel activite = Objects.requireNonNull(dtoToModelActivite(dto),
                "Converted ActiviteModel cannot be null");
        return activiteRepository.save(activite);
    }

    /** Mise à jour d'une activité indépendante. */
    @PutMapping("/activites/{id}")
    public ActiviteModel updateActivite(@PathVariable @NonNull Long id, @Valid @RequestBody ActiviteDto dto) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(dto, "ActiviteDto cannot be null");
        ActiviteModel activite = activiteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Activite non trouvee avec l'id : " + id));

        activite.setName(Objects.requireNonNull(dto.getName(), "Name cannot be null"));
        activite.setType(Objects.requireNonNull(dto.getType(), "Type cannot be null"));
        activite.setHeure(dto.getHeure());
        activite.setReservable(dto.getReservable());
        activite.setIntervenantId(dto.getIntervenantId());
        activite.setIntervenantType(dto.getIntervenantType());

        return activiteRepository.save(activite);
    }

    // -------------- Méthodes privées de conversion DTO <-> Model --------------

    /**
     * Convertit un ProgrammationDto en ProgrammationModel avec conversion des
     * activités.
     */
    private ProgrammationModel dtoToModel(ProgrammationDto dto) {
        ProgrammationModel model = new ProgrammationModel();
        model.setId(dto.getId());
        model.setDate(dto.getDate());
        model.setAnnee(dto.getAnnee());
        model.setActivites(
                dto.getActivites()
                        .stream()
                        .map(this::dtoToModelActivite)
                        .collect(Collectors.toList()));
        return model;
    }

    private ActiviteDto modelToDtoActivite(ActiviteModel model) {
        ActiviteDto dto = new ActiviteDto();
        dto.setId(model.getId());
        dto.setType(model.getType());
        dto.setName(model.getName());
        dto.setHeure(model.getHeure());
        dto.setReservable(model.getReservable());
        if (model.getArtiste() != null) {
            dto.setArtisteId(model.getArtiste().getId());
            dto.setIntervenantId(model.getArtiste().getId());
            dto.setIntervenantType("artiste");
        } else if (model.getEquipe() != null) {
            dto.setEquipeId(model.getEquipe().getId());
            dto.setIntervenantId(model.getEquipe().getId());
            dto.setIntervenantType("equipe");
        }
        return dto;
    }

    /**
     * Convertit un ActiviteDto en ActiviteModel avec liaison aux intervenants
     * (artiste/équipe).
     */
    private ActiviteModel dtoToModelActivite(ActiviteDto dto) {
        ActiviteModel act = new ActiviteModel();
        act.setId(dto.getId());
        act.setType(dto.getType());
        act.setName(dto.getName());
        act.setHeure(dto.getHeure());
        act.setReservable(dto.getReservable());
        act.setIntervenantId(dto.getIntervenantId());
        act.setIntervenantType(dto.getIntervenantType());
        return act;
    }

}
