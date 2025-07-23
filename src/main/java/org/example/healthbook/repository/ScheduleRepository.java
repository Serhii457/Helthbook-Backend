package org.example.healthbook.repository;

import org.example.healthbook.model.Doctor;
import org.example.healthbook.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDoctorId(Long doctorId);
    List<Schedule> findByDoctorAndDayOfWeek(Doctor doctor, String dayOfWeek);
    boolean existsByDoctorIdAndDayOfWeekAndStartTime(Long doctorId, String dayOfWeek, LocalTime startTime);
}
