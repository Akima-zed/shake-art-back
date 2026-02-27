package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.repository.EquipeModelRepository;
import java.util.Objects;


@Service
public class EquipeService {

    @Autowired
    private EquipeModelRepository equipeRepository;

    public EquipeModel getById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return equipeRepository.findById(id).orElse(null);
    }

    public EquipeModel save(EquipeModel model) {
        Objects.requireNonNull(model, "EquipeModel cannot be null");
        return equipeRepository.save(model);
    }
}