package br.com.calculadorahoras.api.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.calculadorahoras.api.model.Users;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Users user) {
        
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = new String("{\"username\":\""+user.getUsername()+"\",\"userId\":"+user.getId()+"}");
            
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(subject)
                    .withClaim("papel", user.getRole().getRoleName())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token,", exception);
        }
    }

    public String validateToken(String token) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth-api")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        String subject = jwt.getSubject();
        System.out.println("Token validado com sucesso. Subject: " + subject);

        return subject;
    } catch (JWTVerificationException exception) {
        System.err.println("Erro na validação do token: " + exception.getMessage());
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
