package org.example.healthbook.dto;

import lombok.Data;

@Data
public class MedicalRecordCreateDTO {
    private String diagnosis;
    private String recommendations;
    private String comment;
    private Long appointmentId;
    private Long patientId;
}
