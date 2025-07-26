package org.example.healthbook.controller;

import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.model.MedicalRecord;
import org.example.healthbook.repository.MedicalRecordRepository;
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
    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordController(MedicalRecordService medicalRecordService,
                                   MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordService = medicalRecordService;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @GetMapping
    public List<MedicalRecordDTO> getAll() {
        return medicalRecordService.getAll();
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> getById(@PathVariable Long id) {
        return medicalRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<MedicalRecordDTO> getPaged(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return medicalRecordRepository.findAll(pageable)
                .map(MedicalRecordDTO::fromEntity);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @PostMapping
    public MedicalRecord create(@RequestBody MedicalRecord record) {
        return medicalRecordService.save(record);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> update(@PathVariable Long id, @RequestBody MedicalRecord record) {
        if (medicalRecordService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        record.setId(id);
        return ResponseEntity.ok(medicalRecordService.save(record));
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (medicalRecordService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        medicalRecordService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}