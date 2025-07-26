package org.example.healthbook.controller;

import org.example.healthbook.dto.SpecializationDTO;
import org.example.healthbook.model.Specialization;
import org.example.healthbook.repository.SpecializationRepository;
import org.example.healthbook.service.SpecializationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;
    private final SpecializationRepository specializationRepository;

    public SpecializationController(SpecializationService specializationService,
                                    SpecializationRepository specializationRepository) {
        this.specializationService = specializationService;
        this.specializationRepository = specializationRepository;
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

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<SpecializationDTO> getPaged(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return specializationRepository.findAll(pageable)
                .map(SpecializationDTO::fromEntity);
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