package com.microservicemeetup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data @EqualsAndHashCode(exclude = "meetups")
@Table(name = "registration")
public class Registration {

    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_attribute")
    private String registrationAttribute;

    @Column(name = "user_name")
    private String name;

    @Column(name = "date_of_registration")
    private LocalDate dateOfRegistration;

    @Column(name = "user_email")
    private String email;

    @ManyToMany(mappedBy = "registrations")
    @JsonIgnoreProperties("registrations")
    private Set<Meetup> meetups;

}
