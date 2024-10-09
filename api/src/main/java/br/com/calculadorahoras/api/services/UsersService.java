package br.com.calculadorahoras.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.calculadorahoras.api.repo.RoleRepo;
import br.com.calculadorahoras.api.repo.UserRepo;

@Service
public class UsersService implements UserDetailsService{
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UserDetails user = userRepo.findByUsername(username);
        // if(user == null)
        //     throw new UsernameNotFoundException("User not found: " + username);

        return userRepo.findByUsername(username);
    }

}
