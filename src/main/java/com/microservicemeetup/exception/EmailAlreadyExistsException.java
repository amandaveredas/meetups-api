package com.microservicemeetup.exception;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(){
        super("Já existe um usuário cadastrado com esse email.");
    }
}
