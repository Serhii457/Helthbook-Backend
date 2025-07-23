package org.example.healthbook.service;

import org.example.healthbook.dto.SpecializationDTO;
import org.example.healthbook.model.Specialization;
import org.example.healthbook.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository){
        this.specializationRepository = specializationRepository;
    }

    public List<SpecializationDTO> getAll() {
        return specializationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<SpecializationDTO> findById(Long id) {
        return specializationRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public void deleteById(Long id) {
        specializationRepository.deleteById(id);
    }

    private SpecializationDTO convertToDTO(Specialization specialization) {
        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(specialization.getId());
        dto.setName(specialization.getName());
        return dto;
    }
}
