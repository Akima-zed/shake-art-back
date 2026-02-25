package com.shake_art.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.shake_art.back.model.ReservationModel;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerConfirmation(ReservationModel res) {
        String subject = "Confirmation de réservation Shake Art";
        String message = "Bonjour " + res.getNomComplet() + ",\n\n"
                + "Votre réservation pour l'activité '" + res.getNom() + "' à " + res.getHeure()
                + " a bien été prise en compte.\n\n"
                + "Merci et à bientôt !\nShake Art";

        // email utilisateur
        sendEmail(res.getEmail(), subject, message);

        // email admin
        sendEmail("admin@shakeart.fr", "Nouvelle réservation", message + "\n\nEmail: " + res.getEmail());
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        mailSender.send(mail);
    }
}
