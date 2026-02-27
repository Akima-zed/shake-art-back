package com.shake_art.back.service;

import com.shake_art.back.dto.IntervenantDto;
import com.shake_art.back.repository.ArtisteRepository;
import com.shake_art.back.repository.EquipeMemberRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntervenantService {

    private final ArtisteRepository artisteRepository;
    private final EquipeMemberRepository equipeRepository;

    public IntervenantService(ArtisteRepository artisteRepository, EquipeMemberRepository equipeRepository) {
        this.artisteRepository = artisteRepository;
        this.equipeRepository = equipeRepository;
    }

    public List<IntervenantDto> search(String term) {
        List<IntervenantDto> result = new ArrayList<>();

        artisteRepository.searchByName(term).forEach(a ->
                result.add(new IntervenantDto(a.getId(), a.getName(), "artiste")));

        equipeRepository.searchByName(term).forEach(e ->
                result.add(new IntervenantDto(e.getId(), e.getFullName(), "equipe")));

        return result;
    }

    public List<IntervenantDto> getAll() {
        List<IntervenantDto> result = new ArrayList<>();

        artisteRepository.findAll().forEach(a ->
                result.add(new IntervenantDto(a.getId(), a.getName(), "artiste")));

        equipeRepository.findAll().forEach(e ->
                result.add(new IntervenantDto(e.getId(), e.getFullName(), "equipe")));

        return result;
    }
}
