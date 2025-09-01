package org.example.healthbook.service;

import org.example.healthbook.dto.AppointmentDTO;
import org.example.healthbook.model.Appointment;
import org.example.healthbook.model.AppointmentStatus;
import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.Schedule;
import org.example.healthbook.repository.AppointmentRepository;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsForDoctor(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AppointmentDTO> findById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> findByDoctorUsername(String username) {
        return appointmentRepository.findByDoctorUserUsername(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByDoctorUsernamePaged(String username, int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(sortField == null || sortField.isBlank() ? "date" : sortField);
        sort = "asc".equalsIgnoreCase(sortDirection) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Appointment> result = appointmentRepository.findByDoctorUserUsername(username, pageable);
        List<AppointmentDTO> mapped = result.getContent().stream().map(this::convertToDTO).toList();
        return new PageImpl<>(mapped, pageable, result.getTotalElements());
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getUser() != null ? appointment.getDoctor().getUser().getFullName() : null);
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getFullName());
        dto.setPhone(appointment.getPatient().getUser().getPhone());
        dto.setDate(appointment.getDate() != null ? appointment.getDate().format(DATE_FORMATTER) : null);
        dto.setTime(appointment.getTime() != null ? appointment.getTime().format(TIME_FORMATTER) : null);
        dto.setStatus(appointment.getStatus().name());
        return dto;
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment startAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only SCHEDULED appointment can be started");
        }
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS appointment can be completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAppointmentsPaged(int page, int size, String sortField, String sortDirection,
                                                     String status, String search) {
        Sort sort = Sort.by(sortField == null || sortField.isBlank() ? "date" : sortField);
        sort = "asc".equalsIgnoreCase(sortDirection) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        AppointmentStatus parsedStatus = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            parsedStatus = AppointmentStatus.valueOf(status);
        }

        Page<Appointment> result = appointmentRepository.findFilteredAppointments(parsedStatus, search, pageable);
        List<AppointmentDTO> mapped = result.getContent().stream().map(this::convertToDTO).toList();

        return new PageImpl<>(mapped, pageable, result.getTotalElements());
    }

}