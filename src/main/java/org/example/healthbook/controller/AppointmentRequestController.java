package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.AppointmentRequestDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.*;
import org.example.healthbook.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
//@RequestMapping("/api/requests")
@RequestMapping("/api/appointment-requests") //for AdminPatientsPage
@RequiredArgsConstructor
public class AppointmentRequestController {

    private final AppointmentRequestRepository requestRepository;
    //for AdminPatientsPage
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRequest(@PathVariable Long id) {
        requestRepository.deleteById(id);
    }

    //добавляем для работы AdminPatientsPage
//    @PostMapping
//    public ResponseEntity<?> createRequest(@RequestBody AppointmentRequestDTO dto) {
//        System.out.println("📨 Надійшла заявка: " + dto);
//        try {
//            // 1. Знайти або створити User
////            User user = userRepository.findByPhone(dto.getPhone())
////                    .orElseGet(() -> {
////                        User newUser = new User();
////                        newUser.setUsername("anon_" + UUID.randomUUID()); // згенерований username
////                        newUser.setPassword(passwordEncoder.encode("patient123"));
////                        newUser.setFullName(dto.getFullName());
////                        newUser.setPhone(dto.getPhone());
////                        newUser.setRoles(Set.of(roleRepository.findById("ROLE_PATIENT").orElseThrow()));
////                        return userRepository.save(newUser);
////                    });
////
////            // 2. Знайти або створити Patient
////            Patient patient = patientRepository.findByUser(user)
////                    .orElseGet(() -> {
////                        Patient newPatient = new Patient();
////                        newPatient.setUser(user);
////                        newPatient.setFullName(dto.getFullName());
////                        newPatient.setPhone(dto.getPhone());
////                        return patientRepository.save(newPatient);
////                    });
//
//            Patient patient = patientService.findOrCreatePatient(dto.getFullName(), dto.getPhone());
//
//
//            // 3. Створити заявку
//            AppointmentRequest request = new AppointmentRequest();
//            request.setPatient(patient);
//            request.setDoctor(doctorRepository.findById(dto.getDoctorId())
//                    .orElseThrow(() -> new RuntimeException("Доктора не знайдено")));
//            request.setDate(LocalDate.parse(dto.getDate()));
//            request.setTime(LocalTime.parse(dto.getTime()));
//            request.setNote(dto.getNote());
//            request.setStatus(AppointmentRequestStatus.PENDING);
//
//            requestRepository.save(request);
//
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

@PostMapping
public ResponseEntity<?> createRequest(@RequestBody AppointmentRequestDTO dto) {
    System.out.println("📨 Надійшла заявка: " + dto);
    try {
        // 1. Знайти або створити пацієнта
        Patient patient = patientService.findOrCreatePatient(dto.getFullName(), dto.getPhone());

        // 2. Знайти доктора
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Доктора не знайдено"));

        // 3. Створити заявку
        AppointmentRequest request = new AppointmentRequest();
        request.setPatient(patient);
        request.setDoctor(doctor);
        request.setDate(LocalDate.parse(dto.getDate()));
        request.setTime(LocalTime.parse(dto.getTime()));
        request.setNote(dto.getNote());
        request.setStatus(AppointmentRequestStatus.PENDING);
        request.setFullName(dto.getFullName()); // ⬅️ додано
        request.setPhone(dto.getPhone());       // ⬅️ додано

        // 📋 Логи перед збереженням
        System.out.println("📌 Створюється заявка:");
        System.out.println("Пацієнт id: " + patient.getId());
        System.out.println("Доктор id: " + doctor.getId());
        System.out.println("Дата: " + request.getDate());
        System.out.println("Час: " + request.getTime());
        System.out.println("Примітка: " + request.getNote());
        System.out.println("📌 Повна заявка: " + request);

        // 4. Зберегти заявку
        requestRepository.save(request);

        return ResponseEntity.ok().build();
    } catch (Exception e) {
        System.err.println("❌ Помилка при створенні заявки:");
        e.printStackTrace(); // ⬅️ обов'язково показати повний стек трейс
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


}