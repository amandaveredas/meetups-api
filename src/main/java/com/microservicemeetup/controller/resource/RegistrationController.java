package com.microservicemeetup.controller.resource;

import com.microservicemeetup.controller.dto.registration.RegistrationDTORequest;
import com.microservicemeetup.controller.dto.registration.RegistrationDTORequestFilter;
import com.microservicemeetup.controller.dto.registration.RegistrationDTOResponse;
import com.microservicemeetup.exceptions.registration.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.service.registration.RegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/registration/v1")

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

    @GetMapping
    public Page<RegistrationDTOResponse> find(RegistrationDTORequestFilter dto, Pageable pageable) {
        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = service.find(filter, pageable);

        List<RegistrationDTOResponse> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationDTOResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<RegistrationDTOResponse>(list, pageable, result.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTOResponse getById(@PathVariable Long id) throws RegistrationNotFoundException {

        return service
                .getById(id)
                .map(registration -> modelMapper.map(registration,RegistrationDTOResponse.class))
                .orElseThrow(() -> new RegistrationNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById (@PathVariable Long id) throws RegistrationNotFoundException {
        service.getById(id);
        service.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTOResponse updateById (@PathVariable Long id, @RequestBody @Valid RegistrationDTORequest dtoRequest) throws EmailAlreadyExistsException {
        Registration entity = modelMapper.map(dtoRequest,Registration.class);
        entity = service.update(id,entity);

        return modelMapper.map(entity,RegistrationDTOResponse.class);
    }
}
