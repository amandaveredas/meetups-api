package com.microservicemeetup.service;

import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationFoundButNotDeletedException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;
import com.microservicemeetup.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service

public class RegistrationServiceImpl implements RegistrationService{

    private RegistrationRepository repository;
    private String firstRegistration = "001";

    public RegistrationServiceImpl(RegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Registration save(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException {

        if(repository.existsByEmail(registrationDTORequest.getEmail())){
            throw new EmailAlreadyExistsException();
        }

        Registration registration = Registration.builder()
                .name(registrationDTORequest.getName())
                .email(registrationDTORequest.getEmail())
                .registrationVersion(firstRegistration)
                .dateOfRegistration(LocalDate.now())
                .build();

        return repository.save(registration);
    }

    @Override
    public Optional<Registration> getById(Long id) throws RegistrationNotFoundException {
        Optional<Registration> foundRegistration = repository.findById(id);

        if(foundRegistration.isEmpty()){
            throw new RegistrationNotFoundException("Não foi possível encontrar o registro com o id informado.");
        }
        return foundRegistration;
    }

    @Override
    public void delete(Registration registration) throws RegistrationNotFoundException, RegistrationFoundButNotDeletedException {
        if(registration == null || registration.getId() == null){
            throw new IllegalArgumentException("Registro ou registro_id não podem ser nulos!!");
        }

        if (!repository.existsByRegistration(registration)){
            throw new RegistrationNotFoundException("Registro não encontrado!");
        }

        repository.delete(registration);

        if (repository.existsById(registration.getId())){
            throw new RegistrationFoundButNotDeletedException();
        }
    }
}
