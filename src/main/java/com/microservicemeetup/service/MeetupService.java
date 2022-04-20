package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {
    Meetup save(Meetup meetup);


}
