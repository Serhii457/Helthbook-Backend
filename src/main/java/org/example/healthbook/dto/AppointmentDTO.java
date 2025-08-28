package org.example.healthbook.dto;

import lombok.Data;
import org.example.healthbook.model.Appointment;

@Data
public class AppointmentDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;

    private String fullName;
    private String phone;

    private String date;
    private String time;
    private String status;

    public static AppointmentDTO fromEntity(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());

        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getId());
            dto.setDoctorName(appointment.getDoctor().getUser() != null
                    ? appointment.getDoctor().getUser().getFullName()
                    : null);
        }

        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPatientName(appointment.getPatient().getFullName());
            dto.setFullName(appointment.getPatient().getFullName());
            dto.setPhone(appointment.getPatient().getPhone());
        }

        if (appointment.getDate() != null) dto.setDate(appointment.getDate().toString());
        if (appointment.getTime() != null) dto.setTime(appointment.getTime().toString());
        if (appointment.getStatus() != null) dto.setStatus(appointment.getStatus().name());

        return dto;
    }
}