package com.microservicemeetup.service;

import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface RegistrationService {

    Registration save(Registration registration) throws EmailAlreadyExistsException;

    Optional<Registration> getById(Long id) throws RegistrationNotFoundException;

    void delete(Long id) throws RegistrationNotFoundException;

    Registration update(Long id, Registration registration) throws EmailAlreadyExistsException;

    Page<Registration> find(Registration filter, PageRequest pageRequest);

    Optional<Registration> getRegistrationByRegistrationVersion(String registrationVersion);
}
