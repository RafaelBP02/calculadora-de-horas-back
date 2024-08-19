package br.com.calculadorahoras.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import br.com.calculadorahoras.api.controller.AlertController;
import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAlertControllerTest {

    private int userId = 2;
    private String message = "Test message";
    private AlertController alertController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Repo repo;

    @BeforeEach
    public void setup() {
       alertController = new AlertController();
    }

    @Test
    public void shouldCreateEmmiterById() throws Exception{
        SseEmitter testEmitter = alertController.getAlerts(userId);
        assertNotNull(testEmitter);
        assertTrue(alertController.hasEmitter(userId));
    }

    @Test
    public void shouldNothaveEmmiter() throws Exception{
        assertFalse(alertController.hasEmitter(userId));
    }

}
