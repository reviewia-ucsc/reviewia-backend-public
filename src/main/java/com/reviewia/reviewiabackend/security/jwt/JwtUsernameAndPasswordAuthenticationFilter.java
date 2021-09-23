package com.reviewia.reviewiabackend.security.jwt;

// Checks pwd and usrname is correct

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // gets user credentials from Request
            JwtUsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(),
                    JwtUsernameAndPasswordAuthenticationRequest.class);

            // check
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    // this invoke only if attemptAuthentication is successful
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        Object[] role = authResult.getAuthorities().toArray();
        String authority = role[0].toString();
        // token build
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authority)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))     // currently expiration set to two weeks
                .signWith(Keys.hmacShaKeyFor("reviewiaaaaaaaiweiverreviewiaaaaaaaiweiver".getBytes()))
                .compact();

//        Cookie cookie = new Cookie("token", token);
//        cookie.setPath("/");
//        cookie.setSecure(true);
//        cookie.setHttpOnly(true);

//        send token to user
        response.addHeader("Authorization", "Bearer " + token);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"" + "token" + "\":\"" + "Bearer "+ token + "\"}");
//        response.addCookie(cookie);
    }
}
