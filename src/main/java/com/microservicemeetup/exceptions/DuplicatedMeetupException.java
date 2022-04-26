package com.microservicemeetup.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedMeetupException extends RuntimeException{
    public DuplicatedMeetupException() {
        super("Já existe um meetup com os mesmos dados!");
    }
}
