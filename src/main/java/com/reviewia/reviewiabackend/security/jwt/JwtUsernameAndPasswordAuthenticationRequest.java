package com.reviewia.reviewiabackend.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtUsernameAndPasswordAuthenticationRequest {
    private String email;
    private String password;
}
