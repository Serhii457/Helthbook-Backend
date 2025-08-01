package org.example.healthbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.healthbook.model.Patient;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private Long id;
    private String fullName;
    private String phone;
    private Long userId;

    public static PatientDTO fromEntity(Patient patient) {
        return new PatientDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getPhone(),
                patient.getUser() != null ? patient.getUser().getId() : null
        );
    }
}
