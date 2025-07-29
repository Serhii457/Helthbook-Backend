package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.PatientRepository;
import org.example.healthbook.repository.AppointmentRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<AppointmentRequestDTO> getRequestsForDoctor(String username, int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sortObj;
        if ("fullName".equalsIgnoreCase(sortField)) {
            sortObj = Sort.by(sortDirection, "fullName");
        } else if ("date".equalsIgnoreCase(sortField)) {
            sortObj = Sort.by(sortDirection, "date").and(Sort.by(sortDirection, "time"));
        } else {
            sortObj = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "time"));
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return requestRepository.findByDoctor(doctor, pageable)
                .map(AppointmentRequestDTO::fromEntity);
    }

}