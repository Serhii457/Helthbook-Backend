package org.example.healthbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentsByStatusDTO {
    private String status;
    private Long count;
}
