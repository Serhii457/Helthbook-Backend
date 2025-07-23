package org.example.healthbook.dto;

import lombok.Data;

@Data
public class ScheduleDTO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private String dayOfWeek;
    private String startTime;
}