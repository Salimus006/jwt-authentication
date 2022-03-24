package com.devo.jwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to filter requests after authentication (requests with generated token)
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. set allowed headers
        setFrontendHeaders(response);

        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            // 1.1. if OPTIONS no need to check security
            response.setStatus(HttpServletResponse.SC_OK);
        }else {
            // 1.2. get request token
            String jwtToken = request.getHeader("Authorization");
            // check if token is present in the header
            if(jwtToken == null || !jwtToken.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }

            // 3 Get claims (to retrieve roles, userName etc)
            Claims claims = Jwts.parser()
                    .setSigningKey("mySecret")
                    .parseClaimsJws(jwtToken.replace("Bearer ", ""))
                    .getBody();

            String userName = claims.getSubject();

            // Spring user with userName no password and authorities (constructor new User... will setAuthenticated to true)
            UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(userName, null, buildSpringAuthorities(claims));

            // add authenticatedUser to spring security context
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

            filterChain.doFilter(request, response);
        }
    }

    /**
     * Construct Spring authorities from clams token
     *
     * @param claims
     * @return
     */
    private static List<GrantedAuthority> buildSpringAuthorities(Claims claims){

        List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();

        roles.forEach(role -> {
            if(role.containsKey("authority")){
                authorities.add(new SimpleGrantedAuthority(role.get("authority")));
            }
        });

        return authorities;
    }

    private void setFrontendHeaders(HttpServletResponse response){
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        // Allowed headers from frontend
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.ORIGIN + ", " + HttpHeaders.ACCEPT
                + ", " + HttpHeaders.CONTENT_TYPE + ", " + HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD + ", "
                + HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS + ", " + HttpHeaders.AUTHORIZATION);

        // allow frontend to read headers from response
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION + ", " +
                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN + ", " + HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
    }
}
