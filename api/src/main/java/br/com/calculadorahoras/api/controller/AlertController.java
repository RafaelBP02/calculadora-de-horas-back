package br.com.calculadorahoras.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
public class AlertController {

    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/alerts/{userId}", produces = "text/event-stream")
    public SseEmitter getAlerts(@PathVariable int userId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10 minutos
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        // Keep-alive logic
        new Thread(() -> {
            try {
                while (emitters.containsKey(userId)) {
                    emitter.send(SseEmitter.event().name("keep-alive").data("keep-alive"));
                    Thread.sleep(30000); // Enviar a cada 30 segundos
                }
            } catch (Exception e) {
                emitters.remove(userId);
            }
        }).start();

        return emitter;
    }

    @PostMapping(value = "/alerts/{userId}")
    public void postAlert(@PathVariable int userId, @RequestBody String message) {
        sendAlert(userId, message);
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

    public boolean hasEmitter(int userId) {
        return emitters.containsKey(userId);
    }
}
