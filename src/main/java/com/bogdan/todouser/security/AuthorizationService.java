package com.bogdan.todouser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.bogdan.todouser.properties.AuthorizationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class AuthorizationService {

    @Autowired
    private AuthorizationProperties authorizationProperties;

    public String generateJwtToken(Long userId, String username) {

        String jwtToken = null;

        try {
            final Integer hours = authorizationProperties.getHours();
            final String algorithmSecret = authorizationProperties.getAlgorithmSecret();
            final String issuer = authorizationProperties.getIssuer();

            Algorithm algorithm = Algorithm.HMAC256(algorithmSecret);

            jwtToken = JWT.create().withIssuer(issuer).withSubject(String.valueOf(userId))
                    .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                    .withExpiresAt(
                            Date.from(LocalDateTime.now().plusHours(hours).atZone(ZoneId.systemDefault()).toInstant())
                    )
                    .withClaim("username", username).sign(algorithm);
        } catch (JWTCreationException e) {

            jwtToken = null;
        }

        return jwtToken;

    }
}
