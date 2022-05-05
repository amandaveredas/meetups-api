package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RegistrationService {

    Registration save(Registration registration) throws EmailAlreadyExistsException;

    Optional<Registration> getById(Long id) throws RegistrationNotFoundException;

    void delete(Long id) throws RegistrationNotFoundException;

    Registration update(Long id, Registration registration) throws EmailAlreadyExistsException;

    Page<Registration> find(Registration filter, Pageable pageable);

    Set<Registration> getByRegistrationAttribute(String registrationAttribute);

}
