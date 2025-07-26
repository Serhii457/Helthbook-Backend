package org.example.healthbook.dto;

import lombok.Data;
import org.example.healthbook.model.MedicalRecord;

@Data
public class MedicalRecordDTO {

    private Long id;
    private String diagnosis;
    private String recommendations;

    private Long appointmentId;
    private String appointmentDate;

    private Long patientId;
    private String patientName;

    public static MedicalRecordDTO fromEntity(MedicalRecord record) {
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
            dto.setPatientName(record.getPatient().getFullName());
        }

        return dto;
    }
}