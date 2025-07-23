package org.example.healthbook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnosis;

    private String recommendations;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
