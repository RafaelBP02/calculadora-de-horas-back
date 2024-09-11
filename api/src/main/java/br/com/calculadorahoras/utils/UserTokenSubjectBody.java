package br.com.calculadorahoras.utils;

import lombok.Getter;

@Getter
public class UserTokenSubjectBody {
    private String username;
    private Integer userId;

    public UserTokenSubjectBody(String username, Integer userId) {
            this.username = username;
            this.userId = userId;
        }
}
