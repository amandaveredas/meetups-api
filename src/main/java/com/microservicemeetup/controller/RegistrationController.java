package com.microservicemeetup.controller;

import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;
import com.microservicemeetup.model.dto.RegistrationDTOResponse;
import com.microservicemeetup.service.RegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/registration")

public class RegistrationController {

    private RegistrationService service;
    private ModelMapper modelMapper;


    public RegistrationController(RegistrationService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationDTOResponse create(@RequestBody @Valid RegistrationDTORequest dtoRequest) throws EmailAlreadyExistsException {

        Registration entity = modelMapper.map(dtoRequest,Registration.class);
        entity = service.save(entity);

        return modelMapper.map(entity,RegistrationDTOResponse.class);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTOResponse getById(@PathVariable Long id) throws RegistrationNotFoundException {

        return service
                .getById(id)
                .map(registration -> modelMapper.map(registration,RegistrationDTOResponse.class))
                .orElseThrow(() -> new RegistrationNotFoundException("Não foi possível encontrar o registro com o id informado."));
    }
}
