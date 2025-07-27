package org.example.healthbook.service;

import org.example.healthbook.dto.MedicalRecordCreateDTO;
import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                UserRepository userRepository,
                                DoctorRepository doctorRepository,
                                PatientRepository patientRepository,
                                AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Page<MedicalRecordDTO> getPaged(Pageable pageable) {
        return medicalRecordRepository.findAll(pageable)
                .map(MedicalRecordDTO::fromEntity);
    }

    public Optional<MedicalRecordDTO> findById(Long id) {
        return medicalRecordRepository.findById(id)
                .map(MedicalRecordDTO::fromEntity);
    }

    public void deleteById(Long id) {
        medicalRecordRepository.deleteById(id);
    }

    // Получить записи текущего доктора
    public List<MedicalRecordDTO> getRecordsForCurrentDoctor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Найти User по username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувач не знайдений"));

        // Найти доктора, связанного с этим пользователем
        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Доктор не знайдений"));

        // Получить записи по доктору
        List<MedicalRecord> records = medicalRecordRepository.findAllByAppointmentDoctor(doctor);

        return records.stream()
                .map(MedicalRecordDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Создать запись по DTO
    public MedicalRecordDTO create(MedicalRecordCreateDTO dto) {
        MedicalRecord record = new MedicalRecord();
        return saveOrUpdateFromDTO(record, dto);
    }

    // Обновить запись по DTO
    public MedicalRecordDTO update(MedicalRecordCreateDTO dto, Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Запис не знайдений"));
        return saveOrUpdateFromDTO(record, dto);
    }

    // Общий метод для создания/обновления
    private MedicalRecordDTO saveOrUpdateFromDTO(MedicalRecord record, MedicalRecordCreateDTO dto) {

        record.setDiagnosis(dto.getDiagnosis());
        record.setRecommendations(dto.getRecommendations());
        record.setComment(dto.getComment());

        // Привязать Appointment
        if (dto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Прийом не знайдений"));
            record.setAppointment(appointment);
        } else {
            record.setAppointment(null);
        }

        // Привязать Patient
        if (dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Пацієнт не знайдений"));
            record.setPatient(patient);
        } else {
            record.setPatient(null);
        }

        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }

        MedicalRecord saved = medicalRecordRepository.save(record);
        return MedicalRecordDTO.fromEntity(saved);
    }
}
