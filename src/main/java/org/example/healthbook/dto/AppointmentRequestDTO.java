package org.example.healthbook.dto;

import lombok.Data;

@Data
public class AppointmentRequestDTO {
//    private String name;
//    private String phone;

    //for AdminPatientsPage
    private Long doctorId;
    private String date;     // у форматі "yyyy-MM-dd"
    private String time;     // "HH:mm"
    private String fullName; // 🔸 потрібне для створення User/Patient
    private String phone;    // 🔸 ключ для пошуку User
    private String note;
}