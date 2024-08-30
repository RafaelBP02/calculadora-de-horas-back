package br.com.calculadorahoras.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculadorahoras.api.model.Roles;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping( "/signup")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Roles role = roleRepo.findById(2)
            .orElseThrow(() -> new IllegalArgumentException("Role não encontrado"));
        
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users savedConfig = userRepo.save(user);
        return new ResponseEntity<>(savedConfig, HttpStatus.OK);
    }

}
