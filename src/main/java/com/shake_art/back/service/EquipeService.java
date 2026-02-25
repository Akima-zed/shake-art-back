package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shake_art.back.model.EquipeModel;
import com.shake_art.back.repository.EquipeModelRepository;


@Service
public class EquipeService {

    @Autowired
    private EquipeModelRepository equipeRepository;

    public EquipeModel getById(Long id) {
        return equipeRepository.findById(id).orElse(null);
    }

    public EquipeModel save(EquipeModel model) {
        return equipeRepository.save(model);
    }
}