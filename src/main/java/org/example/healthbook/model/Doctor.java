package org.example.healthbook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

//@Data
//@NoArgsConstructor
//@Entity
//@Table(name = "doctors")
//public class Doctor {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String fullName;
//
//    @ManyToOne
//    @ToString.Exclude
//    @JoinColumn(name = "specialization_id")
//    private Specialization specialization;
//
//    private String phone;
//
//    @Column(name = "photo_url")
//    private String photoUrl;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @ToString.Exclude
//    @JoinColumn(name = "user_id", unique = true)
//    private User user;
//
//    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
//    private List<Schedule> schedule;
//
//    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
//    private List<Appointment> appointments;
//
//    public Doctor(String fullName, String phone, Specialization specialization, User user, String photoUrl) {
//        this.fullName = fullName;
//        this.phone = phone;
//        this.specialization = specialization;
//        this.user = user;
//        this.photoUrl = photoUrl;
//    }
//}
@Data
@NoArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Schedule> schedule;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Appointment> appointments;

    public Doctor(Specialization specialization, User user, String photoUrl) {
        this.specialization = specialization;
        this.user = user;
        this.photoUrl = photoUrl;
    }
}
