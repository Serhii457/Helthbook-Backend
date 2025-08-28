package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentDTO;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.AppointmentRepository;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.service.AppointmentRequestService;
import org.example.healthbook.service.AppointmentService;
import org.example.healthbook.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRequestService appointmentRequestService;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;

    @PostMapping("/public")
    public ResponseEntity<String> requestAppointment(@RequestBody AppointmentRequestDTO dto) {
        AppointmentRequest request = new AppointmentRequest();
        request.setFullName(dto.getFullName());
        request.setPhone(dto.getPhone());
        appointmentRequestService.createAppointmentRequest(dto);

        return ResponseEntity.ok("Дякуємо! Ми зателефонуємо вам найближчим часом.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/page")
    public ResponseEntity<Page<AppointmentDTO>> getAllAppointmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(
                appointmentService.getAllAppointmentsPaged(page, size, sort, direction)
        );
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public Appointment create(@RequestBody Appointment appointment) {
        return appointmentService.save(appointment);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable Long id, @RequestBody Appointment appointment) {
        if (appointmentService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        appointment.setId(id);
        return ResponseEntity.ok(appointmentService.save(appointment));
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (appointmentService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        appointmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/mine")
    public List<AppointmentDTO> getMyAppointments(Authentication authentication) {
        String username = authentication.getName();
        return appointmentService.findByDoctorUsername(username);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my")
    public List<AppointmentDTO> getMyAppointments(Principal principal) {
        return appointmentService.getAppointmentsForDoctor(principal.getName());
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor")
    public ResponseEntity<Page<AppointmentDTO>> getDoctorAppointmentsPaged(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(
                appointmentService.findByDoctorUsernamePaged(principal.getName(), page, size, sort, direction)
        );
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @PostMapping("/appointments")
    public AppointmentDTO createAppointment(@RequestBody AppointmentDTO dto) {
        Patient patient = patientService.findOrCreatePatient(dto.getFullName(), dto.getPhone());

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Лікар не знайдений"));

        LocalDate date = LocalDate.parse(dto.getDate());
        LocalTime time = LocalTime.parse(dto.getTime());

        if (!appointmentService.isDoctorAvailable(doctor, date, time)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Цей час недоступний");
        }

        Appointment appointment = new Appointment();
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointment = appointmentRepository.save(appointment);

        dto.setId(appointment.getId());
        dto.setDoctorName(doctor.getUser().getFullName());
        dto.setPatientId(patient.getId());
        dto.setPatientName(patient.getFullName());
        dto.setStatus(appointment.getStatus().name());

        return dto;
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}/start")
    public ResponseEntity<AppointmentDTO> startAppointment(@PathVariable Long id) {
        Appointment updated = appointmentService.startAppointment(id);
        return ResponseEntity.ok(AppointmentDTO.fromEntity(updated));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentDTO> completeAppointment(@PathVariable Long id) {
        Appointment updated = appointmentService.updateStatus(id, AppointmentStatus.COMPLETED);
        return ResponseEntity.ok(AppointmentDTO.fromEntity(updated));
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        Appointment updated = appointmentService.updateStatus(id, AppointmentStatus.CANCELLED);
        return ResponseEntity.ok(AppointmentDTO.fromEntity(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<AppointmentDTO> approveAppointment(@PathVariable Long id) {
        Appointment updated = appointmentService.updateStatus(id, AppointmentStatus.SCHEDULED);
        return ResponseEntity.ok(AppointmentDTO.fromEntity(updated));
    }

}