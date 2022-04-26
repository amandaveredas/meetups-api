package com.microservicemeetup.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTORequest {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String registrationAttribute;

}
