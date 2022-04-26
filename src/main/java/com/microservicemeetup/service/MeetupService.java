package com.microservicemeetup.service;

import com.microservicemeetup.model.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Page<Meetup> find(Meetup filter, Pageable pageable);
}
