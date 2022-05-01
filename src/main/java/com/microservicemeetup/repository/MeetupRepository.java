package com.microservicemeetup.repository;

import com.microservicemeetup.exceptions.MeetupAlreadyExistsException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    Meetup save(Meetup meetup);

    Boolean existsByEventAndMeetupDate(String event, LocalDateTime dateTime);

    Optional<Meetup> findById(Long id);

    void delete(Meetup meetup);

    Optional<Meetup> findByEventAndMeetupDate(String event, LocalDateTime meetupDate);
}
