package org.example.healthbook.repository;

import org.example.healthbook.model.Appointment;
import org.example.healthbook.model.Doctor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @EntityGraph(attributePaths = {"doctor", "patient"})
    List<Appointment> findAll();

    @EntityGraph(attributePaths = {"doctor", "patient"})
    List<Appointment> findByDoctorUserUsername(String username);

    @EntityGraph(attributePaths = {"doctor", "patient"})
    Page<Appointment> findByDoctorUserUsername(String username, Pageable pageable);

    List<Appointment> findByDoctor(Doctor doctor);

    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    Optional<Appointment> findByDoctorAndDateAndTime(Doctor doctor, LocalDate date, LocalTime time);

    @Query("SELECT a.date, COUNT(a) FROM Appointment a " +
            "WHERE a.date BETWEEN :from AND :to " +
            "GROUP BY a.date ORDER BY a.date")
    List<Object[]> countAppointmentsPerDay(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a " +
            "WHERE a.date BETWEEN :from AND :to " +
            "GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT a.doctor.user.fullName, COUNT(a) FROM Appointment a " +
            "WHERE a.date BETWEEN :from AND :to " +
            "GROUP BY a.doctor.user.fullName " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findTopDoctorsByAppointments(@Param("from") LocalDate from, @Param("to") LocalDate to);

}
