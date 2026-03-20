package com.shake_art.back.service;

import com.shake_art.back.model.AdminModel;
import com.shake_art.back.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository repo;

    @InjectMocks
    private AdminService service;

    @Test
    void getReservationEmail_retourneValeurDefaut_siAbsente() {
        when(repo.findById(AdminService.RESERVATION_EMAIL_KEY)).thenReturn(Optional.empty());

        String result = service.getReservationEmail();

        assertEquals("admin@shakeart.fr", result);
    }

    @Test
    void getReservationEmail_retourneValeurStockee_siPresente() {
        AdminModel setting = new AdminModel();
        setting.setCle(AdminService.RESERVATION_EMAIL_KEY);
        setting.setValeur("ops@shake-art.fr");
        when(repo.findById(AdminService.RESERVATION_EMAIL_KEY)).thenReturn(Optional.of(setting));

        String result = service.getReservationEmail();

        assertEquals("ops@shake-art.fr", result);
    }

    @Test
    void setReservationEmail_sauvegardeCleEtValeur() {
        service.setReservationEmail("admin@shake-art.fr");

        ArgumentCaptor<AdminModel> captor = ArgumentCaptor.forClass(AdminModel.class);
        verify(repo).save(captor.capture());

        assertEquals(AdminService.RESERVATION_EMAIL_KEY, captor.getValue().getCle());
        assertEquals("admin@shake-art.fr", captor.getValue().getValeur());
    }
}
