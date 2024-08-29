package br.com.calculadorahoras.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculadorahoras.api.model.Users;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private AuthenticationManager  authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users user) {
        try{
            Authentication authentication =  authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ResponseEntity<>("Login efetuado com sucesso",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Usuario nao encontrado",HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Users user) {
        return new ResponseEntity<>("Novo registro criado", HttpStatus.CREATED);
    }

}
