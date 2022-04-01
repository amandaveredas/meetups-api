package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;
import com.microservicemeetup.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service

public class RegistrationServiceImpl implements RegistrationService{

    private RegistrationRepository repository;
    private String firstRegistration = "001";

    public RegistrationServiceImpl(RegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Registration save(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException {

        if(repository.existsByEmail(registrationDTORequest.getEmail())){
            throw new EmailAlreadyExistsException();
        }

        Registration registration = Registration.builder()
                .name(registrationDTORequest.getName())
                .email(registrationDTORequest.getEmail())
                .registrationVersion(firstRegistration)
                .dateOfRegistration(LocalDate.now())
                .build();

        return repository.save(registration);
    }
}
