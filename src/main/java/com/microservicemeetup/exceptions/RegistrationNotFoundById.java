package com.microservicemeetup.exceptions;

public class RegistrationNotFoundById extends Exception {
    public RegistrationNotFoundById() {
        super("Não foi possível encontrar o registro com o id informado.");
    }
}
