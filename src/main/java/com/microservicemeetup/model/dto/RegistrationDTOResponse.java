package com.microservicemeetup.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTOResponse {
    private Long id;

    private String registrationVersion;

    private String name;

    private LocalDate dateOfRegistration;

    private String email;
}
