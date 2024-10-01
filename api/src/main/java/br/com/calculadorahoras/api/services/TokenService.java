package br.com.calculadorahoras.api.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.calculadorahoras.api.model.Users;
import br.com.calculadorahoras.utils.UserTokenSubjectBody;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Users user) {
        UserTokenSubjectBody utsb = new UserTokenSubjectBody(user.getUsername(), user.getId());

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            ObjectMapper objectMapper = new ObjectMapper();
            String subject = objectMapper.writeValueAsString(utsb);
            
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(subject)
                    .withClaim("papel", user.getRole().getRoleName())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException | JsonProcessingException exception) {
            throw new RuntimeException("Error while generating token,", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "erro na validacao";
        }
    }

    public String getClaim(String token, String claim){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getClaim(claim)
                    .asString();
            
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Erro ao verificar o token", exception);
        }
    }

    private Instant genExpirationDate() {
        // Esse offset representa o horario de Brasilia
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
