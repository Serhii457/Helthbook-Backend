package org.example.healthbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopDoctorDTO {
    private String doctorName;
    private Long count;
}