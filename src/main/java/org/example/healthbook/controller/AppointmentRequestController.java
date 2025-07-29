package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.*;
import org.example.healthbook.service.AppointmentRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
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

@GetMapping("/page")
@PreAuthorize("hasRole('ADMIN')")
public Page<AppointmentRequestDTO> getPaged(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(required = false, defaultValue = "date,desc") String sort) {

    String[] sortParams = sort.split(",");
    String sortField = sortParams[0];
    Sort.Direction sortDirection = Sort.Direction.ASC;
    if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
        sortDirection = Sort.Direction.DESC;
    }

    Sort sortObj;
    if ("fullName".equalsIgnoreCase(sortField)) {
        sortObj = Sort.by(sortDirection, "fullName");
    } else if ("date".equalsIgnoreCase(sortField)) {
        sortObj = Sort.by(sortDirection, "date").and(Sort.by(sortDirection, "time"));
    } else {
        sortObj = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "time"));
    }

    Pageable pageable = PageRequest.of(page, size, sortObj);
    return requestRepository.findAll(pageable).map(AppointmentRequestDTO::fromEntity);
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

    @GetMapping("/page/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public Page<AppointmentRequestDTO> getDoctorRequestsPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "date,desc") String sort,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        return appointmentRequestService.getRequestsForDoctor(username, page, size, sort);
    }

}