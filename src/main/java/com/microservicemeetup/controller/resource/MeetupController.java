package com.microservicemeetup.controller.resource;

import com.microservicemeetup.controller.dto.meetup.MeetupDTORequest;
import com.microservicemeetup.controller.dto.meetup.MeetupDTORequestFilter;
import com.microservicemeetup.controller.dto.meetup.MeetupDTOResponse;
import com.microservicemeetup.exceptions.meetup.MeetupAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupNotFoundException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.service.meetup.MeetupService;
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
@RequestMapping("/meetup/v1")
public class MeetupController {

    private MeetupService service;
    private ModelMapper modelMapper;

    public MeetupController(MeetupService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetupDTOResponse create(@RequestBody @Valid MeetupDTORequest dtoRequest) throws MeetupAlreadyExistsException, RegistrationNotFoundException {
        Meetup entity = modelMapper.map(dtoRequest,Meetup.class);
        entity = service.save(entity);

        return modelMapper.map(entity, MeetupDTOResponse.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MeetupDTOResponse> find(MeetupDTORequestFilter dtoFilter, Pageable pageable) {
        Meetup filter = modelMapper.map(dtoFilter, Meetup.class);
        Page<Meetup> result = service.find(filter, pageable);

        List<MeetupDTOResponse> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, MeetupDTOResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<MeetupDTOResponse>(list, pageable, result.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MeetupDTOResponse getById(@PathVariable Long id) throws MeetupNotFoundException{

        return service
                .getById(id)
                .map(meetup -> modelMapper.map(meetup,MeetupDTOResponse.class))
                .orElseThrow(() -> new MeetupNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById (@PathVariable Long id) throws MeetupNotFoundException{
        service.getById(id);
        service.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MeetupDTOResponse updateById (@PathVariable Long id, @RequestBody @Valid MeetupDTORequest dtoRequest) throws MeetupAlreadyExistsException, RegistrationNotFoundException {
        Meetup entity = modelMapper.map(dtoRequest,Meetup.class);
        entity = service.update(id,entity);

        return modelMapper.map(entity,MeetupDTOResponse.class);
    }


}
