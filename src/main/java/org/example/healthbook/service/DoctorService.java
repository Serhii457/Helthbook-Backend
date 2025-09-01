package org.example.healthbook.service;

import org.example.healthbook.dto.DoctorCreateDTO;
import org.example.healthbook.dto.DoctorDTO;
import org.example.healthbook.dto.ScheduleDayDTO;
import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.Schedule;
import org.example.healthbook.model.Specialization;
import org.example.healthbook.model.User;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.SpecializationRepository;
import org.example.healthbook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         SpecializationRepository specializationRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.specializationRepository = specializationRepository;
    }

    @Transactional
    public Doctor createDoctorFromDTO(DoctorCreateDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено: " + dto.getUsername()));

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);

        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Спеціалізацію не знайдено"));

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(specialization);
        doctor.setPhotoUrl(dto.getPhotoUrl());

        return doctorRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = getDoctorByIdEntity(id);
        return convertToDTO(doctor);
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = getDoctorByIdEntity(id);

        User user = doctor.getUser();
        if (user != null) {
            user.setFullName(doctorDetails.getUser() != null ? doctorDetails.getUser().getFullName() : user.getFullName());
            user.setPhone(doctorDetails.getUser() != null ? doctorDetails.getUser().getPhone() : user.getPhone());
            userRepository.save(user);
        }

        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setPhotoUrl(doctorDetails.getPhotoUrl());
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DoctorDTO findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return null;

        Optional<Doctor> doctorOpt = doctorRepository.findByUser(userOpt.get());
        return doctorOpt.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public void updateByUsername(String username, DoctorDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Доктора не знайдено"));

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);

        doctor.setPhotoUrl(dto.getPhotoUrl());
        doctorRepository.save(doctor);
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getId());

        if (doctor.getUser() != null) {
            doctorDTO.setFullName(doctor.getUser().getFullName());
            doctorDTO.setPhone(doctor.getUser().getPhone());
        } else {
            doctorDTO.setFullName(null);
            doctorDTO.setPhone(null);
        }

        doctorDTO.setSpecialization(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : null);
        doctorDTO.setPhotoUrl(doctor.getPhotoUrl());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Map<String, List<String>> grouped = doctor.getSchedule().stream()
                .collect(Collectors.groupingBy(
                        Schedule::getDayOfWeek,
                        Collectors.mapping(s -> s.getStartTime().format(timeFormatter), Collectors.toList())
                ));

        List<ScheduleDayDTO> schedule = grouped.entrySet().stream().map(entry -> {
            ScheduleDayDTO scheduleDayDTO = new ScheduleDayDTO();
            scheduleDayDTO.setDay(entry.getKey());
            scheduleDayDTO.setTimes(entry.getValue());
            return scheduleDayDTO;
        }).collect(Collectors.toList());

        doctorDTO.setSchedule(schedule);

        return doctorDTO;
    }

    private Doctor getDoctorByIdEntity(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Доктор не знайдено"));
    }
}
