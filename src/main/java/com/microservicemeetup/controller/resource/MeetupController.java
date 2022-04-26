package com.microservicemeetup.controller.resource;

import com.microservicemeetup.controller.dto.MeetupDTORequest;
import com.microservicemeetup.controller.dto.MeetupDTOResponse;
import com.microservicemeetup.controller.dto.RegistrationDTOResponse;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.service.MeetupService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/meetup")
public class MeetupController {

    private MeetupService service;
    private ModelMapper modelMapper;

    public MeetupController(MeetupService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetupDTOResponse create(@RequestBody @Valid MeetupDTORequest dtoRequest){
        Meetup entity = modelMapper.map(dtoRequest,Meetup.class);
        entity = service.save(entity);

        return modelMapper.map(entity, MeetupDTOResponse.class);
    }



}
