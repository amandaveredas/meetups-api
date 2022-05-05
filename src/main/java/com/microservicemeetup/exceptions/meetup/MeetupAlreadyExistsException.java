package com.microservicemeetup.exceptions.meetup;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MeetupAlreadyExistsException extends RuntimeException {
    public MeetupAlreadyExistsException() {
        super("Já existe um meetup cadastrado com esses dados.");
    }
}
