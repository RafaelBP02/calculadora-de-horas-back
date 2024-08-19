package br.com.calculadorahoras.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.calculadorahoras.api.repo.Repo;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Repo repo;

}
