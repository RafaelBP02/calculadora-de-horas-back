package br.com.calculadorahoras.api;

import br.com.calculadorahoras.api.controller.Controller;
import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Time;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;



@SpringBootTest
@AutoConfigureMockMvc
public class ApiAlarmControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private Repo repo;

    @BeforeEach
    public void setup(){
        AlertConfig ac = new AlertConfig();
        ac.setId(1);
        ac.setWorkEntry(Time.valueOf("09:00:00"));
        ac.setIntervalBeginning(Time.valueOf("13:00:00"));
        ac.setIntervalEnd(Time.valueOf("14:00:00"));
        ac.setWorkEnd(Time.valueOf("18:00:00"));
        ac.setUser_id(1);

        AlertConfig ac2 = new AlertConfig();
        ac2.setId(2);
        ac2.setWorkEntry(Time.valueOf("09:00:00"));
        ac2.setIntervalBeginning(Time.valueOf("13:00:00"));
        ac2.setIntervalEnd(Time.valueOf("14:00:00"));
        ac2.setWorkEnd(Time.valueOf("18:00:00"));
        ac2.setUser_id(1);

        Mockito.when(repo.findById(1)).thenReturn(Optional.of(ac));
        Mockito.when(repo.findById(2)).thenReturn(Optional.of(ac2));
    }

    @Test
    public void shouldFindConfigById() throws Exception{
        
        mockMvc.perform(MockMvcRequestBuilders.get("/{id}", 2))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void shouldNotFindConfigById() throws Exception{
        given(repo.findById(0)).willReturn(Optional.empty());

        mockMvc.perform(get("/{id}", 0)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
        }

}