package org.example.healthbook.repository;

import org.example.healthbook.model.Patient;
import org.example.healthbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);
    Optional<Patient> findByPhone(String phone);
}
