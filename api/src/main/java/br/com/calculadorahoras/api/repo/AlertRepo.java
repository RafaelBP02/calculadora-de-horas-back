package br.com.calculadorahoras.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.calculadorahoras.api.model.AlertConfig;


@Repository
public interface AlertRepo extends CrudRepository<AlertConfig, Integer>{
    AlertConfig findByUserId(int userId);

}
