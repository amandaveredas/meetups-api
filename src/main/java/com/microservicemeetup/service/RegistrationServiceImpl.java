package com.microservicemeetup.service;

import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.entity.Registration;
import com.microservicemeetup.repository.RegistrationRepository;
import org.springframework.data.domain.*;
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
    public Registration save(Registration registration) throws EmailAlreadyExistsException {

        verifyIfExistsByEmail(registration);

        registration.setDateOfRegistration(LocalDate.now());
        registration.setRegistrationVersion(firstRegistration);

        return repository.save(registration);
    }

    @Override
    public Optional<Registration> getById(Long id) throws RegistrationNotFoundException, IllegalArgumentException {
        verifyNullId(id);

        Optional<Registration> foundRegistration = repository.findById(id);

        if(foundRegistration.isEmpty()){
            throw new RegistrationNotFoundException();
        }
        return foundRegistration;
    }

    @Override
    public void delete(Long id) throws RegistrationNotFoundException{
        this.getById(id);
        repository.deleteById(id);
    }


    @Override
    public Registration update(Long id, Registration registration) throws EmailAlreadyExistsException {
        Optional<Registration> actualRegistration = repository.findById(id);

        if(actualRegistration.isEmpty()){
            return createANewRegister(registration);
        }

        //udating a version
        verifyDuplicatedEmail(id, registration);
        String updatedVersion = getUpdatedVersion(actualRegistration);

        Registration updatedRegistration = Registration.builder()
                .id(id)
                .name(registration.getName())
                .email(registration.getEmail())
                .dateOfRegistration(LocalDate.now())
                .registrationVersion(updatedVersion)
                .build();

        return repository.save(updatedRegistration);
    }

    @Override
    public Page<Registration> find(Registration filter, Pageable pageable) {
        Example<Registration> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example,pageable);
    }

    @Override
    public Optional<Registration> getRegistrationByRegistrationVersion(String registrationAtribute) {
        return repository.findByRegistrationVersion(registrationAtribute);
    }

    private String getUpdatedVersion(Optional<Registration> actualRegistration) {
        return "00" +
                String.valueOf(Integer.parseInt(actualRegistration.get().getRegistrationVersion()) + 1);
    }

    private Registration createANewRegister(Registration registration) throws EmailAlreadyExistsException {
        return this.save(registration);
    }

    private void verifyDuplicatedEmail(Long id, Registration registration) throws EmailAlreadyExistsException {
        if(repository.existsByEmail(registration.getEmail())){
            if (repository.findByEmail(registration.getEmail()).getId() != id)
                throw new EmailAlreadyExistsException();
        }
    }

    private void verifyIfExistsByEmail(Registration registration) throws EmailAlreadyExistsException {
        if(repository.existsByEmail(registration.getEmail())){
            throw new EmailAlreadyExistsException();
        }
    }


    private void verifyNullId(Long id ) throws IllegalArgumentException{
        if(id == null){
            throw new IllegalArgumentException("Registro ou registro_id não podem ser nulos!!");
        }
    }

}
