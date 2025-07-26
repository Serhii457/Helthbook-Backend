package org.example.healthbook.dto;

import lombok.Data;
import org.example.healthbook.model.Specialization;

@Data
public class SpecializationDTO {

    private Long id;
    private String name;

    public static SpecializationDTO fromEntity(Specialization specialization) {
        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(specialization.getId());
        dto.setName(specialization.getName());
        return dto;
    }
}