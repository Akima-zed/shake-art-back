package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shake_art.back.model.AdminModel;
import com.shake_art.back.repository.AdminRepository;


@Service
public class AdminService {

    public static final String RESERVATION_EMAIL_KEY = "reservation_email";

    @Autowired
    private AdminRepository repo;

    public String getReservationEmail() {
        return repo.findById(RESERVATION_EMAIL_KEY)
                .map(AdminModel::getValeur)
                .orElse("admin@shakeart.fr"); // Valeur par défaut
    }

    public void setReservationEmail(String email) {
        AdminModel setting = new AdminModel();
        setting.setCle(RESERVATION_EMAIL_KEY);
        setting.setValeur(email);
        repo.save(setting);
    }
}