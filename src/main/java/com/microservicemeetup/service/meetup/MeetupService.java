package com.microservicemeetup.service.meetup;

import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup) throws RegistrationNotFoundException;

    Page<Meetup> find(Meetup filter, Pageable pageable);

    Optional<Meetup> getById(Long id);

    void delete(Long id);

    Meetup update(Long eq, Meetup any) throws RegistrationNotFoundException;


}
