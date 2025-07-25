package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.*;
import org.example.healthbook.service.AppointmentRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointment-requests")
@RequiredArgsConstructor
public class AppointmentRequestController {

    private final AppointmentRequestRepository requestRepository;
    private final AppointmentRequestService appointmentRequestService;

@GetMapping
@PreAuthorize("hasRole('ADMIN')")
public List<AppointmentRequestDTO> getAllRequests() {
    return requestRepository.findAll().stream()
            .map(AppointmentRequestDTO::fromEntity)
            .collect(Collectors.toList());
}
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void deleteRequest(@PathVariable Long id) {
    requestRepository.deleteById(id);
}

@PostMapping
public ResponseEntity<AppointmentRequest> createAppointmentRequest(@RequestBody AppointmentRequestDTO dto) {
    AppointmentRequest request = appointmentRequestService.createAppointmentRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }
}