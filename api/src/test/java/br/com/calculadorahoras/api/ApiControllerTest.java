package br.com.calculadorahoras.api;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Repo repo;

    @BeforeEach
    public void setup() {
        AlertConfig ac = new AlertConfig();
        ac.setId(1);
        ac.setWorkEntry(Time.valueOf("09:00:00"));
        ac.setIntervalBeginning(Time.valueOf("13:00:00"));
        ac.setIntervalEnd(Time.valueOf("14:00:00"));
        ac.setWorkEnd(Time.valueOf("18:00:00"));
        ac.setWorkload(8);
        ac.setUser_id(1);

        Mockito.when(repo.findById(1)).thenReturn(Optional.of(ac));
        Mockito.doReturn(Arrays.asList(ac)).when(repo).findAll();
        Mockito.when(repo.existsById(1)).thenReturn(Boolean.TRUE);
        Mockito.when(repo.save(ac)).thenReturn(ac);
    }

    @Test
    public void shouldFindConfigById() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void shouldNotFindConfigById() throws Exception {
        given(repo.findById(0)).willReturn(Optional.empty());

        mockMvc.perform(get("/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllConfigurtions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldNotFindAllConfigurtions() throws Exception {
        given(repo.findAll()).willReturn(new ArrayList<AlertConfig>());

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(content()
                        .json("{\"errorMessage\":\"Erro na comunicação com o servidor. Por favor tente mais tarde\"}"));
    }

    @Test
    public void shouldEditAlarmConfig() throws Exception {
        AlertConfig editAC = new AlertConfig();
        editAC.setId(1);
        editAC.setWorkEntry(Time.valueOf("08:00:00"));
        editAC.setIntervalBeginning(Time.valueOf("13:00:00"));
        editAC.setIntervalEnd(Time.valueOf("14:00:00"));
        editAC.setWorkEnd(Time.valueOf("18:00:00"));
        editAC.setWorkload(6);
        editAC.setUser_id(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String editACJson = objectMapper.writeValueAsString(editAC);

        mockMvc.perform(put("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editACJson))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotEditAlarmConfig() throws Exception {
        AlertConfig editAC = new AlertConfig();
        editAC.setId(10);
        editAC.setWorkEntry(Time.valueOf("08:00:00"));

        ObjectMapper objectMapper = new ObjectMapper();
        String editACJson = objectMapper.writeValueAsString(editAC);

        mockMvc.perform(put("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editACJson))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .json("{\"errorMessage\":\"Essa configuração de alarme não existe\"}"));
    
    }

    @Test
    public void shouldRegisterAlarmConfig() throws Exception {
        AlertConfig newAC = new AlertConfig();
        newAC.setWorkEntry(Time.valueOf("10:00:00"));
        newAC.setIntervalBeginning(Time.valueOf("13:00:00"));
        newAC.setIntervalEnd(Time.valueOf("14:00:00"));
        newAC.setWorkEnd(Time.valueOf("18:00:00"));
        newAC.setWorkload(6);
        newAC.setUser_id(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String newACJson = objectMapper.writeValueAsString(newAC);

        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newACJson))
                .andExpect(status().isOk());                
    }

    @Test
    public void shouldNotRegisterAlarmConfig() throws Exception {
        given(repo.save(any(AlertConfig.class))).willThrow(new RuntimeException());
        AlertConfig newAC = new AlertConfig();

        ObjectMapper objectMapper = new ObjectMapper();
        String newACJson = objectMapper.writeValueAsString(newAC);

        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newACJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                        "{\"errorMessage\":\"Não foi possível salvar sua configuração. Erro na comunicação com o servidor\"}"));

    }

}