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

    @NotBlank(message = "O campo name não pode estar em branco!")
    private String name;

    @NotBlank(message = "O campo email não pode estar em branco!")
    private String email;

    @NotBlank(message = "O campo registrationAttribute não pode estar em branco!")
    private String registrationAttribute;

}
