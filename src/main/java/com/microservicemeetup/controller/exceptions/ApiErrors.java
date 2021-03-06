package com.microservicemeetup.controller.exceptions;

import com.microservicemeetup.exceptions.registration.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupNotFoundException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    private List<String> errors;

    public ApiErrors(BindingResult bindingResult){
        this.errors = new ArrayList<>();
        bindingResult
                .getAllErrors()
                .forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors (EmailAlreadyExistsException e){
        this.errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors (RegistrationNotFoundException e){
        this.errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors (MeetupAlreadyExistsException e){
        this.errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors (MeetupNotFoundException e){
        this.errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors (ResponseStatusException e){
        this.errors = Arrays.asList(e.getReason());
    }

    public List<String> getErrors() {
        return errors;
    }
}
