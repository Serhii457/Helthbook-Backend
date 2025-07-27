package org.example.healthbook.repository;

import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findAllByAppointmentDoctor(Doctor doctor);
}
