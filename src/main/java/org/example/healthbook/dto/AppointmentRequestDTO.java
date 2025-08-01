package org.example.healthbook.dto;

import lombok.Data;
import org.example.healthbook.model.AppointmentRequest;

@Data
public class AppointmentRequestDTO {

    private Long doctorId;
    private String doctorName;
    private String date;
    private String time;
    private String fullName;
    private String phone;
    private String note;
    private Long id;
    private String status;
    private Long patientId;


    public static AppointmentRequestDTO fromEntity(AppointmentRequest request) {
        AppointmentRequestDTO dto = new AppointmentRequestDTO();
        dto.setId(request.getId());
        dto.setFullName(request.getFullName());
        dto.setPhone(request.getPhone());
        dto.setDoctorId(request.getDoctor().getId());
        dto.setDoctorName(request.getDoctor() != null && request.getDoctor().getUser() != null
                ? request.getDoctor().getUser().getFullName()
                : null);

        dto.setDate(request.getDate().toString());
        dto.setTime(request.getTime().toString());
        dto.setNote(request.getNote());
        dto.setStatus(request.getStatus().name());
        if (request.getPatient() != null) {
            dto.setPatientId(request.getPatient().getId());
        }
        return dto;
    }
}