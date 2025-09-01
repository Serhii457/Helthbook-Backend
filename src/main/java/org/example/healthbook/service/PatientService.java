package org.example.healthbook.service;

import org.example.healthbook.dto.PatientDTO;
import org.example.healthbook.model.Patient;
import org.example.healthbook.model.Role;
import org.example.healthbook.model.User;
import org.example.healthbook.repository.PatientRepository;
import org.example.healthbook.repository.RoleRepository;
import org.example.healthbook.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public PatientService(PatientRepository patientRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository)
    {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));
        return convertToDTO(patient);
    }

    @Transactional
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
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Пацієнт не знайдений"));
        patientRepository.delete(patient);
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
        Optional<Patient> existingPatient = patientRepository.findByPhone(phone);
        if (existingPatient.isPresent()) {
            return existingPatient.get();
        }

        Role role = roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new RuntimeException("Роль не знайдена"));
        Optional<User> existingUser = userRepository.findByPhone(phone);
        User user = existingUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setUsername(UUID.randomUUID().toString());
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setRoles(Set.of(role));
            return userRepository.save(newUser);
        });

        Patient patient = new Patient();
        patient.setFullName(fullName);
        patient.setPhone(phone);
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Page<Patient> getPatientsPaged(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }
}