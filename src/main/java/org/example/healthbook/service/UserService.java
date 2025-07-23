package org.example.healthbook.service;

import org.example.healthbook.model.Role;
import org.example.healthbook.model.User;
import org.example.healthbook.repository.RoleRepository;
import org.example.healthbook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerPatient(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }

        Role patientRole = roleRepository.findById("ROLE_PATIENT")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_PATIENT не найдена"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(patientRole));

        return userRepository.save(user);
    }

    public User registerDoctor(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }

        Role doctorRole = roleRepository.findById("ROLE_DOCTOR")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_DOCTOR не найдена"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(doctorRole));

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}