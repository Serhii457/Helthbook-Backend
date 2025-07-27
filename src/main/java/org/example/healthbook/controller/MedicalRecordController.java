package org.example.healthbook.controller;

import org.example.healthbook.dto.MedicalRecordCreateDTO;
import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.service.MedicalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/page")
    public ResponseEntity<Page<MedicalRecordDTO>> getPaged(
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(medicalRecordService.getPaged(pageable));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my")
    public ResponseEntity<List<MedicalRecordDTO>> getMyRecords() {
        return ResponseEntity.ok(medicalRecordService.getRecordsForCurrentDoctor());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> getById(@PathVariable Long id) {
        return medicalRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping
    public ResponseEntity<MedicalRecordDTO> create(@RequestBody MedicalRecordCreateDTO dto) {
        return ResponseEntity.ok(medicalRecordService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> update(@PathVariable Long id, @RequestBody MedicalRecordCreateDTO dto) {
        return ResponseEntity.ok(medicalRecordService.update(dto, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (medicalRecordService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        medicalRecordService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
