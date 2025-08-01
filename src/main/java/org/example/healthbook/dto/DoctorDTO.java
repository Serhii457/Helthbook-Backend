package org.example.healthbook.dto;

import lombok.Data;
import org.example.healthbook.model.Doctor;

import java.util.List;

@Data
public class DoctorDTO {
    private Long id;
    private String fullName;
    private String specialization;
    private String phone;
    private String photoUrl;

    private List<ScheduleDayDTO> schedule;

    public static DoctorDTO fromEntity(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setFullName(doctor.getUser() != null ? doctor.getUser().getFullName() : null);
        dto.setPhone(doctor.getUser() != null ? doctor.getUser().getPhone() : null);
        dto.setPhotoUrl(doctor.getPhotoUrl());
        dto.setSpecialization(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : null);
        dto.setSchedule(null);
        return dto;
    }
}
