package com.microservicemeetup.exceptions.meetup;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MeetupNotFoundException extends RuntimeException {
    public MeetupNotFoundException(Long id) {
        super("Não foi possível encontrar o meetup com o id: "+ id +".");
    }
}
