package com.shake_art.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Programmation", description = "Gestion du programme du festival (jours, activites, intervenants)")
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

    @Operation(summary = "Liste toutes les programmations",
        description = "Retourne l'ensemble des jours de programmation avec leurs activites. Acces public.")
    @ApiResponse(responseCode = "200", description = "Liste des programmations retournee")
    @GetMapping
    public List<ProgrammationDto> getAll() {
        return service.getAllDto();
    }

    @Operation(summary = "Recuperer une programmation par ID",
        description = "Retourne le detail d'un jour de programmation avec ses activites. Necessite ROLE_USER ou ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Programmation trouvee"),
        @ApiResponse(responseCode = "401", description = "Token JWT manquant ou invalide"),
        @ApiResponse(responseCode = "404", description = "Programmation introuvable")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ProgrammationDto> getById(@PathVariable @NonNull Long id) {
        ProgrammationModel prog = service.getById(id);
        if (prog == null) {
            throw new ResourceNotFoundException("Programmation introuvable avec l'id " + id);
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

    @Operation(summary = "Programmations par annee",
        description = "Filtre les jours de programmation par annee (ex: 2025). Necessite ROLE_USER ou ROLE_ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/annee/{annee}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<ProgrammationDto> getByAnnee(@PathVariable @NonNull Integer annee) {
        return service.getByAnnee(annee).stream()
                .map(prog -> {
                    ProgrammationDto dto = new ProgrammationDto();
                    dto.setId(prog.getId());
                    dto.setDate(prog.getDate());
                    dto.setAnnee(prog.getAnnee());
                    dto.setActivites(prog.getActivites().stream()
                            .filter(a -> Objects.nonNull(a) && Objects.nonNull(a.getId()))
                            .map(this::modelToDtoActivite)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "Creer une programmation",
        description = "Cree un nouveau jour de programmation avec ses activites. Necessite ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Programmation creee"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "401", description = "Token JWT manquant ou invalide"),
        @ApiResponse(responseCode = "403", description = "Role ADMIN requis")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ProgrammationDto> create(@Valid @RequestBody ProgrammationDto dto) {
        ProgrammationModel model = dtoToModel(dto);
        model = Objects.requireNonNull(model, "Le modèle de programmation ne peut pas être nul");
        ProgrammationModel saved = service.save(model);
        // Convertir le modele sauvegarde en DTO pour le retour
        ProgrammationDto returnDto = new ProgrammationDto();
        returnDto.setId(saved.getId());
        returnDto.setDate(saved.getDate());
        returnDto.setAnnee(saved.getAnnee());
        returnDto.setActivites(saved.getActivites().stream()
                .filter(a -> Objects.nonNull(a) && Objects.nonNull(a.getId()))
                .map(this::modelToDtoActivite)
                .collect(Collectors.toList()));
        return ResponseEntity.status(201).body(returnDto);
    }

    @Operation(summary = "Mettre a jour une programmation",
        description = "Modifie un jour de programmation existant. Necessite ROLE_ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ProgrammationDto> update(@PathVariable @NonNull Long id,
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
        // Convertir le modele update en DTO pour le retour
        ProgrammationDto returnDto = new ProgrammationDto();
        returnDto.setId(updated.getId());
        returnDto.setDate(updated.getDate());
        returnDto.setAnnee(updated.getAnnee());
        returnDto.setActivites(updated.getActivites().stream()
                .filter(a -> Objects.nonNull(a) && Objects.nonNull(a.getId()))
                .map(this::modelToDtoActivite)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(returnDto);
    }

    @Operation(summary = "Supprimer une activite d'une programmation",
        description = "Retire une activite d'un jour de programmation. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Activite retiree de la programmation")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{programmationId}/activites/{activiteId}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(summary = "Supprimer une programmation par date",
        description = "Supprime tous les jours de programmation correspondant a une date. Necessite ROLE_ADMIN.")
    @ApiResponse(responseCode = "204", description = "Programmations supprimees")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/date/{date}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteByDate(@PathVariable String date) {
        service.deleteByDate(date);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Creer une activite independante",
        description = "Ajoute une activite non rattachee a un jour. Necessite ROLE_ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/activites")
    @PreAuthorize("hasRole('ADMIN')")
    public ActiviteModel createActivite(@Valid @RequestBody ActiviteDto dto) {
        Objects.requireNonNull(dto, "ActiviteDto cannot be null");
        ActiviteModel activite = Objects.requireNonNull(dtoToModelActivite(dto),
                "Converted ActiviteModel cannot be null");
        return activiteRepository.save(activite);
    }

    @Operation(summary = "Mettre a jour une activite",
        description = "Modifie le contenu d'une activite existante. Necessite ROLE_ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/activites/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
