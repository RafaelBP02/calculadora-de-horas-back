package br.com.calculadorahoras.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import br.com.calculadorahoras.api.model.Roles;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.services.TokenService;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTokenizationTest {
    private Roles roles;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void setup(){
        roles = new Roles();
        roles.setId(1);
        roles.setRoleName("USUARIO");
        roles.setDetails("Teste de token");
    }

    @Test
    public void shouldGenerateToken(){
        
        Users user = new Users();
        user.setUsername("justAnUser");
        user.setRole(roles);

        String token = tokenService.generateToken(user);
        Assert.notNull(token, "failed");
    }

    @Test
    public void shouldValidateToken(){
        Users user = new Users();
        user.setUsername("justAnUser");
        user.setId(7);
        user.setRole(roles);

        String token = tokenService.generateToken(user);

        String validation = tokenService.validateToken(token);

        assertEquals("{\"username\":\"justAnUser\",\"userId\":7}", validation);
    }

    @Test
    public void shouldNotValidateToken(){
        String validation = tokenService.validateToken("invalidToken");

        assertEquals("erro na validacao", validation);
    }

}
