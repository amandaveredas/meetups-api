package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.RegistrationNotFoundById;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;

import java.util.Optional;

public interface RegistrationService {

    Registration save(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException;

    Optional<Registration> getById(Long id) throws RegistrationNotFoundById;
}
