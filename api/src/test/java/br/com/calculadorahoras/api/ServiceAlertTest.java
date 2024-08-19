package br.com.calculadorahoras.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import br.com.calculadorahoras.api.controller.AlertController;
import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;
import br.com.calculadorahoras.api.service.AlertService;
import jakarta.inject.Inject;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceAlertTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @MockBean
    private Repo repo;

    @MockBean
    private AlertController alertController;

    @Autowired
    private AlertService alertService;

    @Inject
    LocalTime localTime;

    @BeforeEach
    public void setup(){
        System.setOut(new PrintStream(outContent));

        AlertConfig ac = new AlertConfig();
        ac.setId(1);
        ac.setWorkEntry(Time.valueOf("09:00:00"));
        ac.setIntervalBeginning(Time.valueOf("13:00:00"));
        ac.setIntervalEnd(Time.valueOf("14:00:00"));
        ac.setWorkEnd(Time.valueOf("18:00:00"));
        ac.setWorkload(8);
        ac.setUser_id(1);

        AlertConfig ac2 = new AlertConfig();
        ac2.setId(2);
        ac2.setWorkEntry(Time.valueOf("10:00:00"));
        ac2.setIntervalBeginning(Time.valueOf("11:00:00"));
        ac2.setIntervalEnd(Time.valueOf("12:00:00"));
        ac2.setWorkEnd(Time.valueOf("16:00:00"));
        ac2.setWorkload(6);
        ac2.setUser_id(2);

        Mockito.when(repo.findAll()).thenReturn( Arrays.asList(ac, ac2));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldCallNotificarUsuario(){
        alertService.verificarHorariosDePonto();

        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Verifica se deve alertar o usuário ID 1"));
        assertTrue(consoleOutput.contains("Verifica se deve alertar o usuário ID 2"));
    }

    @Test
    public void shouldSendAlertWithEmitter() {
        when(alertController.hasEmitter(anyInt())).thenReturn(true);
        alertService.enviarAlerta(2, "Teste");
        verify(alertController, times(1)).sendAlert(2, "Teste");
    }

    @Test
    public void shouldNotSendAlert() {
        when(alertController.hasEmitter(anyInt())).thenReturn(false);
        alertService.enviarAlerta(1, "Teste");

        String consoleOutput = outContent.toString();
        assertEquals("Nenhum SseEmitter encontrado para o usuário ID 1".trim(), consoleOutput.trim());
    }

    // @Test
    // public void shouldSendWorkEntryAlert(){
    //     when(alertController.hasEmitter(anyInt())).thenReturn(true);

    //     when(localTime.now()).thenReturn("09:00:00");
    //     //COMO MOCKAR CHAMADAS STATICAS

    //     alertService.verificarHorariosDePonto();

    //     String consoleOutput = outContent.toString();
    //     assertEquals("Enviando alerta para o usuário ID 2: Hora de bater o ponto de entrada!".trim(), consoleOutput.trim());

    // }

    // @Test
    // public void shouldSendIntervalBegginingAlert(){}

    // @Test
    // public void shouldSendIntevalEnd(){}

    // @Test
    // public void shouldSendWorkEndAlert(){}

}
