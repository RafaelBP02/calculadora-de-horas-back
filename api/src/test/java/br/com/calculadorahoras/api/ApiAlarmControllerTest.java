package br.com.calculadorahoras.api;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.AlertRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Time;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAlarmControllerTest {
    private String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoLWFwaSIsInN1YiI6IntcInVzZXJuYW1lXCI6XCJMZW9uY2lvXCIsXCJ1c2VySWRcIjo1fSIsImV4cCI6MTcyNjA3MTU3M30.arEPRV0ckn12CwKA5DELINDWnAhxtaupoj1e1HbmWJg";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertRepo alertRepo;

    @BeforeEach
    public void setup() {
        AlertConfig ac = new AlertConfig();
        ac.setId(1);
        ac.setWorkEntry(Time.valueOf("09:00:00"));
        ac.setIntervalBeginning(Time.valueOf("13:00:00"));
        ac.setIntervalEnd(Time.valueOf("14:00:00"));
        ac.setWorkEnd(Time.valueOf("18:00:00"));
        ac.setWorkload(8);
        ac.setUserId(5);

        Mockito.when(alertRepo.findByUserId(5)).thenReturn((ac));
        Mockito.when(alertRepo.findAll()).thenReturn(Arrays.asList(ac));
        Mockito.when(alertRepo.existsById(1)).thenReturn(Boolean.TRUE);
        Mockito.when(alertRepo.save(ac)).thenReturn(ac);
    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldFindConfigById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/alarms/{id}", 5)
                .header("Authorization", "Bearer " + this.validToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldNotFindConfigById() throws Exception {
        

        given(alertRepo.findByUserId(5)).willReturn(null);

        mockMvc.perform(get("/alarms/{id}", 5)
                .header("Authorization", "Bearer " + this.validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /*
     * DESABILITA TESTES DO ENDPOINT INATIVO
     * 
     * @Test
     * 
     * @WithMockUser(username = "user", roles = {"USER"})
     * public void shouldFindAllConfigurtions() throws Exception {
     * mockMvc.perform(MockMvcRequestBuilders.get("/alarms"))
     * .andExpect(MockMvcResultMatchers.status().isOk());
     * }
     * 
     * @Test
     * 
     * @WithMockUser(username = "user", roles = {"USER"})
     * public void shouldNotFindAllConfigurtions() throws Exception {
     * given(repo.findAll()).willReturn(new ArrayList<AlertConfig>());
     * 
     * mockMvc.perform(MockMvcRequestBuilders.get("/alarms"))
     * .andExpect(MockMvcResultMatchers.status().isInternalServerError())
     * .andExpect(content()
     * .json("{\"errorMessage\":\"Erro na comunicação com o servidor. Por favor tente mais tarde\"}"
     * ));
     * }
     */
    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    public void shouldEditAlarmConfig() throws Exception {
        AlertConfig editAC = new AlertConfig();
        editAC.setId(1);
        editAC.setWorkEntry(Time.valueOf("08:00:00"));
        editAC.setIntervalBeginning(Time.valueOf("13:00:00"));
        editAC.setIntervalEnd(Time.valueOf("14:00:00"));
        editAC.setWorkEnd(Time.valueOf("18:00:00"));
        editAC.setWorkload(6);
        editAC.setUserId(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String editACJson = objectMapper.writeValueAsString(editAC);

        mockMvc.perform(put("/alarms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editACJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    public void shouldNotEditAlarmConfig() throws Exception {
        AlertConfig editAC = new AlertConfig();
        editAC.setId(10);
        editAC.setWorkEntry(Time.valueOf("08:00:00"));

        ObjectMapper objectMapper = new ObjectMapper();
        String editACJson = objectMapper.writeValueAsString(editAC);

        mockMvc.perform(put("/alarms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editACJson))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .json("{\"errorMessage\":\"Essa configuração de alarme não existe\"}"));

    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    public void shouldRegisterAlarmConfig() throws Exception {
        AlertConfig newAC = new AlertConfig();
        newAC.setWorkEntry(Time.valueOf("10:00:00"));
        newAC.setIntervalBeginning(Time.valueOf("13:00:00"));
        newAC.setIntervalEnd(Time.valueOf("14:00:00"));
        newAC.setWorkEnd(Time.valueOf("18:00:00"));
        newAC.setWorkload(6);
        newAC.setUserId(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String newACJson = objectMapper.writeValueAsString(newAC);

        mockMvc.perform(post("/alarms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newACJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    public void shouldNotRegisterAlarmConfig() throws Exception {
        given(alertRepo.save(any(AlertConfig.class))).willThrow(new RuntimeException());
        AlertConfig newAC = new AlertConfig();

        ObjectMapper objectMapper = new ObjectMapper();
        String newACJson = objectMapper.writeValueAsString(newAC);

        mockMvc.perform(post("/alarms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newACJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                        "{\"errorMessage\":\"Não foi possível salvar sua configuração. Erro na comunicação com o servidor\"}"));

    }

}