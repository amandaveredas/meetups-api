package com.microservicemeetup.exception;

public class RegistrationNotFoundException extends Exception {
    public RegistrationNotFoundException(String message) {
        super(message);
    }
}
