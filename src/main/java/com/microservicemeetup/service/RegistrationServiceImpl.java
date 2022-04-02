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

        verifyIfExistsByEmail(registrationDTORequest);

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
        verifyNullId(registration.getId());
        verifyNullRegistration(registration);
        verifyIfExistsByRegistration(registration);

        repository.delete(registration);

        if (repository.existsById(registration.getId())){
            throw new RegistrationFoundButNotDeletedException();
        }
    }
    
    @Override
    public Registration update(Long id, RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException {
        Optional<Registration> actualRegistration = repository.findById(id);

        if(actualRegistration.isEmpty()){
            return createANewRegister(registrationDTORequest);
        }

        //udating a version
        verifyDuplicatedEmail(id, registrationDTORequest);
        String updatedVersion = getUpdatedVersion(actualRegistration);

        Registration updatedRegistration = Registration.builder()
                .id(id)
                .name(registrationDTORequest.getName())
                .email(registrationDTORequest.getEmail())
                .dateOfRegistration(LocalDate.now())
                .registrationVersion(updatedVersion)
                .build();

        return repository.save(updatedRegistration);
    }

    private String getUpdatedVersion(Optional<Registration> actualRegistration) {
        return "00" +
                String.valueOf(Integer.parseInt(actualRegistration.get().getRegistrationVersion()) + 1);
    }

    private Registration createANewRegister(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException {
        return this.save(registrationDTORequest);
    }

    private void verifyDuplicatedEmail(Long id, RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException {
        if(repository.existsByEmail(registrationDTORequest.getEmail())){
            if (repository.findByEmail(registrationDTORequest.getEmail()).getId() != id)
                throw new EmailAlreadyExistsException();
        }
    }

    private void verifyIfExistsByEmail(RegistrationDTORequest registrationDTORequest) throws EmailAlreadyExistsException {
        if(repository.existsByEmail(registrationDTORequest.getEmail())){
            throw new EmailAlreadyExistsException();
        }
    }

    private void verifyIfExistsByRegistration(Registration registration) throws RegistrationNotFoundException {
        if (!repository.existsByRegistration(registration)){
            throw new RegistrationNotFoundException("Registro não encontrado!");
        }
    }

    private void verifyNullId(Long id ) {
        if(id == null){
            throw new IllegalArgumentException("Registro ou registro_id não podem ser nulos!!");
        }
    }

    private void verifyNullRegistration(Registration registration) {
        if(registration == null){
            throw new IllegalArgumentException("Registro ou registro_id não podem ser nulos!!");
        }
    }
}
