package com.reviewia.reviewiabackend.registration;

import com.reviewia.reviewiabackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<?> register(@Valid  @RequestBody RegistrationRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@Valid  @RequestBody RegistrationRequest request) {
        registrationService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(registrationService.confirmToken(token));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<User> update(@RequestParam("email") String email, @RequestParam("first") String firstName, @RequestParam("last") String lastName) {
        return ResponseEntity.ok(registrationService.update(email, firstName, lastName));
    }

    @GetMapping(path = "/update")
    public ResponseEntity<User> update2(@RequestParam("email") String email, @RequestParam("first") String firstName, @RequestParam("last") String lastName) {
        return ResponseEntity.ok(registrationService.update(email, firstName, lastName));
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid  @RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        return ResponseEntity.ok(registrationService.resetPassword(forgetPasswordRequest));
    }
}
