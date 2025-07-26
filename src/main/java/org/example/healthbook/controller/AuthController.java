package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.model.User;
import org.example.healthbook.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestParam String username, @RequestParam String password) {
        return userService.registerPatient(username, password);
    }

    @PostMapping("/registerDoctor")
    public User registerDoctor(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String fullName,
                               @RequestParam String phone,
                               @RequestParam Long specializationId) {
        return userService.registerDoctor(username, password, fullName, phone, specializationId);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.findByUsername(authentication.getName())
                .map(user -> {
                    String role = user.getRoles().stream()
                            .map(roleObj -> roleObj.getName().replace("ROLE_", ""))
                            .findFirst()
                            .orElse("UNKNOWN");

                    return ResponseEntity.ok(Map.of(
                            "username", user.getUsername(),
                            "role", role
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}