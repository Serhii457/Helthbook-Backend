package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.DoctorCreateDTO;
import org.example.healthbook.dto.DoctorDTO;
import org.example.healthbook.model.Doctor;
import org.example.healthbook.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // ADMIN CRUD
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<DoctorDTO> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public DoctorDTO getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Doctor createDoctor(@RequestBody DoctorCreateDTO dto) {
        return doctorService.createDoctorFromDTO(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorService.updateDoctor(id, doctorDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }

    // Публічний ендпоінт
    @GetMapping("/public")
    public List<DoctorDTO> getAllDoctorsPublic() {
        return doctorService.getAllDoctors();
    }

    // Для лікаря: профіль
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my-profile")
    public ResponseEntity<DoctorDTO> getMyProfile(Authentication auth) {
        String username = auth.getName();
        DoctorDTO doctor = doctorService.findByUsername(username);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctor);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/my-profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody DoctorDTO dto, Authentication auth) {
        String username = auth.getName();
        try {
            doctorService.updateByUsername(username, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
