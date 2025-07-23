package org.example.healthbook.service;

import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.model.MedicalRecord;
import org.example.healthbook.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository){
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public List<MedicalRecordDTO> getAll() {
        return medicalRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MedicalRecordDTO> findById(Long id) {
        return medicalRecordRepository.findById(id)
                .map(this::convertToDTO);
    }

    public MedicalRecord save(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    public void deleteById(Long id) {
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setRecommendations(record.getRecommendations());

        if (record.getAppointment() != null) {
            dto.setAppointmentId(record.getAppointment().getId());
            dto.setAppointmentDate(record.getAppointment().getDate().toString());
        }
        if (record.getPatient() != null) {
            dto.setPatientId(record.getPatient().getId());
            //dto.setPatientName(record.getPatient().getFullName());
        }
        return dto;
    }
}