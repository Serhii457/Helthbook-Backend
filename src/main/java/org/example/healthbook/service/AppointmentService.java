package org.example.healthbook.service;

import org.example.healthbook.dto.AppointmentDTO;
import org.example.healthbook.model.Appointment;
import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.Schedule;
import org.example.healthbook.repository.AppointmentRepository;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              ScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<AppointmentDTO> getAppointmentsForDoctor(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<AppointmentDTO> findById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<AppointmentDTO> findByDoctorUsername(String username) {
        return appointmentRepository.findByDoctorUserUsername(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getUser() != null ? appointment.getDoctor().getUser().getFullName() : null);
        dto.setPatientId(appointment.getPatient().getId());
        dto.setDate(appointment.getDate().toString());
        dto.setTime(appointment.getTime().toString());
        dto.setStatus(appointment.getStatus().name());
        return dto;
    }

    public boolean isDoctorAvailable(Doctor doctor, LocalDate date, LocalTime time) {
        String dayOfWeek = capitalize(date.getDayOfWeek().name().toLowerCase());

        List<Schedule> schedules = scheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
        boolean timeInSchedule = schedules.stream()
                .anyMatch(s -> s.getStartTime().equals(time));

        if (!timeInSchedule) return false;

        return appointmentRepository.findByDoctorAndDateAndTime(doctor, date, time).isEmpty();
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}