package org.example.healthbook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "appointment_requests")
public class AppointmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    private LocalDate date;
    private LocalTime time;

    private String note;

    @Enumerated(EnumType.STRING)
    private AppointmentRequestStatus status;

    @ManyToOne
    @ToString.Exclude
    private Doctor doctor;

    @ManyToOne
    @ToString.Exclude
    private Patient patient;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private Appointment appointment;

}

