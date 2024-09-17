package br.com.calculadorahoras.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
public class UserTokenSubjectBody {
    private String username;
    private Integer userId;

    public UserTokenSubjectBody(){}

    public UserTokenSubjectBody(String username, Integer userId) {
        this.username = username;
        this.userId = userId;
    }

    public static UserTokenSubjectBody convertStringToJson(String data) throws JsonMappingException, JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        
        return objectMapper.readValue(data, UserTokenSubjectBody.class);
    }
}
