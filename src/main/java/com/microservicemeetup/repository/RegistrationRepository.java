package com.microservicemeetup.repository;

import com.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByRegistrationAttribute(String registrationAtribute);

    Boolean existsByEmail(String email);

    Optional<Registration> findById(Long id);

    List<Registration> findByRegistrationAttributeIgnoringCase(String registrationAtribute);

    Optional<Registration> findByEmail(String email);

    Registration save(Registration registration);

    void deleteById(Long id);










}
