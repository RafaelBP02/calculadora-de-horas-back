package br.com.calculadorahoras.api.controller;

import org.springframework.web.bind.annotation.RestController;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.api.repo.AlertRepo;
import br.com.calculadorahoras.api.repo.UserRepo;
import br.com.calculadorahoras.api.services.TokenService;
import br.com.calculadorahoras.api.services.UsersService;
import br.com.calculadorahoras.utils.ErrorResponse;
import br.com.calculadorahoras.utils.UserTokenSubjectBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin(origins = "*")
public class AlarmsController {

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private TokenService tokenService;

    @GetMapping("alarms/all")
    public ResponseEntity<?> selectAllAlarmConfigs(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Iterable<AlertConfig> response = alertRepo.findAll();
            if (!response.iterator().hasNext()) {
                return new ResponseEntity<>(new ErrorResponse(), HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ErrorResponse("Erro no processamento: " + e), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }


    //Deprecated endpoint
    @GetMapping("alarms/{id}")
    public ResponseEntity<?> selectAlarmConfig(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        try {
            UserTokenSubjectBody verified = UserTokenSubjectBody.convertStringToJson(tokenService.validateToken(token));
            if(verified.getUserId() == id){
                AlertConfig response = alertRepo.findByUserId(id);
                if (response == null) {
                    return new ResponseEntity<>(
                        new ErrorResponse("Esse usuario não possui um alerta configurado"), 
                        HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(response, HttpStatus.OK);    
            }
            else{
                return new ResponseEntity<>(
                    new ErrorResponse("Este usuario nao possui a devida autorizacao"),
                    HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ErrorResponse("Erro no processamento: " + e), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }      
        
    }

    @GetMapping("v2/alarms")
    public ResponseEntity<?> selectAlarmConfig(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        try {
            UserTokenSubjectBody verified = UserTokenSubjectBody.convertStringToJson(tokenService.validateToken(token));
            AlertConfig response = alertRepo.findByUserId(verified.getUserId());
            if (response == null) {
                return new ResponseEntity<>(
                    new ErrorResponse("Esse usuario não possui um alerta configurado"), 
                    HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);    

        } catch (Exception e) {
            return new ResponseEntity<>(
                new ErrorResponse("Erro no processamento: " + e), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }      
        
    }

    @PostMapping("alarms")
    public ResponseEntity<?> registerAlarmConfig(@RequestBody AlertConfig ac, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        
        try {
            UserTokenSubjectBody verified = UserTokenSubjectBody.convertStringToJson(tokenService.validateToken(token));
            if (ac.getUserId() == verified.getUserId()){
                AlertConfig savedConfig = alertRepo.save(ac);
                return new ResponseEntity<AlertConfig>(savedConfig, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(
                    new ErrorResponse("Este usuario nao possui a devida autorizacao"),
                    HttpStatus.UNAUTHORIZED);
            }
            
        } catch (Exception e) {
            // Tratamento de erro genérico para status 500
            return new ResponseEntity<>(
                    new ErrorResponse("Não foi possível salvar sua configuração. Erro na comunicação com o servidor"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("alarms")
    public ResponseEntity<?> editAlarmConfig(@RequestBody AlertConfig ac, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        try {
            UserTokenSubjectBody verified = UserTokenSubjectBody.convertStringToJson(tokenService.validateToken(token));
            if (ac.getUserId() == verified.getUserId()) {
                AlertConfig originalData = alertRepo.findByUserId(ac.getUserId());
                if (originalData == null) {
                    // Se o ID não existir, retorna 404
                    return new ResponseEntity<>(new ErrorResponse("Essa configuração de alarme não existe"),
                            HttpStatus.NOT_FOUND);
                }
                originalData.setWorkEntry(ac.getWorkEntry());
                originalData.setIntervalBeginning(ac.getIntervalBeginning());
                originalData.setIntervalEnd(ac.getIntervalEnd());
                originalData.setWorkEnd(ac.getWorkEnd());
                originalData.setWorkload(ac.getWorkload());

                AlertConfig updatedConfig = alertRepo.save(originalData);
                return new ResponseEntity<AlertConfig>(updatedConfig, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(
                    new ErrorResponse("Este usuario nao possui a devida autorizacao"),
                    HttpStatus.UNAUTHORIZED);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
