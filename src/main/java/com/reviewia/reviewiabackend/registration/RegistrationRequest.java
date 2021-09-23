package com.reviewia.reviewiabackend.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    @NotBlank(message = "First name required")
    private final String firstName;
    @NotBlank(message = "Last name required")
    private final String lastName;
    @Email(message = "Email required")
    private final String email;
    @Length(min = 8, message = "Password minimum length is 8 chars")
    private final String password;
}