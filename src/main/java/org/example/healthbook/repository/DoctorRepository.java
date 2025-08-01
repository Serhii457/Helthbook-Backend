package org.example.healthbook.repository;

import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserUsername(String username);
    Optional<Doctor> findByUser(User user);
}
