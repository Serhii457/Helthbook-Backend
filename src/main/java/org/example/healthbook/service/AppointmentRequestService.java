package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.model.AppointmentRequest;
import org.example.healthbook.repository.AppointmentRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentRequestService {

    private final AppointmentRequestRepository repository;

    public List<AppointmentRequest> getAll() {
        return repository.findAll();
    }

    public void save(AppointmentRequest request) {
        repository.save(request);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}