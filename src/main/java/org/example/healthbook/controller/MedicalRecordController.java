package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.MedicalRecordCreateDTO;
import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.service.MedicalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping
    public ResponseEntity<List<MedicalRecordDTO>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordDTO>> getRecordsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatientId(patientId));
    }

    @PostMapping
    public ResponseEntity<MedicalRecordDTO> createRecord(@RequestBody MedicalRecordCreateDTO dto,
                                                         Authentication authentication) {
        String doctorUsername = authentication.getName();
        MedicalRecordDTO created = medicalRecordService.createRecord(dto, doctorUsername);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> updateRecord(@PathVariable Long id,
                                                         @RequestBody MedicalRecordCreateDTO dto,
                                                         Authentication authentication) {
        String doctorUsername = authentication.getName();
        MedicalRecordDTO updated = medicalRecordService.updateRecord(id, dto, doctorUsername);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecordById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/all")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<MedicalRecordDTO>> getAllRecordsForDoctor(Authentication authentication) {
        String username = authentication.getName();
        List<MedicalRecordDTO> records = medicalRecordService.getAllRecordsForDoctor(username);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MedicalRecordDTO>> getAllRecordsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = Sort.Direction.fromString(sortParams.length > 1 ? sortParams[1] : "desc");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<MedicalRecordDTO> recordsPage = medicalRecordService.getAllRecordsForAdmin(pageRequest);

        return ResponseEntity.ok(recordsPage);
    }
}