package com.microservicemeetup.service.meetup;

import com.microservicemeetup.exceptions.meetup.MeetupAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupNotFoundException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.MeetupRepository;
import com.microservicemeetup.service.registration.RegistrationService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
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
    public Meetup save(Meetup meetup) throws MeetupAlreadyExistsException, RegistrationNotFoundException {
        verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTime(meetup);
        Set<Registration> registrations = new LinkedHashSet<>(registrationService.getByRegistrationAttribute(meetup.getRegistrationAttribute()));

        if(meetup.getRegistrations() != null){
            for(Registration r: meetup.getRegistrations()){
                Optional<Registration> registration = registrationService.getById(r.getId());
                if(registration.isPresent()){
                    registrations.add(registration.get());
                }
            }
        }
        registrations = new LinkedHashSet<>(registrations.stream().distinct().collect(Collectors.toList())) ;
        meetup.setRegistrations(registrations);
      return repository.save(meetup);

    }

    @Override
    public Optional<Meetup> getById(Long id) {
        verifyNullId(id);
        if (repository.findById(id).isEmpty())
            throw new MeetupNotFoundException(id);
        return repository.findById(id);
    }


    @Override
    public Page<Meetup> find(Meetup filter, Pageable pageable) {
        Example<Meetup> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example,pageable);
    }

    @Override
    public void delete(Long id) {
        verifyNullId(id);
        Optional<Meetup> meetup = this.getById(id);
        repository.delete(meetup.orElseThrow(() -> new MeetupNotFoundException(id)));

    }

    @Override
    public Meetup update(Long id, Meetup meetup) throws RegistrationNotFoundException {
        Optional<Meetup> actualMeetup = repository.findById(id);

        if(actualMeetup.isEmpty()){
            return save(meetup);
        }

        verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTimeWhenTryToUpdate(id, meetup);

        Meetup updatedMeetup = Meetup.builder()
                .id(id)
                .event(meetup.getEvent())
                .meetupDate(meetup.getMeetupDate())
                .registrations(meetup.getRegistrations())
                .registrationAttribute(meetup.getRegistrationAttribute())
                .build();

        return repository.save(updatedMeetup);
    }

    protected Boolean verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTime(Meetup meetup) throws MeetupAlreadyExistsException{
        if(repository.existsByEventIgnoringCaseAndMeetupDate(meetup.getEvent(), meetup.getMeetupDate()))
            throw new MeetupAlreadyExistsException();
        return false;
    }

    protected boolean verifyNullId(Long id ) throws IllegalArgumentException{
        if(id == null){
            throw new IllegalArgumentException("Id n??o pode ser nulo!!");
        }
        return false;
    }


    protected Boolean verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTimeWhenTryToUpdate(Long id, Meetup receivedMeetup) {
        String event = receivedMeetup.getEvent();
        LocalDateTime meetupDate = receivedMeetup.getMeetupDate();

        if(repository.existsByEventIgnoringCaseAndMeetupDate(event, meetupDate)){
            if(repository.findByEventIgnoringCaseAndMeetupDate(event, meetupDate)
                    .get().getId() != id)
                throw new MeetupAlreadyExistsException();
        }
        return false;
    }
}
