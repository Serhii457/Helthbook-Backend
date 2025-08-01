package org.example.healthbook.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalRecordDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientFullName;
    private String diagnosis;
    private String comment;
    private LocalDate date;
    private String patientPhone;

}
