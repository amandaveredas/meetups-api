package com.microservicemeetup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RegistrationNotFoundException extends Exception {
    public RegistrationNotFoundException() {
        super("Não foi possível encontrar o registro com o id informado.");
    }
}
