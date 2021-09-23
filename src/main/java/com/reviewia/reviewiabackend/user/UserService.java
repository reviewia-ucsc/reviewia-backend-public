package com.reviewia.reviewiabackend.user;

import com.reviewia.reviewiabackend.registration.token.ConfirmationToken;
import com.reviewia.reviewiabackend.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private ConfirmationTokenService confirmationTokenService;

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        System.out.println(email);
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    public boolean checkUser(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private Set getAuthority(User user) {
        Set authorities = new HashSet<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    public String signUpUser(User user) {
        String encodedPassword;
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();

        if (userExists) {
            User userFromDb = userRepository.findByEmail(user.getEmail()).get();
            boolean userEnabled = userFromDb.isEnabled();
            if (userEnabled || userFromDb.isLocked()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "This email is already in use");
            }
            encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setId(userFromDb.getId());
            user.setPassword(encodedPassword);
//            userRepository.save(user);

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user
            );

            confirmationTokenService.saveConfirmationToken(confirmationToken);
            return token;
        }

        encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public String resetPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") {
                        });
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setEnabled(false);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void enableAppUser(String email) {
        userRepository.enableAppUser(email);
    }

    public List<UserView> getBlockedUsers() {
        return userRepository.findAllByLockedTrue();
    }
}
