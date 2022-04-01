package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;

public interface RegistrationService {

    Registration save(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException;
}
