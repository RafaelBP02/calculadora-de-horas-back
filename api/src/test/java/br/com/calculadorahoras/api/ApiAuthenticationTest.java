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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.calculadorahoras.api.dtos.UserDTO;
import br.com.calculadorahoras.api.model.Roles;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;
import br.com.calculadorahoras.api.services.TokenService;
import br.com.calculadorahoras.utils.UserTokenSubjectBody;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAuthenticationTest {
    Roles role = new Roles();
    Users user = new Users();


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
    public void shouldHaveUserInfo() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldHaveAdminPrivilege(){
        role.setId(2);
        role.setRoleName("ADMINISTRADOR");

        assertEquals(user.getAuthorities(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")));
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
    public void shouldNotCreateAnUser() throws Exception {
        Mockito.when(userRepo.findByUsername(anyString())).thenReturn(new Users());

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"TesterUnit\", \"password\": \"testPass123\", \"role_id\": 1 }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrowUserCreationError() throws Exception {
        Mockito.when(userRepo.findByUsername(anyString())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"TesterUnit\", \"password\": \"testPass123\", \"role_id\": 1 }"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"errorMessage\":\"Erro no processamento: java.lang.RuntimeException\"}"));
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
        assertEquals(17, token.length());
    }

    @Test
    public void shoulNotMakeLogin() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"NotUser\", \"password\": \"wrongPass\" }"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shoulNotFindUserLogin() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new InternalAuthenticationServiceException("Usuario inexistente"));
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"NotUser\", \"password\": \"wrongPass\" }"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowLoginError() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException());
    
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"NotUser\", \"password\": \"wrongPass\" }"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"errorMessage\":\"Erro no processamento: java.lang.RuntimeException\"}"));
    }

    @Test
    @WithMockUser(username = "Picapau", roles = { "ADMIN" })
    public void shouldListAllUsers() throws Exception{
        UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getUsername(), 
                        user.getName(), 
                        user.getSureName(), 
                        user.getWorkPlace(),
                        user.getRole());
                    
        ObjectMapper objectUserMapper = new ObjectMapper();
        String dtoJson = objectUserMapper.writeValueAsString(userDTO);
        
        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Picapau", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJsonToken = objectMapper.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);
        when(userRepo.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Picapau", roles = { "ADMIN" })
    public void shouldNotListAllUsers() throws Exception{
                    
        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Picapau", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJsonToken = objectMapper.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);
        when(userRepo.findAll()).thenReturn(new ArrayList<Users>());

        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"errorMessage\":\"Nenhum usuario encontrado\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Picapau", roles = { "ADMIN" })
    public void shouldThrowExceptionWhenListAllUsers() throws Exception{
                    
        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Picapau", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJsonToken = objectMapper.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);
        when(userRepo.findAll()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"errorMessage\":\"Erro interno\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test 
    @WithMockUser(username = "Picapau", roles = { "ADMIN" })
    public void shouldUpdateUser() throws Exception{

        UserDTO userDTO = new UserDTO(
            1,
            "testUser@mail.com", 
            "name", 
            "surename", 
            "updatedWorkplace",
            role);

        ObjectMapper objectMapper = new ObjectMapper();
        String dtoJson = objectMapper.writeValueAsString(userDTO);

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Picapau", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tokenService.getClaim(anyString(), anyString())).thenReturn("ADMINISTRADOR");

        mockMvc.perform(put("/users/update")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sucesso\":\"usuario atualizado sem erros!\"}"));

    }

    @Test 
    @WithMockUser(username = "Picapau", roles = { "USER" })
    public void shouldNotHavePermissionToUpdateUser() throws Exception{

        UserDTO userDTO = new UserDTO(
            1,
            "testUser@mail.com", 
            "name", 
            "surename", 
            "updatedWorkplace",
            role);

        ObjectMapper objectMapper = new ObjectMapper();
        String dtoJson = objectMapper.writeValueAsString(userDTO);

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Picapau", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tokenService.getClaim(anyString(), anyString())).thenReturn("USUARIO");

        mockMvc.perform(put("/users/update")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"errorMessage\":\"Este usuario nao possui permissao para realizar essa operação\"}"));

    }

    @Test 
    @WithMockUser(username = "Picapau", roles = { "ADMINISTRADOR" })
    public void shouldThrowUpdateUserException() throws Exception{

        UserTokenSubjectBody validToken = new UserTokenSubjectBody("Picapau", 5);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String validJsonToken = objectMapper2.writeValueAsString(validToken);

        when(tokenService.validateToken("valid_token")).thenReturn(validJsonToken);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tokenService.getClaim(anyString(), anyString())).thenReturn("ADMINISTRADOR");

        mockMvc.perform(put("/users/update")
                .header("Authorization", "Bearer valid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());

    }

    

    





}
