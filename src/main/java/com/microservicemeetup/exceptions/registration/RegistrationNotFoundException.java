package com.microservicemeetup.exceptions.registration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RegistrationNotFoundException extends Exception {
    public RegistrationNotFoundException(Long id) {
        super("Não foi possível encontrar o registration com o id: "+ id +".");
    }
}
