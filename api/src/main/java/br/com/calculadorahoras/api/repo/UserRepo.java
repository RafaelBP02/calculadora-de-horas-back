package br.com.calculadorahoras.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.calculadorahoras.api.model.Users;


@Repository
public interface UserRepo extends CrudRepository<Users, Integer> {
    UserDetails findByUsername(String username);
}
