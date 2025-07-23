package org.example.healthbook.dto;

import lombok.Data;

@Data
public class MedicalRecordDTO {

    private Long id;
    private String diagnosis;
    private String recommendations;

    private Long appointmentId;
    private String appointmentDate;

    private Long patientId;
    private String patientName;
}