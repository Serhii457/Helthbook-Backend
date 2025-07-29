package org.example.healthbook.repository;

import org.example.healthbook.model.AppointmentRequest;
import org.example.healthbook.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {
    void deleteAllByPatientId(Long patientId);
    Page<AppointmentRequest> findByDoctor(Doctor doctor, Pageable pageable);

}
