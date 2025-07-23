package org.example.healthbook.repository;

import org.example.healthbook.model.AppointmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {
    void deleteAllByPatientId(Long patientId);
}
