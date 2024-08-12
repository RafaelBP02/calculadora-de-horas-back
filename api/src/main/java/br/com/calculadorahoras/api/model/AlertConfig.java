package br.com.calculadorahoras.api.model;

import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alarm_config")
@Getter
@Setter
public class AlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private Time workEntry;

    private Time intervalBeginning ;

    private Time intervalEnd;

    private Time workEnd;

    private int workload;

    private int user_id;  ;

}
