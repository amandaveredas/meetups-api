package com.microservicemeetup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Registration {

    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_version")
    private String registrationVersion;

    @Column(name = "user_name")
    private String name;

    @Column(name = "date_of_registration")
    private LocalDate dateOfRegistration;

    @Column(name = "user_email")
    private String email;

}
