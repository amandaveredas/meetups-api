package com.microservicemeetup.controller.resource;

import com.microservicemeetup.controller.dto.*;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.service.MeetupService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping
    public Page<MeetupDTOResponse> find(MeetupDTORequestFilter dtoFilter, Pageable pageable) {
        Meetup filter = modelMapper.map(dtoFilter, Meetup.class);
        Page<Meetup> result = service.find(filter, pageable);

        List<MeetupDTOResponse> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, MeetupDTOResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<MeetupDTOResponse>(list, pageable, result.getTotalElements());
    }



}
