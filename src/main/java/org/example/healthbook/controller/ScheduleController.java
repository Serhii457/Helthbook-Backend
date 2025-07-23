package org.example.healthbook.controller;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.ScheduleDTO;
import org.example.healthbook.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @GetMapping
    public List<ScheduleDTO> getAll() {
        return scheduleService.getAllSchedules();
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getById(@PathVariable Long id) {
        return scheduleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @PostMapping
    public ScheduleDTO create(@RequestBody ScheduleDTO dto) {
        return scheduleService.addSlot(dto);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> update(@PathVariable Long id, @RequestBody ScheduleDTO dto) {
        if (scheduleService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        dto.setId(id);
        return ResponseEntity.ok(scheduleService.addSlot(dto));
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (scheduleService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        scheduleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors/{doctorId}/schedule")
    public List<ScheduleDTO> getDoctorSchedule(@PathVariable Long doctorId) {
        return scheduleService.getScheduleForDoctor(doctorId);
    }
}
