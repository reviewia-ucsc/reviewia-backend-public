package com.reviewia.reviewiabackend.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getDetails(@RequestParam("email") String email) {
        User fromDB = userService.getUser(email);
        UserResponse user = new UserResponse(
                fromDB.getId(),
                fromDB.getFirstName(),
                fromDB.getLastName(),
                fromDB.getEmail(),
                fromDB.getRole(),
                fromDB.isLocked(),
                fromDB.isEnabled(),
                fromDB.getAvatar(),
                fromDB.getReportCount()
//                fromDB.getFavouriteList(),
//                fromDB.getNotifications()
        );
        return ResponseEntity.ok(user);
    }

    @GetMapping("/lock/all")
    public ResponseEntity<List<UserView>> getLockedUsers() {
        return ResponseEntity.ok(userService.getBlockedUsers());
    }
}
