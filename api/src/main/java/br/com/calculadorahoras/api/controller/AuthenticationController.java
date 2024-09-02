package br.com.calculadorahoras.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculadorahoras.api.model.Roles;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;
import br.com.calculadorahoras.api.services.TokenService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        if(this.userRepo.findByUsername(user.getUsername()) != null)
            return ResponseEntity.badRequest().build();
        else{
            Roles role = roleRepo.findById(2)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrado"));
            
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            this.userRepo.save(user);

            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        var userPassword = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        var auth = this.authenticationManager.authenticate(userPassword);

        var token = tokenService.generateToken((Users) auth.getPrincipal());

        return ResponseEntity.ok(token);
    }

}
