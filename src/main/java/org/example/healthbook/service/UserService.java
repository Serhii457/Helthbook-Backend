package org.example.healthbook.service;

import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.Role;
import org.example.healthbook.model.Specialization;
import org.example.healthbook.model.User;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.RoleRepository;
import org.example.healthbook.repository.SpecializationRepository;
import org.example.healthbook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpecializationRepository specializationRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       DoctorRepository doctorRepository,
                       PasswordEncoder passwordEncoder,
                       SpecializationRepository specializationRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
        this.specializationRepository = specializationRepository;
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

public User registerDoctor(String username, String password, String fullName, String phone, Long specializationId) {
    if (userRepository.findByUsername(username).isPresent()) {
        throw new RuntimeException("Користувач вже існує");
    }

    Role doctorRole = roleRepository.findById("ROLE_DOCTOR")
            .orElseThrow(() -> new RuntimeException("Роль ROLE_DOCTOR не знайдена"));

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setRoles(Collections.singleton(doctorRole));
    user.setFullName(fullName);
    user.setPhone(phone != null ? phone : "");

    User savedUser = userRepository.save(user);

    Doctor doctor = new Doctor();
    doctor.setUser(savedUser);
    //doctor.setFullName(fullName);
    //doctor.setPhone(phone);

    Specialization specialization = specializationRepository.findById(specializationId)
            .orElseThrow(() -> new RuntimeException("Спеціалізацію не знайдено"));
    doctor.setSpecialization(specialization);

    doctor.setPhotoUrl("");

    doctorRepository.save(doctor);

    return savedUser;
}

public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}