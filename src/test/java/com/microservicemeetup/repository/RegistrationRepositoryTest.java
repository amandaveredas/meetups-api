package com.microservicemeetup.repository;

import com.microservicemeetup.model.Registration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@DataJpaTest
public class RegistrationRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RegistrationRepository repository;

    @Test
    @DisplayName("Should return true when an registration exists by registration version.")
    void shouldReturnTrue_whenRegistrationExistsByRegistrationVersion() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        boolean exists = repository.existsByRegistrationAttribute(createNewRegistration().getRegistrationAttribute());
        Assertions.assertTrue(exists);

    }

    @Test
    @DisplayName("Should return false when a registration doesn't exists by registration version.")
    void shouldReturnFalse_whenRegistrationDoesntExistsByRegistrationVersion() {
        Registration registration = createNewRegistration();

        boolean exists = repository.existsByRegistrationAttribute(createNewRegistration().getRegistrationAttribute());
        Assertions.assertFalse(exists);

    }

    @Test
    @DisplayName("Should return true when an registration exists by email.")
    void shouldReturnTrue_whenRegistrationExistsByEmail() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        boolean exists = repository.existsByEmail(createNewRegistration().getEmail());
        Assertions.assertTrue(exists);

    }

    @Test
    @DisplayName("Should return false when a registration doesn't exists by email.")
    void shouldReturnFalse_whenRegistrationDoesntExistsByEmail() {
        Registration registration = createNewRegistration();

        boolean exists = repository.existsByEmail(createNewRegistration().getEmail());
        Assertions.assertFalse(exists);

    }

    @Test
    @DisplayName("Should find a Registration By id")
    void shouldFindById_whenThisRegistrationExists() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        Optional<Registration> foundRegistration = repository.findById(registration.getId());

        assertThat(foundRegistration.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not find a Registration By id")
    void shouldNotFindById_whenThisRegistrationDontExists() {
        Registration registration = createNewRegistration();

        Optional<Registration> foundRegistration = repository.findById(1L);

        assertThat(foundRegistration.isEmpty()).isTrue();
    }



    @Test
    @DisplayName("Should find a Registration By email")
    void shouldFindByEmail_whenThisRegistrationExists() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        Optional<Registration> foundRegistration = repository.findByEmail(registration.getEmail());

        assertThat(foundRegistration).isPresent();
    }

    @Test
    @DisplayName("Should not find a Registration By email")
    void shouldNotFindByEmail_whenThisRegistrationDontExists() {
        Registration registration = createNewRegistration();

        Optional<Registration> foundRegistration = repository.findByEmail(registration.getEmail());

        assertThat(foundRegistration).isEmpty();
    }

    @Test
    @DisplayName("Should find a Registration By attribute")
    void shouldFindByRegistrationAttribute_whenThisRegistrationExists() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        List<Registration> foundRegistration = repository.findByRegistrationAttributeIgnoringCase(registration.getRegistrationAttribute());

        assertThat(foundRegistration).hasSize(1);
    }

    @Test
    @DisplayName("Should not find a Registration By attribute")
    void shouldNotFindByRegistrationAttribute_whenThisRegistrationDontExists() {
        Registration registration = createNewRegistration();

        List<Registration> foundRegistration = repository.findByRegistrationAttributeIgnoringCase(registration.getRegistrationAttribute());

        assertThat(foundRegistration).isEmpty();
    }

    @Test
    @DisplayName("Should save a Registration")
    void shouldSaveARegistration_whenAllTheRequirementsAreSatisfied() {
        Registration registration = createNewRegistration();

        Registration savedRegistration = repository.save(registration);

        assertThat(savedRegistration.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete a Registration")
    void shouldDeleteARegistration_whenRegistrationExists() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        Registration foundRegistration = entityManager
                .find(Registration.class, registration.getId());

        repository.delete(foundRegistration);
        Registration deletedRegistration = entityManager
                .find(Registration.class, registration.getId());

        assertThat(deletedRegistration).isNull();

    }

    private Registration createNewRegistration() {
        return Registration.builder()
                .name("Amanda Lima")
                .email("amanda@teste.com")
                .dateOfRegistration(LocalDate.now())
                .registrationAttribute("001").build();
    }
}
