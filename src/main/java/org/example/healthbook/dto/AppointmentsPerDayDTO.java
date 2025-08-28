package org.example.healthbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AppointmentsPerDayDTO {
    private LocalDate date;
    private Long count;
}
