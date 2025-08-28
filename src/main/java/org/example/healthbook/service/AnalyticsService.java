package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentsByStatusDTO;
import org.example.healthbook.dto.AppointmentsPerDayDTO;
import org.example.healthbook.dto.TopDoctorDTO;
import org.example.healthbook.model.AppointmentStatus;
import org.example.healthbook.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AppointmentRepository appointmentRepository;

    public List<AppointmentsPerDayDTO> getAppointmentsPerDay(LocalDate from, LocalDate to) {
        List<Object[]> results = appointmentRepository.countAppointmentsPerDay(from, to);

        return results.stream()
                .map(r -> new AppointmentsPerDayDTO((LocalDate) r[0], (Long) r[1]))
                .collect(Collectors.toList());
    }

    public List<AppointmentsByStatusDTO> getAppointmentsByStatus(LocalDate from, LocalDate to) {
        List<Object[]> results = appointmentRepository.countAppointmentsByStatus(from, to);

        Map<AppointmentStatus, Long> map = results.stream()
                .collect(Collectors.toMap(
                        r -> (AppointmentStatus) r[0],
                        r -> (Long) r[1]
                ));

        return Arrays.stream(AppointmentStatus.values())
                .map(s -> new AppointmentsByStatusDTO(
                        s.name(),
                        map.getOrDefault(s, 0L)
                ))
                .collect(Collectors.toList());
    }

    public List<TopDoctorDTO> getTopDoctors(LocalDate from, LocalDate to) {
        List<Object[]> results = appointmentRepository.findTopDoctorsByAppointments(from, to);

        return results.stream()
                .map(r -> new TopDoctorDTO((String) r[0], (Long) r[1]))
                .collect(Collectors.toList());
    }
}