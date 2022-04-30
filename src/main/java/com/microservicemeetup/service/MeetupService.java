package com.microservicemeetup.service;

import com.microservicemeetup.model.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Page<Meetup> find(Meetup filter, Pageable pageable);

    Optional<Meetup> getById(Long id);

    void delete(Long id);

    Meetup update(Long eq, Meetup any);


}
