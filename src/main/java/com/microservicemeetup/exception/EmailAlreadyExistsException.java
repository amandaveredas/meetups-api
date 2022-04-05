package com.microservicemeetup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(){
        super("Já existe um usuário cadastrado com esse email.");
    }
}
