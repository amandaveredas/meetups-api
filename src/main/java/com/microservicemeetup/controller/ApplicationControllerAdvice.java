package com.microservicemeetup.controller;

import com.microservicemeetup.controller.exceptions.ApiErrors;
import com.microservicemeetup.exceptions.registration.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupNotFoundException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidateException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleEmailAlreadyExistsException(EmailAlreadyExistsException e){
        return new ApiErrors(e);
    }

    @ExceptionHandler(RegistrationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrors handleRegistrationNotFoundException(RegistrationNotFoundException e){
        return new ApiErrors(e);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus
    public ResponseEntity handleResponseStatusException(ResponseStatusException e){
        return new ResponseEntity(new ApiErrors(e), e.getStatus());
    }

    @ExceptionHandler(MeetupAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleMeetupAlreadyExistsException(MeetupAlreadyExistsException e){
        return new ApiErrors(e);
    }

    @ExceptionHandler(MeetupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrors handleMeetupNotFoundException(MeetupNotFoundException e){
        return new ApiErrors(e);
    }

}
