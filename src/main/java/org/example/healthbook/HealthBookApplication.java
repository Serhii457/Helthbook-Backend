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
    public CommandLineRunner dataInitializer(RoleRepository roleRepository,
                                             UserRepository userRepository,
                                             PasswordEncoder encoder) {
        return args -> {

            if (roleRepository.findById("ROLE_PATIENT").isEmpty()) {
                roleRepository.save(new Role("ROLE_PATIENT"));
            }
            if (roleRepository.findById("ROLE_DOCTOR").isEmpty()) {
                roleRepository.save(new Role("ROLE_DOCTOR"));
            }
            if (roleRepository.findById("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Role("ROLE_ADMIN"));
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setFullName("Адміністратор");
                admin.setPhone("+380000000001");
                admin.setRoles(Collections.singleton(roleRepository.findById("ROLE_ADMIN").get()));
                userRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner doctorInitializer(
            SpecializationRepository specializationRepository,
            DoctorRepository doctorRepository,
            ScheduleRepository scheduleRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder
    ) {
        return args -> {
            Role doctorRole = roleRepository.findById("ROLE_DOCTOR")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_DOCTOR")));

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

                record DoctorData(String fullName, String phone, Specialization specialization, String photoUrl) {}

                List<DoctorData> doctorsData = List.of(
                        new DoctorData("Іванна Петрова", "+380501111234", plastic, "/images/doctors/doc1.jpg"),
                        new DoctorData("Олена Іваненко", "+380502222458", endoskopia, "/images/doctors/doc2.jpg"),
                        new DoctorData("Андрій Шевченко", "+380503333123", proktologia, "/images/doctors/doc3.jpg"),
                        new DoctorData("Марія Сидоренко", "+380504444124", hirurgia, "/images/doctors/doc4.jpg"),
                        new DoctorData("Віктор Коваль", "+380505555452", sudinnaHir, "/images/doctors/doc5.jpg"),
                        new DoctorData("Оксана Дорош", "+380506666521", proktologia, "/images/doctors/doc6.jpeg"),
                        new DoctorData("Сергій Кравченко", "+380507777700", hirurgia, "/images/doctors/doc7.jpg"),
                        new DoctorData("Тетяна Лисенко", "+380508888800", uzd, "/images/doctors/doc8.jpg"),
                        new DoctorData("Юлія Козак", "+380509999900", proktologia, "/images/doctors/doc9.jpg"),
                        new DoctorData("Наталія Романюк", "+380501010107", plastic, "/images/doctors/doc10.jpeg"),
                        new DoctorData("Ольга Скрипка", "+380501234585", sudinnaHir, "/images/doctors/doc11.jpg"),
                        new DoctorData("Соломія Магура", "+380990077810", uzd, "/images/doctors/doc12.jpg")
                );

                for (int i = 0; i < doctorsData.size(); i++) {
                    DoctorData data = doctorsData.get(i);

                    String username = "doc" + (i + 1);
                    String rawPassword = "pass" + (i + 1);

                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(encoder.encode(rawPassword));
                    user.setFullName(data.fullName);
                    user.setPhone(data.phone);
                    user.setRoles(Collections.singleton(doctorRole));
                    userRepository.save(user);

                    Doctor doctor = new Doctor();
                    doctor.setUser(user);
                    doctor.setSpecialization(data.specialization);
                    doctor.setPhotoUrl(data.photoUrl);
                    doctorRepository.save(doctor);

                    scheduleRepository.saveAll(List.of(
                            new Schedule(doctor, "Понеділок", LocalTime.of(9, 0)),
                            new Schedule(doctor, "Понеділок", LocalTime.of(10, 0)),
                            new Schedule(doctor, "Понеділок", LocalTime.of(11, 0)),
                            new Schedule(doctor, "Понеділок", LocalTime.of(12, 0)),

                            new Schedule(doctor, "Вівторок", LocalTime.of(10, 0)),
                            new Schedule(doctor, "Вівторок", LocalTime.of(11, 0)),
                            new Schedule(doctor, "Вівторок", LocalTime.of(12, 0)),
                            new Schedule(doctor, "Вівторок", LocalTime.of(13, 0)),

                            new Schedule(doctor, "Середа", LocalTime.of(9, 0)),
                            new Schedule(doctor, "Середа", LocalTime.of(10, 0)),
                            new Schedule(doctor, "Середа", LocalTime.of(12, 0)),
                            new Schedule(doctor, "Середа", LocalTime.of(13, 0)),

                            new Schedule(doctor, "Четвер", LocalTime.of(9, 0)),
                            new Schedule(doctor, "Четвер", LocalTime.of(10, 0)),
                            new Schedule(doctor, "Четвер", LocalTime.of(11, 0)),
                            new Schedule(doctor, "Четвер", LocalTime.of(13, 0)),

                            new Schedule(doctor, "Пʼятниця", LocalTime.of(9, 30)),
                            new Schedule(doctor, "Пʼятниця", LocalTime.of(10, 30)),
                            new Schedule(doctor, "Пʼятниця", LocalTime.of(11, 30)),
                            new Schedule(doctor, "Пʼятниця", LocalTime.of(12, 30))
                    ));
                }
            }
        };
    }
}
