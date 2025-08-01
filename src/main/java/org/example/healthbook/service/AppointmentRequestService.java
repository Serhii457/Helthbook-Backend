package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.model.AppointmentRequestStatus;
import org.example.healthbook.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppointmentRequestService {

    private final AppointmentRequestRepository requestRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppointmentRequest createAppointmentRequest(AppointmentRequestDTO dto) {
        User user = userRepository.findByPhone(dto.getPhone()).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(dto.getPhone());
            newUser.setPassword(passwordEncoder.encode("default123"));
            newUser.setFullName(dto.getFullName());
            newUser.setPhone(dto.getPhone());

            Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                    .orElseThrow(() -> new RuntimeException("Роль ROLE_PATIENT не знайдена"));
            newUser.setRoles(Set.of(patientRole));

            return userRepository.save(newUser);
        });

        Patient patient = patientRepository.findByUser(user).orElseGet(() -> {
            Patient newPatient = new Patient();
            newPatient.setFullName(dto.getFullName());
            newPatient.setPhone(dto.getPhone());
            newPatient.setUser(user);
            return patientRepository.save(newPatient);
        });

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Лікаря не знайдено з ID: " + dto.getDoctorId()));

        AppointmentRequest request = new AppointmentRequest();
        request.setFullName(dto.getFullName());
        request.setPhone(dto.getPhone());
        request.setNote(dto.getNote());
        request.setDate(LocalDate.parse(dto.getDate()));
        request.setTime(LocalTime.parse(dto.getTime()));
        request.setStatus(AppointmentRequestStatus.PENDING);
        request.setDoctor(doctor);
        request.setPatient(patient);

        return requestRepository.save(request);
    }

    public Page<AppointmentRequestDTO> getRequestsForDoctor(String username, int page, int size, String sort) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Sort sortObj;
        if ("date".equalsIgnoreCase(sortField)) {
            sortObj = Sort.by(sortDirection, "date").and(Sort.by(sortDirection, "time"));
        } else {
            sortObj = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "time"));
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<AppointmentRequest> requests = requestRepository.findByDoctor(doctor, pageable);
        return requests.map(AppointmentRequestDTO::fromEntity);
    }
}