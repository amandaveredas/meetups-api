package com.microservicemeetup.repository;

import com.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Boolean existsByNameAndEmail(String name, String email);
    Registration save(Registration registration);
}
