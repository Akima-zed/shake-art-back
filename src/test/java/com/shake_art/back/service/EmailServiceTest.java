package com.shake_art.back.service;

import com.shake_art.back.model.ReservationModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @Test
    void envoyerConfirmation_envoieEmailUtilisateurEtAdmin() {
        ReservationModel reservation = new ReservationModel();
        reservation.setNomComplet("Jean Dupont");
        reservation.setNom("Atelier Graffiti");
        reservation.setHeure("14:00");
        reservation.setEmail("jean@example.com");

        service.envoyerConfirmation(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(captor.capture());

        List<SimpleMailMessage> mails = captor.getAllValues();
        assertEquals("jean@example.com", mails.get(0).getTo()[0]);
        assertEquals("admin@shakeart.fr", mails.get(1).getTo()[0]);
        assertTrue(mails.get(0).getText().contains("Jean Dupont"));
    }
}
