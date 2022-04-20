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
public class RegistrationDTOResponse {
    private Long id;

    private String registrationVersion;

    private String name;

    private String dateOfRegistration;

    private String email;
}
