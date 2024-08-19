-- SCRIPT PARA CRIAÇÃO DA BASE DE DADOS
--
-- Sobre: Este script registra operações sql mais utilizadas nessa base de dados
--
-- Author: Rafael Berto Pereira
-- Data Criação: 31/08/2024
-- Última alteração: 31/08/2024
--
-- Histórico: - Criação do script
-- 			  > Popula base com dados iniciais	

-- Base de dados alvo
USE calculadora_horas;

-- Inserção de dados
INSERT INTO USERS(username, passworld) VALUES(
	'fulano', '!@senha@!'
);

INSERT INTO ALARM_CONFIG(work_entry,interval_beginning,interval_end,work_end,workload,user_id) VALUES(
	'09:00:00', '13:00:00', '14:00:00','18:00:00', 8, 1
);

-- Seleção de dados
-- Seleciona os usuarios com suas configurações do alarme ordenado
-- alfabetimente pelo nome do usuario:
SELECT 
	us.username,
	ac.workload,
	ac.work_entry,
    ac.interval_beginning,
    ac.interval_end
	FROM calculadora_horas.alarm_config AS ac
    INNER JOIN calculadora_horas.users AS us 
		ON ac.user_id = us.id
	ORDER BY us.username ASC;

-- Atualização de dados
-- Atualiza o horaio de entrada do usuario
UPDATE ALARM_CONFIG
SET work_entry = '09:30:00'
WHERE user_id = 1;

-- Atualiza nome do usuario
UPDATE USERS
SET username = 'Cebolinha'
WHERE id = 1;

-- Deleção de dados
DROP TABLE ALARM_CONFIG;

DROP TABLE USERS;