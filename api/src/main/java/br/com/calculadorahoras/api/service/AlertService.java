package br.com.calculadorahoras.api.service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.calculadorahoras.api.controller.AlertController;
import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

@Service
public class AlertService {
    @Autowired
    private Repo repo;

    @Autowired
    private AlertController alertController;

    @Scheduled(fixedRate = 30000) // Verify each minute
    public void verificarHorariosDePonto() {
        Iterable<AlertConfig> savedAlerts = repo.findAll();
        LocalTime now = LocalTime.now();

        for (AlertConfig alert : savedAlerts) {
            // enviarAlerta(alert.getId(), "Teste do scheduler da notificacao!");
            notificaUsuario(alert, now);
        }
    }

    public void enviarAlerta(int userId, String mensagem) {
        if (alertController.hasEmitter(userId)) {
            System.out.println("Enviando alerta para o usuário ID " + userId + ": " + mensagem);
            alertController.sendAlert(userId, mensagem);
        } else {
            System.out.println("Nenhum SseEmitter encontrado para o usuário ID " + userId);
        }
    }

    private void notificaUsuario(AlertConfig alert, LocalTime now) {
        System.out.println("Verifica se deve alertar o usuário ID " + alert.getUser_id());

        if (deveAcionarAlerta(alert.getWorkEntry().toLocalTime(), now)) {
            enviarAlerta(alert.getUser_id(), "Hora de bater o ponto de entrada!");
        } else if (deveAcionarAlerta(alert.getIntervalBeginning().toLocalTime(), now)) {
            enviarAlerta(alert.getUser_id(), "Hora de iniciar o intervalo!");
        } else if (deveAcionarAlerta(alert.getIntervalEnd().toLocalTime(), now)) {
            enviarAlerta(alert.getUser_id(), "Hora de terminar o intervalo!");
        } else if (deveAcionarAlerta(alert.getWorkEnd().toLocalTime(), now)) {
            enviarAlerta(alert.getUser_id(), "Hora de encerrar o expediente!");
        }
    }

    private boolean deveAcionarAlerta(LocalTime ponto, LocalTime now) {
        long diffInMinutes = Math.abs(ChronoUnit.MINUTES.between(now, ponto));
        return diffInMinutes > 0 && diffInMinutes <= 2;
    }

}
