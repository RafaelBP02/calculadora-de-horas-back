package br.com.calculadorahoras.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.calculadorahoras.api.model.Roles;

@Repository
public interface RoleRepo extends CrudRepository<Roles, Integer>{
    
}
