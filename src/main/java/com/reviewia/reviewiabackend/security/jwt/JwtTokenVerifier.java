package com.reviewia.reviewiabackend.security.jwt;

import com.reviewia.reviewiabackend.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private UserService userService;

    public JwtTokenVerifier(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (Strings.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(403);
            filterChain.doFilter(request, response);
            return;
        }

        String extractedToken = authorizationHeader.replace("Bearer ", "");

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor("reviewiaaaaaaaiweiverreviewiaaaaaaaiweiver" .getBytes()))
                    .parseClaimsJws(extractedToken);

            Claims body = claimsJws.getBody();
            String username = body.getSubject();
            String authorities = body.get("authorities").toString();
            System.out.println(authorities);

            if(!userService.loadUserByUsername(username).isAccountNonLocked()) {
                response.setStatus(423);
                throw new LockedException("user locked");
            }

            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = Collections.singleton(new SimpleGrantedAuthority(authorities));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            response.setStatus(403);
//            throw new IllegalStateException(String.format("Token %s cannot be trusted", extractedToken));
        }
        filterChain.doFilter(request, response);
    }
}
