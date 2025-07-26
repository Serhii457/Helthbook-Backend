package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.model.User;
import org.example.healthbook.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/create-doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public User createDoctor(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam Long specializationId) {
        return userService.registerDoctor(username, password, fullName, phone, specializationId);
    }
}
