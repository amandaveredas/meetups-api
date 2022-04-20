package com.microservicemeetup.repository;

import com.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByRegistrationVersion(String registration);

    Boolean existsByEmail(String email);

    Optional<Registration> findById(Long id);

    Optional<Registration> findByRegistrationVersion(String registrationAtribute);

    Optional<Registration> findByEmail(String email);

    Registration save(Registration registration);

    void deleteById(Long id);








}
