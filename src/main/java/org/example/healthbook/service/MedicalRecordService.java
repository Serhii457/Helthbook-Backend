package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.MedicalRecordCreateDTO;
import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.MedicalRecord;
import org.example.healthbook.model.Patient;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.MedicalRecordRepository;
import org.example.healthbook.repository.PatientRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public List<MedicalRecordDTO> getAllRecords() {
        return medicalRecordRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MedicalRecordDTO> getRecordsByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MedicalRecordDTO createRecord(MedicalRecordCreateDTO dto, String doctorUsername) {
        Doctor doctor = doctorRepository.findByUserUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        MedicalRecord record = new MedicalRecord();
        record.setDoctor(doctor);
        record.setPatient(patient);
        record.setDiagnosis(dto.getDiagnosis());
        record.setComment(dto.getComment());
        record.setCreatedAt(LocalDateTime.now());

        return toDTO(medicalRecordRepository.save(record));
    }

    public MedicalRecordDTO updateRecord(Long id, MedicalRecordCreateDTO dto, String doctorUsername) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));

        if (!record.getDoctor().getUser().getUsername().equals(doctorUsername)) {
            throw new RuntimeException("Access denied");
        }

        Doctor doctor = doctorRepository.findByUserUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        record.setDoctor(doctor); // обычно не меняется, но на всякий случай
        record.setPatient(patient); // тоже можно не менять, если нужно
        record.setDiagnosis(dto.getDiagnosis());
        record.setComment(dto.getComment());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return toDTO(saved);
    }


    public void deleteRecordById(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new RuntimeException("Medical record not found");
        }
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO toDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setComment(record.getComment());
        dto.setDate(record.getCreatedAt().toLocalDate());
        dto.setDoctorId(record.getDoctor().getId());
        dto.setDoctorName(record.getDoctor().getUser().getFullName());
        dto.setPatientId(record.getPatient().getId());
        dto.setPatientName(record.getPatient().getUser().getFullName());
        dto.setPatientPhone(record.getPatient().getUser().getPhone());
        return dto;
    }

    public List<MedicalRecordDTO> getAllRecordsForDoctor(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorId(doctor.getId());

        return records.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
