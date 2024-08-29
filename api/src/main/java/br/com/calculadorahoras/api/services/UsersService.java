package br.com.calculadorahoras.api.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.UserRepo;
import lombok.var;

@Service
public class UsersService implements UserDetailsService{
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = userRepo.findByUsername("Cebolinha");
        System.out.println(user);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                .username(userObj.getUsername())
                .password(userObj.getPassword())
                .build();
        }

        throw new UsernameNotFoundException("Usuário não encontrado, faça cadastro");

    }

}
