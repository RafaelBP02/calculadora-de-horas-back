package br.com.calculadorahoras.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.calculadorahoras.api.model.AlertConfig;

@Repository
public interface Repo extends CrudRepository<AlertConfig, Integer>{

}
