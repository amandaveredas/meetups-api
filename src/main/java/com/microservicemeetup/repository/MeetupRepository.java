package com.microservicemeetup.repository;

import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    Meetup save(Meetup meetup);


}
