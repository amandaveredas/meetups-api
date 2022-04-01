package com.microservicemeetup.exception;

public class RegistrationFoundButNotDeletedException extends Exception {
    public RegistrationFoundButNotDeletedException() {
        super("Não foi possível excluir o registro!");
    }
}
