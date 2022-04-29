package com.microservicemeetup.service;

import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.MeetupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MeetupServiceTest {

    @InjectMocks
    MeetupServiceImpl meetupService;

    @Mock
    MeetupRepository repository;
    @Mock
    RegistrationServiceImpl registrationService;

    @BeforeEach
    public void setUp(){
        this.meetupService = new MeetupServiceImpl(registrationService,repository);
    }

    //************************************* save()
    @Test
    @DisplayName("Should save an meetup with sucess when a empty list and empty registration attribute are gave.")
    public void shouldCreateAMeetupWithSucces_whenNoRegistrationsAreGave() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNullRegistrationAttributeAndNulId();
        Mockito.when(repository.save(meetup)).thenReturn(createValidMeetupWithoutRegistrationsAndNullRegistrationAttribute());

        Meetup savedMeetup = meetupService.save(meetup);

        assertThat(savedMeetup.getId()).isEqualTo(1L);
        assertThat(savedMeetup.getEvent()).isEqualTo("Encontro anual da liderança");
        assertThat(savedMeetup.getMeetupDate()).isEqualTo(LocalDateTime.of(2022,05,05,19,00));
        assertThat(savedMeetup.getRegistrationAttribute()).isNullOrEmpty();
        assertThat(savedMeetup.getRegistrations()).isNullOrEmpty();
    }

    @Test
    @DisplayName("Should save an meetup with sucess when a empty list is gave and with a registration attribute defined.")
    public void shouldCreateAMeetupWithSuccess_whenARegistrationAttributeAreGave() {
        Meetup meetupRequest = createValidMeetupWithoutRegistrationsAndNulId();
        Meetup meetup = createValidMeetupWithRegistrationsByRegistrationAttribute();
        meetup.setId(null);
        Mockito.when(registrationService.getByRegistrationAttribute(meetup.getRegistrationAttribute())).thenReturn(createListOfRegistrations());
        Mockito.when(repository.save(meetup)).thenReturn(createValidMeetupWithRegistrationsByRegistrationAttribute());

        Meetup savedMeetup = meetupService.save(meetupRequest);

        assertThat(savedMeetup.getId()).isEqualTo(1L);
        assertThat(savedMeetup.getEvent()).isEqualTo("Encontro anual da liderança");
        assertThat(savedMeetup.getMeetupDate()).isEqualTo(LocalDateTime.of(2022,05,05,19,00));
        assertThat(savedMeetup.getRegistrationAttribute()).isEqualTo("gestão");
        assertThat(savedMeetup.getRegistrations()).isEqualTo(createValidMeetupWithRegistrationsByRegistrationAttribute().getRegistrations());
        assertThat(savedMeetup.getRegistrations()).hasSize(2);
    }


    @Test
    @DisplayName("Should save an meetup with sucess when a registration's list is gave and with a empty registration attribute.")
    public void shouldCreateAMeetupWithSuccess_whenARegistrationsListIsGave() {
        Meetup meetupRequest = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();
        Mockito.when(registrationService.getByRegistrationAttribute(meetupRequest.getRegistrationAttribute())).thenReturn(Collections.emptyList());
        Mockito.when(repository.save(meetupRequest)).thenReturn(createValidMeetupWithRegistrationsAndNullRegistrationAttribute());

        Meetup savedMeetup = meetupService.save(meetupRequest);

        assertThat(savedMeetup.getId()).isEqualTo(1L);
        assertThat(savedMeetup.getEvent()).isEqualTo("Encontro anual da liderança");
        assertThat(savedMeetup.getMeetupDate()).isEqualTo(LocalDateTime.of(2022,05,05,19,00));
        assertThat(savedMeetup.getRegistrationAttribute()).isNullOrEmpty();
        assertThat(savedMeetup.getRegistrations()).isEqualTo(createValidMeetupWithRegistrationsAndNullRegistrationAttribute().getRegistrations());
        assertThat(savedMeetup.getRegistrations()).hasSize(2);
    }

    @Test
    @DisplayName("Should save an meetup with success when a registration's list and registrationAttribute are gave.")
    public void shouldCreateAMeetupWithSuccess_whenARegistrationsListAndRegistrationAttributeAreGave() {
        Meetup meetupRequest = createValidMeetupWithRegistrationsAndRegistrationAttributeAndNulId();
        Meetup meetup = createValidMeetupWithRegistrationsAndRegistrationAttribute();
        meetup.setId(null);
        Mockito.when(registrationService.getByRegistrationAttribute(meetupRequest.getRegistrationAttribute())).thenReturn(createValidMeetupWithRegistrationsAndRegistrationAttribute().getRegistrations());
        Mockito.when(repository.save(meetup)).thenReturn(createValidMeetupWithRegistrationsAndRegistrationAttribute());

        Meetup savedMeetup = meetupService.save(meetupRequest);


        assertThat(savedMeetup.getId()).isEqualTo(1L);
        assertThat(savedMeetup.getEvent()).isEqualTo("Encontro anual da liderança");
        assertThat(savedMeetup.getMeetupDate()).isEqualTo(LocalDateTime.of(2022,05,05,19,00));
        assertThat(savedMeetup.getRegistrationAttribute()).isEqualTo("gestão");
        assertThat(savedMeetup.getRegistrations()).isEqualTo(createValidMeetupWithRegistrationsAndRegistrationAttribute().getRegistrations());
        assertThat(savedMeetup.getRegistrations()).hasSize(4);
    }

    private Meetup createValidMeetupWithRegistrationsAndRegistrationAttribute() {
        Meetup meetup = createValidMeetupWithRegistrationsAndRegistrationAttributeAndNulId();
        Registration registration3 = Registration.builder()
                .id(3L)
                .registrationAttribute("gestão").build();
        Registration registration4 = Registration.builder()
                .id(4L)
                .registrationAttribute("gestão").build();
        List<Registration> addedRegistrationList = new ArrayList<>(meetup.getRegistrations());
        addedRegistrationList.add(registration3);
        addedRegistrationList.add(registration4);
        meetup.setRegistrations(addedRegistrationList);

        meetup.setId(1L);

        return meetup;
    }

    private Meetup createValidMeetupWithRegistrationsAndRegistrationAttributeAndNulId() {
        Meetup meetup = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();
        meetup.setRegistrationAttribute("gestão");
        return meetup;
    }

    private Meetup createValidMeetupWithRegistrationsAndNullRegistrationAttribute() {
        Meetup meetup = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();
        meetup.setId(1L);
        return meetup;
    }

    private Meetup createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNullRegistrationAttributeAndNulId();
        List<Registration> registrations = createListOfRegistrations();
        meetup.setRegistrations(registrations);
        return meetup;
    }

    private Meetup createValidMeetupWithRegistrationsByRegistrationAttribute() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNulId();
        meetup.setId(1L);
        List<Registration> registrations = createListOfRegistrations();
        meetup.setRegistrations(registrations);
        return meetup;
    }

    private Meetup createValidMeetupWithoutRegistrationsAndNulId() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNullRegistrationAttributeAndNulId();
        meetup.setRegistrationAttribute("gestão");
        return meetup;
    }

    private Meetup createValidMeetupWithoutRegistrationsAndNullRegistrationAttribute() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNullRegistrationAttributeAndNulId();
        meetup.setId(1L);
        return meetup;
    }

    private Meetup createValidMeetupWithoutRegistrationsAndNullRegistrationAttributeAndNulId() {
        return Meetup.builder()
                .meetupDate(LocalDateTime.of(2022,05,05,19,00))
                .event("Encontro anual da liderança")
                .build();
    }

    private List<Registration> createListOfRegistrations() {
        Registration registration1 = Registration.builder()
                .id(1L)
                .registrationAttribute("gestão").build();
        Registration registration2 = Registration.builder()
                .id(2L)
                .registrationAttribute("gestão").build();
        return Arrays.asList(registration1,registration2);
    }


}
