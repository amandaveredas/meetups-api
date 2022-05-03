package com.microservicemeetup.repository;

import com.microservicemeetup.model.Meetup;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class MeetupRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MeetupRepository repository;

    //********************** SAVE
    @Test
    @DisplayName("Should save a Meetup")
    void shouldSaveAMeetup_whenSaveMEthodIsCalled() {
        Meetup meetup = createNewMeetup();
        Meetup savedMeetup = repository.save(meetup);

        assertThat(savedMeetup.getId()).isNotNull();

    }

    //********************** existsByEventAndMeetupDate
    @Test
    @DisplayName("Should return true when an meetup exists by event and date.")
    void shouldReturnTrue_whenAMeetupExistsByByEventAndMeetupDate() {
        Meetup meetup = createNewMeetup();
        entityManager.persist(meetup);

        boolean exists = repository.existsByEventAndMeetupDate(createNewMeetup().getEvent(), createNewMeetup().getMeetupDate());
        Assertions.assertTrue(exists);

    }

    @Test
    @DisplayName("Should return false when an meetup exists by event and date.")
    void shouldReturnFalse_whenAMeetupDontExistsByByEventAndMeetupDate() {
        Meetup meetup = createNewMeetup();

        boolean exists = repository.existsByEventAndMeetupDate(meetup.getEvent(), meetup.getMeetupDate());
        Assertions.assertFalse(exists);

    }

    //******************** FIND BY ID
    @Test
    @DisplayName("Should find a Meetup By id")
    void shouldFindAMeetupById() {
        Meetup meetup = createNewMeetup();
        entityManager.persist(meetup);

        Optional<Meetup> foundMeetup = repository.findById(meetup.getId());

        assertThat(foundMeetup.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not find a Meetup By id")
    void shoulNotFindAMeetupById() {
        Meetup meetup = createNewMeetup();

        Optional<Meetup> foundMeetup = repository.findById(1L);

        assertThat(foundMeetup.isEmpty()).isTrue();
    }

    //******************** FIND BY EVENT AND MEETUPDATE
    @Test
    @DisplayName("Should find a Meetup By event and meetup date")
    void shouldFindAMeetupByEventAndMeetupDate() {
        Meetup meetup = createNewMeetup();
        entityManager.persist(meetup);

        Optional<Meetup> foundMeetup = repository.findByEventAndMeetupDate(meetup.getEvent(), meetup.getMeetupDate());

        assertThat(foundMeetup.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not find a Meetup By event and meetup date")
    void shoulNotFindAMeetupByEventAndMeetupDate() {
        Meetup meetup = createNewMeetup();

        Optional<Meetup> foundMeetup = repository.findByEventAndMeetupDate(meetup.getEvent(), meetup.getMeetupDate());


        assertThat(foundMeetup.isEmpty()).isTrue();
    }

    //************************** DELETE
    @Test
    @DisplayName("Should delete a Metup")
    void shouldDeleteAMeetup() {
        Meetup meetup = createNewMeetup();
        entityManager.persist(meetup);

        Meetup foundMeetup = entityManager
                .find(Meetup.class, meetup.getId());

        repository.delete(foundMeetup);
        Meetup deletedMeetup = entityManager
                .find(Meetup.class, meetup.getId());

        assertThat(deletedMeetup).isNull();

    }

    private Meetup createNewMeetup() {
        return Meetup.builder()
                .event("Encontro Anual da Liderança")
                .meetupDate(LocalDateTime.of(2022,05,02,19,00))
                .registrations(new LinkedHashSet(Arrays.asList(Registration.builder().id(1L).name("Amanda").build(),
                        Registration.builder().id(2L).name("Roni").build())))
                .registrationAttribute("Gestão").build();
    }
}
