package br.com.calculadorahoras.api.dtos;

import br.com.calculadorahoras.api.model.Roles;

public record UserDTO(
    int id,
    String eMail,
    String name,
    String surename,
    String workplace,
    Roles role

) {    

}
