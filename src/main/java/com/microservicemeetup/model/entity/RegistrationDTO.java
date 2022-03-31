package com.microservicemeetup.model.entity;

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
public class RegistrationDTO {

    private Long id;

    @NotBlank
    private String registration;

    @NotBlank
    private String name;

    @NotEmpty
    private LocalDate dateOfRegistration;

    @NotBlank
    private String email;
}
