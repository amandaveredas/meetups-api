package com.microservicemeetup.repository;

import com.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Boolean existsByEmail(String email);
    Registration save(Registration registration);

    boolean existsByRegistration(Registration registration);

    Registration findByEmail(String email);

    Optional<Registration> findByRegistrationAtribute(String registrationAtribute);
}
