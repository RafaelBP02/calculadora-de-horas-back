package br.com.calculadorahoras.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.calculadorahoras.api.controller.AlertController;
import br.com.calculadorahoras.api.repo.Repo;


@SpringBootTest
@AutoConfigureMockMvc
public class ApiAlertControllerTest {

    private int userId = 2;
    private String message = "Test message";
    private AlertController alertController;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Repo repo;

    @BeforeEach
    public void setup() {
        alertController = new AlertController();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldCreateEmmiterById() throws Exception {
        SseEmitter testEmitter = alertController.getAlerts(userId);
        assertNotNull(testEmitter);
        assertTrue(alertController.hasEmitter(userId));
    }

    @Test
    public void shouldNothaveEmmiter() throws Exception {
        assertFalse(alertController.hasEmitter(userId));
    }

    @Test
    public void shouldSendAlert() throws Exception {
        alertController.getAlerts(userId);
        alertController.sendAlert(userId, message);
        String expectedLog = "Enviando alerta para o usuário ID " + userId + ": " + message + "\n";

        assertEquals(expectedLog.trim(), outContent.toString().trim());

    }

    @Test
    public void shouldNotSendAlert() throws Exception {
        alertController.sendAlert(userId, message);
        String expectedLog = "Nenhum SseEmitter encontrado para o usuário ID " + userId + "\n";
        assertEquals(expectedLog.trim(), outContent.toString().trim());
    }

    @Test
    public void shouldPostMessae() throws Exception {
        mockMvc.perform(post("/alerts/{userId}", userId)
                .content(message)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
}
