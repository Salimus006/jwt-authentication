package com.devo.jwt.config;

import com.devo.jwt.dto.LoginForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Filter to define attemptAuthentication (called by spring security when login) and
 * successfulAuthentication if authentication is OK
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super();
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    // method called by Spring to authenticate a user with his userName and password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // try to Get User from request
        LoginForm credentials;
        try {
            credentials = this.objectMapper.readValue(request.getInputStream(), LoginForm.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Not able to get credentials");
        }

        return this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUserName(),
                        credentials.getPassword()));
    }

    // method called by Spring after authenticating a user
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 1. Get Spring User
        User springUser = (User) authResult.getPrincipal();

        // 2. Generate JWT token
        String jwtToken = Jwts.builder()
                .setSubject(springUser.getUsername())
                // token expiration == 10 days
                .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                // HMAC with my secret
                .signWith(SignatureAlgorithm.HS512, "mySecret")
                .claim("roles", springUser.getAuthorities())
                .compact();

        // add token in the header
        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
