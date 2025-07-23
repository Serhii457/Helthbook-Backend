package org.example.healthbook.dto;

import lombok.Data;

@Data
public class PatientDTO {
    private Long id;
    private String fullName;
    private String phone;
    private Long userId;
//    private String username;
}
