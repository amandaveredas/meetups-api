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
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String event;

    @JoinColumn(name = "registration_id")
    @ManyToOne
    private Registration registration;

    @Column
    private LocalDate meetupDate;

    @Column
    private Boolean registered;

}
