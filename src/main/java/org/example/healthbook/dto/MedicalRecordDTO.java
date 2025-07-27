package org.example.healthbook.dto;

import lombok.Data;
import org.example.healthbook.model.MedicalRecord;

import java.time.format.DateTimeFormatter;

@Data
public class MedicalRecordDTO {

    private Long id;
    private String diagnosis;
    private String recommendations;
    private String comment;

    private Long appointmentId;
    private String appointmentDate;

    private Long patientId;
    private String patientName;

    private String createdAt;

    public static MedicalRecordDTO fromEntity(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setRecommendations(record.getRecommendations());
        dto.setComment(record.getComment());

        if (record.getAppointment() != null) {
            dto.setAppointmentId(record.getAppointment().getId());
            dto.setAppointmentDate(record.getAppointment().getDate().toString());
        }

        if (record.getPatient() != null) {
            dto.setPatientId(record.getPatient().getId());
            dto.setPatientName(record.getPatient().getFullName());
        }

        if (record.getCreatedAt() != null) {
            dto.setCreatedAt(record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        return dto;
    }
}
