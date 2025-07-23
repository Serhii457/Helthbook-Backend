package org.example.healthbook.dto;

import lombok.Data;

@Data
public class AppointmentDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String date;
    private String time;
    private String status;
}