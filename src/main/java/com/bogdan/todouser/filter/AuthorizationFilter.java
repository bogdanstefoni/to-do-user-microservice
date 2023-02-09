package com.bogdan.todouser.filter;

import com.bogdan.todouser.util.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.bogdan.todouser.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.bogdan.todouser.constant.SecurityConstant.TOKEN_PREFIX;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    private JWTTokenProvider provider;

    public AuthorizationFilter(JWTTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);

                return;
            }

            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = provider.getSubject(token);

            if (provider.isTokenValid(username, token)) {
                List<GrantedAuthority> authorities = provider.getAuthorities(token);
                Authentication authentication = provider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        logger.info("Authorization token : " + provider);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return StringUtils.equalsAny(path, "/users/login/", "/users/register/");
    }
}
