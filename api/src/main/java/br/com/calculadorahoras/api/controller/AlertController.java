package br.com.calculadorahoras.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
public class AlertController {

    // motivo do ConcurrentHashMap: <https://www.baeldung.com/java-concurrent-map>
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/alerts/{userId}")
    public SseEmitter getAlerts(@PathVariable int userId) {
        SseEmitter emitter = new SseEmitter(); // 30 minutos
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        return emitter;
    }

    public void sendAlert(int userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                System.out.println("Enviando alerta para o usuário ID " + userId + ": " + message);
                emitter.send(SseEmitter.event().name("alert").data(message));
            } catch (IOException e) {
                System.out.println("Erro ao enviar alerta: " + e.getMessage());
                emitters.remove(userId);
            }
        } else {
            System.out.println("Nenhum SseEmitter encontrado para o usuário ID " + userId);
        }
    }
}
