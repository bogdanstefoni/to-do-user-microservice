package com.bogdan.todouser.Filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bogdan.todouser.enums.ErrorsEnum;
import com.bogdan.todouser.properties.AuthorizationProperties;
import com.bogdan.todouser.service.CustomRequestBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Autowired
    private AuthorizationProperties authorizationProperties;

    @Autowired
    private CustomRequestBean customRequestBean;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String jwtToken = response.getHeader("jwtHeader");

        if(StringUtils.isBlank(jwtToken)) {
            logger.error(ErrorsEnum.TOKEN_MISSING.getErrorDescription());
            response.sendError(ErrorsEnum.TOKEN_MISSING.getHttpStatus().value(),
                    ErrorsEnum.TOKEN_MISSING.getErrorDescription());
            return;
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(authorizationProperties.getAlgorithmSecret());
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(authorizationProperties.getIssuer()).build();

            verifier.verify(jwtToken);

            DecodedJWT decodedJwtToken = JWT.decode(jwtToken);

            String userId = decodedJwtToken.getSubject();
            String userName = decodedJwtToken.getClaim("username").asString();

            customRequestBean.setUserId(Long.parseLong(userId));
            customRequestBean.setUserName(userName);

            logger.info("user id {} user name {}", userId, userName);
        }catch (JWTVerificationException e) {
            logger.error(ErrorsEnum.TOKEN_INVALID.getErrorDescription(), e);
            response.sendError(ErrorsEnum.TOKEN_INVALID.getHttpStatus().value(), e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return StringUtils.equalsAny(path, "/users/login", "/users/");
    }
}
