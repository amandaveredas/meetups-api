package com.microservicemeetup.service.registration;

import com.microservicemeetup.exceptions.registration.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
