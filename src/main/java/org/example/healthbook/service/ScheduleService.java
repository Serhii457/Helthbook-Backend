package org.example.healthbook.service;

import org.example.healthbook.dto.ScheduleDTO;
import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.Schedule;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           DoctorRepository doctorRepository){
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ScheduleDTO> findById(Long id) {
        return scheduleRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ScheduleDTO addSlot(ScheduleDTO dto) {
        LocalTime time = LocalTime.parse(dto.getStartTime());

        boolean exists = scheduleRepository.existsByDoctorIdAndDayOfWeekAndStartTime(
                dto.getDoctorId(), dto.getDayOfWeek(), time);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Слот уже існує");
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(time);

        return convertToDTO(scheduleRepository.save(schedule));
    }

    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    public ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDoctorId(schedule.getDoctor().getId());
        dto.setDoctorName(schedule.getDoctor().getUser() != null ? schedule.getDoctor().getUser().getFullName() : null);
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime().toString());
        return dto;
    }

    public List<ScheduleDTO> getScheduleForDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
