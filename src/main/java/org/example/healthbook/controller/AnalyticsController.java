package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentsByStatusDTO;
import org.example.healthbook.dto.AppointmentsPerDayDTO;
import org.example.healthbook.dto.TopDoctorDTO;
import org.example.healthbook.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/appointments-per-day")
    public List<AppointmentsPerDayDTO> getAppointmentsPerDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return analyticsService.getAppointmentsPerDay(from, to);
    }

    @GetMapping("/appointments-by-status")
    public List<AppointmentsByStatusDTO> getAppointmentsByStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return analyticsService.getAppointmentsByStatus(from, to);
    }

    @GetMapping("/top-doctors")
    public List<TopDoctorDTO> getTopDoctors(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return analyticsService.getTopDoctors(from, to);
    }
}
