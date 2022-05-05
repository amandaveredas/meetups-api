package com.microservicemeetup.service.registration;

import com.microservicemeetup.exceptions.registration.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.RegistrationRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RegistrationServiceImpl implements RegistrationService{

    private RegistrationRepository repository;

    public RegistrationServiceImpl(RegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Registration save(Registration registration) throws EmailAlreadyExistsException {
        verifyIfExistsByEmail(registration);

        registration.setDateOfRegistration(LocalDate.now());

        return repository.save(registration);
    }

    @Override
    public Optional<Registration> getById(Long id) throws RegistrationNotFoundException, IllegalArgumentException {
        verifyNullId(id);

        Optional<Registration> foundRegistration = repository.findById(id);

        if(foundRegistration.isEmpty()){
            throw new RegistrationNotFoundException(id);
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
        verifyDuplicatedEmailWhenTryToUpdate(id, registration);
        Registration updatedRegistration = Registration.builder()
                .id(id)
                .name(registration.getName())
                .email(registration.getEmail())
                .dateOfRegistration(LocalDate.now())
                .registrationAttribute(registration.getRegistrationAttribute())
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
    public Set<Registration> getByRegistrationAttribute(String registrationAttribute) {
        return new LinkedHashSet<>(repository.findByRegistrationAttributeIgnoringCase(registrationAttribute));
    }

    protected Registration createANewRegister(Registration registration) throws EmailAlreadyExistsException {
        return this.save(registration);
    }

    protected boolean verifyDuplicatedEmailWhenTryToUpdate(Long id, Registration registration) throws EmailAlreadyExistsException {
        if(repository.existsByEmail(registration.getEmail())){
            if (repository.findByEmail(registration.getEmail()).get().getId() != id)
                throw new EmailAlreadyExistsException();
        }
        return false;
    }

    protected boolean verifyIfExistsByEmail(Registration registration) throws EmailAlreadyExistsException {
        if(repository.existsByEmail(registration.getEmail())){
            throw new EmailAlreadyExistsException();
        }
        return false;
    }


    protected boolean verifyNullId(Long id ) throws IllegalArgumentException{
        if(id == null){
            throw new IllegalArgumentException("Id não pode ser nulo!!");
        }
        return false;
    }

}
