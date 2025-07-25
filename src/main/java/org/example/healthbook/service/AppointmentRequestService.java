package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.PatientRepository;
import org.example.healthbook.repository.AppointmentRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AppointmentRequestService {

    private final AppointmentRequestRepository requestRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentRequest createAppointmentRequest(AppointmentRequestDTO dto) {
        AppointmentRequest request = new AppointmentRequest();

        request.setFullName(dto.getFullName());
        request.setPhone(dto.getPhone());
        request.setNote(dto.getNote());
        request.setDate(LocalDate.parse(dto.getDate()));
        request.setTime(LocalTime.parse(dto.getTime()));
        request.setStatus(AppointmentRequestStatus.PENDING);

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Лікаря не знайдено з ID: " + dto.getDoctorId()));
        request.setDoctor(doctor);

        return requestRepository.save(request);
    }
}