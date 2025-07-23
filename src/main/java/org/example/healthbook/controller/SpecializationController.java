package org.example.healthbook.controller;

import org.example.healthbook.dto.SpecializationDTO;
import org.example.healthbook.model.Specialization;
import org.example.healthbook.service.SpecializationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<SpecializationDTO> getAll() {
        return specializationService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationDTO> getById(@PathVariable Long id) {
        return specializationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Specialization create(@RequestBody Specialization specialization) {
        return specializationService.save(specialization);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Specialization> update(@PathVariable Long id, @RequestBody Specialization specialization) {
        if (specializationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        specialization.setId(id);
        return ResponseEntity.ok(specializationService.save(specialization));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (specializationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        specializationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}