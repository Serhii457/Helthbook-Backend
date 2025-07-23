package org.example.healthbook.repository;

import org.example.healthbook.model.Appointment;
import org.example.healthbook.model.Doctor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @EntityGraph(attributePaths = {"doctor", "patient"})
    List<Appointment> findAll();

    @EntityGraph(attributePaths = {"doctor", "patient"})
    List<Appointment> findByDoctorUserUsername(String username);

    List<Appointment> findByDoctor(Doctor doctor);

    Optional<Appointment> findByDoctorAndDateAndTime(Doctor doctor, LocalDate date, LocalTime time);

}
