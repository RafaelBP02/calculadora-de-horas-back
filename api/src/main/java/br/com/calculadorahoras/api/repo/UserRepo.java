package br.com.calculadorahoras.api.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.calculadorahoras.api.model.Users;


@Repository
public interface UserRepo extends CrudRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
}
