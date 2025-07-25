package org.example.healthbook;

import org.example.healthbook.model.*;
import org.example.healthbook.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class HealthBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthBookApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {

            // Ролі
            if (roleRepository.findById("PATIENT").isEmpty()) {
                roleRepository.save(new Role("PATIENT"));
            }
            if (roleRepository.findById("DOCTOR").isEmpty()) {
                roleRepository.save(new Role("DOCTOR"));
            }
            if (roleRepository.findById("ADMIN").isEmpty()) {
                roleRepository.save(new Role("ADMIN"));
            }

            // Адмін
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRoles(Collections.singleton(roleRepository.findById("ADMIN").get()));
                userRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner doctorInitializer(
            SpecializationRepository specializationRepository,
            DoctorRepository doctorRepository,
            ScheduleRepository scheduleRepository
    ) {
        return args -> {

            Specialization endoskopia = specializationRepository.findByName("Ендоскопія")
                    .orElseGet(() -> specializationRepository.save(new Specialization("Ендоскопія")));
            Specialization hirurgia = specializationRepository.findByName("Хірургія")
                    .orElseGet(() -> specializationRepository.save(new Specialization("Хірургія")));
            Specialization proktologia = specializationRepository.findByName("Проктологія")
                    .orElseGet(() -> specializationRepository.save(new Specialization("Проктологія")));
            Specialization uzd = specializationRepository.findByName("УЗД")
                    .orElseGet(() -> specializationRepository.save(new Specialization("УЗД")));
            Specialization sudinnaHir = specializationRepository.findByName("Судинна хірургія")
                    .orElseGet(() -> specializationRepository.save(new Specialization("Судинна хірургія")));
            Specialization plastic = specializationRepository.findByName("Пластична хірургія")
                    .orElseGet(() -> specializationRepository.save(new Specialization("Пластична хірургія")));

            if (doctorRepository.count() == 0) {
                List<Doctor> doctors = List.of(
                        new Doctor("Іванна Петрова", "+380501111111", plastic, null, "/images/doctors/doc1.jpg"),
                        new Doctor("Олена Іваненко", "+380502222222", endoskopia, null, "/images/doctors/doc2.jpg"),
                        new Doctor("Андрій Шевченко", "+380503333333", proktologia, null, "/images/doctors/doc3.jpg"),
                        new Doctor("Марія Сидоренко", "+380504444444", hirurgia, null, "/images/doctors/doc4.jpg"),
                        new Doctor("Віктор Коваль", "+380505555555", sudinnaHir, null, "/images/doctors/doc5.jpg"),
                        new Doctor("Оксана Дорош", "+380506666666", proktologia, null, "/images/doctors/doc6.jpeg"),
                        new Doctor("Сергій Кравченко", "+380507777777", hirurgia, null, "/images/doctors/doc7.jpg"),
                        new Doctor("Тетяна Лисенко", "+380508888888", uzd, null, "/images/doctors/doc8.jpg"),
                        new Doctor("Юлія Козак", "+380509999999", proktologia, null, "/images/doctors/doc9.jpg"),
                        new Doctor("Наталія Романюк", "+380501010101", plastic, null, "/images/doctors/doc10.jpeg"),
                        new Doctor("Ольга Скрипка", "+380501234567", sudinnaHir, null, "/images/doctors/doc11.jpg"),
                        new Doctor("Соломія Магура", "+380990077889", uzd, null, "/images/doctors/doc12.jpg")
                );

                doctorRepository.saveAll(doctors);

                for (Doctor doc : doctors) {
                    scheduleRepository.saveAll(List.of(

                            new Schedule(doc, "Понеділок", LocalTime.of(9, 0)),
                            new Schedule(doc, "Понеділок", LocalTime.of(10, 0)),
                            new Schedule(doc, "Понеділок", LocalTime.of(11, 0)),
                            new Schedule(doc, "Понеділок", LocalTime.of(12, 0)),

                            new Schedule(doc, "Вівторок", LocalTime.of(10, 0)),
                            new Schedule(doc, "Вівторок", LocalTime.of(11, 0)),
                            new Schedule(doc, "Вівторок", LocalTime.of(12, 0)),
                            new Schedule(doc, "Вівторок", LocalTime.of(13, 0)),

                            new Schedule(doc, "Середа", LocalTime.of(9, 0)),
                            new Schedule(doc, "Середа", LocalTime.of(10, 0)),
                            new Schedule(doc, "Середа", LocalTime.of(12, 0)),
                            new Schedule(doc, "Середа", LocalTime.of(13, 0)),

                            new Schedule(doc, "Четвер", LocalTime.of(9, 0)),
                            new Schedule(doc, "Четвер", LocalTime.of(10, 0)),
                            new Schedule(doc, "Четвер", LocalTime.of(11, 0)),
                            new Schedule(doc, "Четвер", LocalTime.of(13, 0)),

                            new Schedule(doc, "Пʼятниця", LocalTime.of(9, 30)),
                            new Schedule(doc, "Пʼятниця", LocalTime.of(10, 30)),
                            new Schedule(doc, "Пʼятниця", LocalTime.of(11, 30)),
                            new Schedule(doc, "Пʼятниця", LocalTime.of(12, 30))
                    ));
                }
            }
        };
    }
}
