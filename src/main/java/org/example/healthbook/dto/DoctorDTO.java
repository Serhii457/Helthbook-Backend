package org.example.healthbook.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorDTO {
    private Long id;
    private String fullName;
    private String specialization;
    private String phone;
    private String photoUrl;

    private List<ScheduleDayDTO> schedule;
}
