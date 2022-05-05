package com.microservicemeetup.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data @EqualsAndHashCode(exclude = "registrations")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "meetup")
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String event;

    @Column
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime meetupDate;

    @Column(name = "registration_attribute")
    private String registrationAttribute;

    @ManyToMany
    @JoinTable(name="meetup_has_registrations", joinColumns=
            {@JoinColumn(name="meetup_id")}, inverseJoinColumns=
            {@JoinColumn(name="registration_id")})
    @JsonIgnoreProperties("meetups")
    private Set<Registration> registrations;



}
