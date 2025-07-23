package org.example.healthbook.service;

import org.example.healthbook.dto.PatientDTO;
import org.example.healthbook.model.Patient;
import org.example.healthbook.model.User;
import org.example.healthbook.repository.AppointmentRequestRepository;
import org.example.healthbook.repository.PatientRepository;
import org.example.healthbook.repository.RoleRepository;
import org.example.healthbook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRequestRepository appointmentRequestRepository;

    public PatientService(PatientRepository patientRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          AppointmentRequestRepository appointmentRequestRepository){
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRequestRepository = appointmentRequestRepository;
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));
        return convertToDTO(patient);
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = getPatientByIdEntity(id);
        patient.setFullName(patientDetails.getFullName());
        patient.setPhone(patientDetails.getPhone());
        patient.setUser(patientDetails.getUser());
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id) {
        appointmentRequestRepository.deleteAllByPatientId(id);
        patientRepository.deleteById(id);
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFullName(patient.getFullName());
        dto.setPhone(patient.getPhone());
        dto.setUserId(patient.getUser() != null ? patient.getUser().getId() : null);
        return dto;
    }

    private Patient getPatientByIdEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));
    }

    @Transactional
    public Patient findOrCreatePatient(String fullName, String phone) {
        User user = userRepository.findByPhone(phone).orElse(null);

        if (user == null) {
            System.out.println("Користувача не знайдено. Створюємо нового...");
            user = new User();
            user.setUsername("anon_" + UUID.randomUUID());
            user.setPassword(passwordEncoder.encode("patient123"));
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRoles(Set.of(roleRepository.findById("ROLE_PATIENT")
                    .orElseThrow(() -> new RuntimeException("Роль ROLE_PATIENT не знайдена"))));
            user = userRepository.save(user);
        } else {
            System.out.println("Користувача знайдено: user.id = " + user.getId());
        }

        Patient patient = patientRepository.findByUser(user).orElse(null);

        if (patient == null) {
            System.out.println("Пацієнта не знайдено. Створюємо нового...");
            patient = new Patient();
            patient.setUser(user);
            patient.setFullName(fullName);
            patient.setPhone(phone);
            patient = patientRepository.save(patient);
            patientRepository.flush();
        } else {
            System.out.println("Пацієнта знайдено: patient.id = " + patient.getId());
        }

        return patient;
    }

}