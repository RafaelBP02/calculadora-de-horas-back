package br.com.calculadorahoras.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculadorahoras.api.dtos.UserDTO;
import br.com.calculadorahoras.api.model.Roles;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;
import br.com.calculadorahoras.api.services.TokenService;
import br.com.calculadorahoras.utils.ErrorResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        try {
            if(this.userRepo.findByUsername(user.getUsername()) != null)
                return ResponseEntity.badRequest().build();
            else{
                Roles role = roleRepo.findById(2)
                    .orElseThrow(() -> new IllegalArgumentException("Role não encontrado"));
                
                user.setRole(role);
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                this.userRepo.save(user);

                return ResponseEntity.ok().body("{\"concluido\":\"" + user.getName() + " efetuou seu cadastro com sucesso!\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Erro no processamento: " + e));
        }  
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        try {
            var userPassword = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            var auth = this.authenticationManager.authenticate(userPassword);
            if(auth.isAuthenticated()){
                var token = tokenService.generateToken((Users) auth.getPrincipal());
                Map<String, String> tokenJson = new HashMap<>();
                tokenJson.put("token", token);
                
                return ResponseEntity.ok(tokenJson);
            }
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Erro no processamento: " + e));
        }
        
        
    }

    @GetMapping("users/all")
    public ResponseEntity<?> listAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Iterable<Users> response = userRepo.findAll();
            if (!response.iterator().hasNext()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Nenhum usuario encontrado"));
            } else {
                List<UserDTO>userDTOList = new ArrayList<>();
                
                response.forEach(user -> {
                    UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getUsername(), 
                        user.getName(), 
                        user.getSureName(), 
                        user.getWorkPlace(),
                        user.getRole());
                    
                    userDTOList.add(userDTO);
                });

                return ResponseEntity.ok(userDTOList);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Erro no processamento: " + e));
        }
    }
    

}
