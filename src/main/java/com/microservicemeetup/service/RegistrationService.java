package com.microservicemeetup.service;

import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationFoundButNotDeletedException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;

import java.util.Optional;

public interface RegistrationService {

    Registration save(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException;

    Optional<Registration> getById(Long id) throws RegistrationNotFoundException;

    void delete(Registration registration) throws RegistrationNotFoundException, RegistrationFoundButNotDeletedException;

    Registration update(Long id, RegistrationDTORequest registration) throws EmailAlreadyExistsException;
}
