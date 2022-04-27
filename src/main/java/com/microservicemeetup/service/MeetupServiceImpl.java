package com.microservicemeetup.service;

import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.MeetupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetupServiceImpl implements MeetupService {

    private RegistrationService registrationService;
    private MeetupRepository repository;

    public MeetupServiceImpl(RegistrationService registrationService, MeetupRepository repository) {
        this.registrationService = registrationService;
        this.repository = repository;
    }

    @Override
    public Meetup save(Meetup meetup) {
       return repository.save(meetup);
    }

    @Override
    public Page<Meetup> find(Meetup filter, Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Meetup> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Meetup update(Long eq, Meetup any) {
        return null;
    }


}
