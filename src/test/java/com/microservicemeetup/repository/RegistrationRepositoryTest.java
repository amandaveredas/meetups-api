package com.microservicemeetup.repository;

import com.microservicemeetup.model.Registration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RegistrationRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RegistrationRepository repository;

    @Test
    @DisplayName("Should return true when an registration exists by registration version.")
    void returnTrueWhenRegistrationExistsByRegistrationVersion() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        boolean exists = repository.existsByRegistrationAttribute(createNewRegistration().getRegistrationAttribute());
        Assertions.assertTrue(exists);

    }

    @Test
    @DisplayName("Should return false when a registration doesn't exists by registration version.")
    void returnFalseWhenRegistrationDoesntExistsByRegistrationVersion() {
        Registration registration = createNewRegistration();

        boolean exists = repository.existsByRegistrationAttribute(createNewRegistration().getRegistrationAttribute());
        Assertions.assertFalse(exists);

    }

    @Test
    @DisplayName("Should return true when an registration exists by email.")
    void returnTrueWhenRegistrationExistsByEmail() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        boolean exists = repository.existsByEmail(createNewRegistration().getEmail());
        Assertions.assertTrue(exists);

    }

    @Test
    @DisplayName("Should return false when a registration doesn't exists by email.")
    void returnFalseWhenRegistrationDoesntExistsByEmail() {
        Registration registration = createNewRegistration();

        boolean exists = repository.existsByEmail(createNewRegistration().getEmail());
        Assertions.assertFalse(exists);

    }

    @Test
    @DisplayName("Should find a Registration By id")
    void findByIdTest() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        Optional<Registration> foundRegistration = repository.findById(registration.getId());

        assertThat(foundRegistration.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not find a Registration By id")
    void notFindByIdTest() {
        Registration registration = createNewRegistration();

        Optional<Registration> foundRegistration = repository.findById(1L);

        assertThat(foundRegistration.isEmpty()).isTrue();
    }



    @Test
    @DisplayName("Should find a Registration By email")
    void findByEmail() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        Optional<Registration> foundRegistration = repository.findByEmail(registration.getEmail());

        assertThat(foundRegistration).isPresent();
    }

    @Test
    @DisplayName("Should not find a Registration By email")
    void notFindByEmail() {
        Registration registration = createNewRegistration();

        Optional<Registration> foundRegistration = repository.findByEmail(registration.getEmail());

        assertThat(foundRegistration).isEmpty();
    }

    @Test
    @DisplayName("Should find a Registration By attribute")
    void findByRegistrationAttribute() {
        Registration registration = createNewRegistration();
        entityManager.persist(registration);

        List<Registration> foundRegistration = repository.findByRegistrationAttribute(registration.getRegistrationAttribute());

        assertThat(foundRegistration).hasSize(1);
    }

    @Test
    @DisplayName("Should not find a Registration By attribute")
    void notFindByRegistrationAttribute() {
        Registration registration = createNewRegistration();

        List<Registration> foundRegistration = repository.findByRegistrationAttribute(registration.getRegistrationAttribute());

        assertThat(foundRegistration).isEmpty();
    }

    @Test
    @DisplayName("Should save a Registration")
    void saveARegistration() {
        Registration registration = createNewRegistration();

        Registration savedRegistration = repository.save(registration);

        assertThat(savedRegistration.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete a Registration")
    void deleteARegistration() {
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
