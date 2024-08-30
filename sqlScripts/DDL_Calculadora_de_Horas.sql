-- SCRIPT PARA CRIAÇÃO DA BASE DE DADOS
--
-- Sobre: Este script cria a base de dados para a persistencia da configuração
-- dos horarios do ponto
--
-- Author: Rafael Berto Pereira
-- Data Criação: 30/08/2024
-- Última alteração: 30/08/2024
--
-- Histórico: > Criação do script

CREATE DATABASE IF NOT EXISTS CALCULADORA_HORAS;

CREATE TABLE IF NOT EXISTS CALCULADORA_HORAS.APP_ROLES(
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(30) NOT NULL,
    details VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS CALCULADORA_HORAS.USERS (
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(30) NOT NULL,
    role_id INTEGER NOT NULL,
    
    FOREIGN KEY (role_id) REFERENCES CALCULADORA_HORAS.APP_ROLES(id)
);

CREATE TABLE IF NOT EXISTS CALCULADORA_HORAS.ALARM_CONFIG (
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
    work_entry TIME NOT NULL,
    interval_beginning TIME NOT NULL,
    interval_end TIME NOT NULL,
    work_end TIME NOT NULL,
    workload INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES CALCULADORA_HORAS.USERS(id)
);


		