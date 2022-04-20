package com.microservicemeetup.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTORequestFilter {
    private Long id;

    private String registrationAttribute;

    private String name;

    private LocalDate dateOfRegistration;

    private String email;
}
