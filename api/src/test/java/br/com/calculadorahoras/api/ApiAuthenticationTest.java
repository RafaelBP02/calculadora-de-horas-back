package br.com.calculadorahoras.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.calculadorahoras.api.model.Roles;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;
import br.com.calculadorahoras.api.services.TokenService;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAuthenticationTest {
    Roles role = new Roles();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleRepo roleRepo;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        role.setId(2);
        role.setRoleName("USUARIO");
        role.setDetails("teste para os perfis");

        Users user = new Users();
        user.setId(1);
        user.setUsername("TesterUnit");
        user.setRole(role);
        user.setPassword(passwordEncoder.encode("testPass123"));

        Mockito.when(roleRepo.findById(2)).thenReturn(Optional.of(role));
        Mockito.when(userRepo.findByUsername("TesterUnit")).thenReturn(user);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new TestingAuthenticationToken(user, null));

    }

    @Test
    public void shouldCreateAnUser() throws Exception {
        Mockito.when(userRepo.findByUsername(anyString())).thenReturn(null);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"TesterUnit\", \"password\": \"testPass123\", \"role_id\": 1 }"))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldMakeLogin() throws Exception {

        Users registeredUser = new Users();
        registeredUser.setId(2);
        registeredUser.setUsername("savedUser");
        registeredUser.setPassword(passwordEncoder.encode("savedPass123"));
        registeredUser.setRole(role);

        when(userRepo.findByUsername("savedUser")).thenReturn(registeredUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new TestingAuthenticationToken(registeredUser, null, "ROLE_USUARIO"));

        when(tokenService.generateToken(any(Users.class))).thenReturn("token");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"savedUser\", \"password\": \"savedPass123\" }"))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        assertFalse(token.isEmpty());
        assertEquals(5, token.length());
    }

    @Test
    public void shoulNotMakeLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"NotUser\", \"password\": \"wrongPass\" }"))
                .andExpect(status().isUnauthorized());
    }

}
