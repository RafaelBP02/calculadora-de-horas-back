package br.com.calculadorahoras.api.service;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.calculadorahoras.api.model.AlertConfig;
import br.com.calculadorahoras.api.repo.Repo;

@Service
public class AlertService {
    @Autowired
    private Repo repo;

    @Scheduled(fixedRate = 60000) // Verify each minute
    public void verificarHorariosDePonto() {
        Iterable<AlertConfig> savedAlerts = repo.findAll();
        LocalTime now = LocalTime.now();

        for (AlertConfig alert : savedAlerts) {
            // enviarAlerta(alert.getId(), "Teste do scheduler da notificacao!");

            notificaUsuario(alert, now);
        }
    }

    public void enviarAlerta(int userId, String mensagem) {
        System.out.println("Enviando alerta para o usuário ID " + userId + ": " + mensagem);
        // TODO LOGICA PARA ENVIAR ALERTA NO FRONT
    }

    private void notificaUsuario(AlertConfig alert, LocalTime now) {
        if (deveAcionarAlerta(alert.getWorkEntry().toLocalTime(), now)) {
            enviarAlerta(alert.getId(), "Hora de bater o ponto de entrada!");
        } else if (deveAcionarAlerta(alert.getIntervalBeginning().toLocalTime(), now)) {
            enviarAlerta(alert.getId(), "Hora de iniciar o intervalo!");
        } else if (deveAcionarAlerta(alert.getIntervalEnd().toLocalTime(), now)) {
            enviarAlerta(alert.getId(), "Hora de terminar o intervalo!");
        } else if (deveAcionarAlerta(alert.getWorkEnd().toLocalTime(), now)) {
            enviarAlerta(alert.getId(), "Hora de encerrar o expediente!");
        }
    }

    private boolean deveAcionarAlerta(LocalTime ponto, LocalTime now) {
        long diffInMinutes = Math.abs(ChronoUnit.MINUTES.between(now, ponto));
        return diffInMinutes > 0 && diffInMinutes <= 2;
    }

}
