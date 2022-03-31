package com.microservicemeetup.exceptions;

public class RegistrationAlreadyExistsException extends Exception {
    public RegistrationAlreadyExistsException(){
        super("Já existe um registro cadastrado com esses dados.");
    }
}
