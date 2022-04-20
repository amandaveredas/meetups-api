package com.microservicemeetup.service;

import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeetupServiceImpl implements MeetupService {


    @Override
    public Meetup save(Meetup meetup) {
        return null;
    }


}
