package com.shake_art.back.service;

import com.shake_art.back.dto.ActiviteDto;
import com.shake_art.back.dto.ProgrammationDto;
import com.shake_art.back.model.ActiviteModel;
import com.shake_art.back.model.ProgrammationModel;
import com.shake_art.back.repository.EquipeMemberRepository;
import com.shake_art.back.repository.ProgrammationRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgrammationService {

    private final ProgrammationRepository repository;

    public ProgrammationService(ProgrammationRepository repository) {
        this.repository = repository;
    }

    // Retourne toutes les programmations converties en DTO
    public List<ProgrammationDto> getAllDto() {
        return repository.findAll().stream()
                .map(prog -> {
                    ProgrammationDto dto = new ProgrammationDto();
                    dto.setId(prog.getId());
                    dto.setDate(prog.getDate());
                    dto.setAnnee(prog.getAnnee());
                    dto.setActivites(prog.getActivites().stream().map(act -> {
                        ActiviteDto actDto = new ActiviteDto();
                        actDto.setId(act.getId());
                        actDto.setType(act.getType());
                        actDto.setName(act.getName());
                        actDto.setHeure(act.getHeure());
                        actDto.setReservable(act.getReservable());

                        if (act.getArtiste() != null) {
                            actDto.setIntervenantType("artiste");
                            actDto.setIntervenantId(act.getArtiste().getId());
                        } else if (act.getEquipe() != null) {
                            actDto.setIntervenantType("equipe");
                            actDto.setIntervenantId(act.getEquipe().getId());
                        }

                        return actDto;
                    }).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ProgrammationModel> getAll() {
        return repository.findAll();
    }

    public ProgrammationModel getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<ProgrammationModel> getByAnnee(Integer annee) {
        return repository.findByAnnee(annee);
    }

    public ProgrammationModel save(ProgrammationModel programmation) {
        validateProgrammation(programmation);
        return repository.save(programmation);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("La programmation avec l'ID " + id + " n'existe pas.");
        }
        repository.deleteById(id);
    }

    private void validateProgrammation(ProgrammationModel programmation) {
        if (programmation.getDate() == null || programmation.getDate().isEmpty()) {
            throw new IllegalArgumentException("La date de la programmation est obligatoire.");
        }
        if (programmation.getAnnee() == null || programmation.getAnnee() <= 0) {
            throw new IllegalArgumentException("L'année de la programmation est invalide.");
        }
        if (programmation.getActivites() == null || programmation.getActivites().isEmpty()) {
            throw new IllegalArgumentException("La programmation doit contenir au moins une activité.");
        }
    }

    /**
     * Supprime les programmations à une date donnée.
     * Orphan removal ou suppression manuelle des activités.
     */
    @Transactional
    public void deleteByDate(String date) {
        List<ProgrammationModel> programmations = repository.getAllByDate(date);
        for (ProgrammationModel prog : programmations) {
            for (ActiviteModel act : prog.getActivites()) {
                act.setProgrammation(null);
            }
            prog.getActivites().clear();
            repository.delete(prog);
        }
    }
}
