package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.PatientDTO;
import org.example.healthbook.model.Patient;
import org.example.healthbook.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients()
                .stream()
                .map(p -> new PatientDTO(
                        p.getId(),
                        p.getFullName(),
                        p.getPhone(),
                        p.getUser() != null ? p.getUser().getId() : null
                ))
                .toList();
        return ResponseEntity.ok(patients);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public PatientDTO getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

@GetMapping("/page")
public Page<PatientDTO> getPatientsPaged(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(required = false, defaultValue = "fullName,asc") String sort) {

    String[] sortParams = sort.split(",");
    String sortField = sortParams[0];
    Sort.Direction sortDirection = Sort.Direction.ASC;
    if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
        sortDirection = Sort.Direction.DESC;
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

    return patientService.getPatientsPaged(pageable)
            .map(PatientDTO::fromEntity);
}


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientService.updatePatient(id, patientDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }
}