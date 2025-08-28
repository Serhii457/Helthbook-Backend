package org.example.healthbook.dto;

import lombok.Data;

@Data
public class MedicalRecordCreateDTO {
    private Long patientId;
    private String diagnosis;
    private String comment;
    private Long appointmentId;
}
