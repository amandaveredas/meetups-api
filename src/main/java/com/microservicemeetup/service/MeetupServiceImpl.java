package com.microservicemeetup.service;

import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.MeetupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Registration> registrations = new ArrayList<>(registrationService.getByRegistrationAttribute(meetup.getRegistrationAttribute()));
        if(meetup.getRegistrations() != null)
            registrations.addAll(meetup.getRegistrations());
        registrations = registrations.stream().distinct().collect(Collectors.toList());
        meetup.setRegistrations(registrations);

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
