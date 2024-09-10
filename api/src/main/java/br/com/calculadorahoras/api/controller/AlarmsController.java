package br.com.calculadorahoras.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;
import br.com.calculadorahoras.utils.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("alarms")
public class AlarmsController {

    @Autowired
    private Repo action;

    @GetMapping
    public ResponseEntity<?> selectAllAlarmConfigs() {
        Iterable<AlertConfig> response = action.findAll();
        if (!response.iterator().hasNext()) {
            // retorna status 500 se a colecao de elementos estiver vazia
            return new ResponseEntity<>(new ErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertConfig> selectAlarmConfig(@PathVariable Integer id) {
        AlertConfig response = action.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Dado inexistente. Alerta não foi configurado"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> registerAlarmConfig(@RequestBody AlertConfig ac) {
        try {
            AlertConfig savedConfig = action.save(ac);
            return new ResponseEntity<AlertConfig>(savedConfig, HttpStatus.OK);
        } catch (Exception e) {
            // Tratamento de erro genérico para status 500
            return new ResponseEntity<>(
                    new ErrorResponse("Não foi possível salvar sua configuração. Erro na comunicação com o servidor"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> editAlarmConfig(@RequestBody AlertConfig ac) {
        try {
            if (!action.existsById(ac.getId())) {
                // Se o ID não existir, retorna 404
                return new ResponseEntity<>(new ErrorResponse("Essa configuração de alarme não existe"),
                        HttpStatus.NOT_FOUND);
            }
            AlertConfig updatedConfig = action.save(ac);
            return new ResponseEntity<AlertConfig>(updatedConfig, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
