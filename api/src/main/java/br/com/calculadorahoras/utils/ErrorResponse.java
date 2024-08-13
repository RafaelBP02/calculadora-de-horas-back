package br.com.calculadorahoras.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String errorMessage;

    public ErrorResponse(String message) {
        this.errorMessage = message;
    }
    public ErrorResponse() {
        this.errorMessage = "Erro na comunicação com o servidor. Por favor tente mais tarde";
    }
}

