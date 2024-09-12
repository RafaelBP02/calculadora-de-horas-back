package br.com.calculadorahoras.api;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.AlertRepo;
import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;
import br.com.calculadorahoras.api.services.TokenService;
import br.com.calculadorahoras.utils.UserTokenSubjectBody;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertRepo alertRepo;

    @MockBean
    private RoleRepo roleRepo;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

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
        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJsonToken = objectMapper.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        mockMvc.perform(MockMvcRequestBuilders.get("/alarms/{id}", 5)
                .header("Authorization", "Bearer valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldNotFindConfigById() throws Exception {
        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJsonToken = objectMapper.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        given(alertRepo.findByUserId(5)).willReturn(null);

        mockMvc.perform(get("/alarms/{id}", 5)
                .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldNotHaveAuthorizationToFindByUserId() throws Exception {
        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJsonToken = objectMapper.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        mockMvc.perform(MockMvcRequestBuilders.get("/alarms/{id}", 1)
                .header("Authorization", "Bearer valid_token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
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
        editAC.setUserId(5);

        ObjectMapper objectMapper = new ObjectMapper();
        String editACJson = objectMapper.writeValueAsString(editAC);

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        mockMvc.perform(put("/alarms")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editACJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldNotEditAlarmConfig() throws Exception {
        AlertConfig editAC = new AlertConfig();
        editAC.setId(10);
        editAC.setWorkEntry(Time.valueOf("08:00:00"));
        editAC.setUserId(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String editACJson = objectMapper.writeValueAsString(editAC);

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        mockMvc.perform(put("/alarms")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editACJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content()
                        .json("{\"errorMessage\":\"Este usuario nao possui a devida autorizacao\"}"));

    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldRegisterAlarmConfig() throws Exception {
        AlertConfig newAC = new AlertConfig();
        newAC.setWorkEntry(Time.valueOf("10:00:00"));
        newAC.setIntervalBeginning(Time.valueOf("13:00:00"));
        newAC.setIntervalEnd(Time.valueOf("14:00:00"));
        newAC.setWorkEnd(Time.valueOf("18:00:00"));
        newAC.setWorkload(6);
        newAC.setUserId(5);

        ObjectMapper objectMapper = new ObjectMapper();
        String newACJson = objectMapper.writeValueAsString(newAC);

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        mockMvc.perform(post("/alarms")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newACJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Leoncio", roles = { "USER" })
    public void shouldNotRegisterAlarmConfig() throws Exception {
        given(alertRepo.save(any(AlertConfig.class))).willThrow(new RuntimeException());
        AlertConfig newAC = new AlertConfig();
        newAC.setUserId(5);

        ObjectMapper objectMapper = new ObjectMapper();
        String newACJson = objectMapper.writeValueAsString(newAC);

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Leoncio", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);

        mockMvc.perform(post("/alarms")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newACJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(
                        "{\"errorMessage\":\"Não foi possível salvar sua configuração. Erro na comunicação com o servidor\"}"));

    }

}