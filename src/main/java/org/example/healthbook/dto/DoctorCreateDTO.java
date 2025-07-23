package org.example.healthbook.dto;

import lombok.Data;

@Data
public class DoctorCreateDTO {
    private String fullName;
    private String phone;
    private String username;
    private Long specializationId;
}