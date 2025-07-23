package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentDTO;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.AppointmentRepository;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.PatientRepository;
import org.example.healthbook.service.AppointmentRequestService;
import org.example.healthbook.service.AppointmentService;
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
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @PostMapping("/public")
    public ResponseEntity<String> requestAppointment(@RequestBody AppointmentRequestDTO dto) {
        AppointmentRequest request = new AppointmentRequest();
        request.setFullName(dto.getFullName());
        request.setPhone(dto.getPhone());
        appointmentRequestService.save(request);

        return ResponseEntity.ok("Дякуємо! Ми зателефонуємо вам найближчим часом.");
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @GetMapping
    public List<AppointmentDTO> getAll() {
        return appointmentService.getAllAppointments();
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

    @PostMapping("/appointments")
    public AppointmentDTO createAppointment(@RequestBody AppointmentDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Лікар не знайдений"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Пацієнт не знайдений"));

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

        appointmentRepository.save(appointment);
        return dto;
    }
}