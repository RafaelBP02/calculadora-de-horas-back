package br.com.calculadorahoras.service;

import java.sql.Time;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceAlertTest {

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
        ac.setWorkload(8);
        ac.setUser_id(1);

        AlertConfig ac2 = new AlertConfig();
        ac2.setId(2);
        ac2.setWorkEntry(Time.valueOf("09:00:00"));
        ac2.setIntervalBeginning(Time.valueOf("13:00:00"));
        ac2.setIntervalEnd(Time.valueOf("14:00:00"));
        ac2.setWorkEnd(Time.valueOf("18:00:00"));
        ac2.setWorkload(6);
        ac2.setUser_id(2);

        Mockito.when(repo.findAll()).thenReturn( Arrays.asList(ac, ac2));
    }

}
