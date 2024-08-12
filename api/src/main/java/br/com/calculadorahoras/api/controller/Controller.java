package br.com.calculadorahoras.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin(origins = "*")
public class Controller {

    @Autowired
    private Repo action;

    @GetMapping("/")
    public Iterable<AlertConfig> selectAllAlarmConfigs(){
        return action.findAll();
    }

    @GetMapping("/{id}")
    public AlertConfig selectAlarmConfig(@PathVariable Integer id){
       return action.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Dado inexistente. Alerta não foi configurado")
       );
    }

    @PostMapping("/")
    public AlertConfig registerAlarmConfig(@RequestBody AlertConfig ac){
        return action.save(ac);
    }

    @PutMapping("/")
    public AlertConfig editAlarmConfig(@RequestBody AlertConfig ac){
        return action.save(ac);
    }
}
