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

    @NotBlank
    private String registrationVersion;

    @NotBlank
    private String name;

    @NotEmpty
    private LocalDate dateOfRegistration;

    @NotBlank
    private String email;
}
