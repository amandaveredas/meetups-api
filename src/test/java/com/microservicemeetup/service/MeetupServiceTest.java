package com.microservicemeetup.service;

import com.microservicemeetup.controller.dto.MeetupDTORequest;
import com.microservicemeetup.controller.dto.RegistrationDTORequest;
import com.microservicemeetup.exceptions.DuplicatedMeetupException;
import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.MeetupNotFoundException;
import com.microservicemeetup.exceptions.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.MeetupRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    @Test
    @DisplayName("Should Not Create A Meetup And Throws A Duplicated Meetup Exception")
    void shouldNotCreateAMeetupAndThrowADuplicatedMeetupException_whenTryToSaveADuplicatedMeetup() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNullRegistrationAttributeAndNulId();
        String expectedMessage = "Já existe um meetup com os mesmos dados!";
        Mockito.when(meetupService.verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTime(meetup)).thenThrow(new DuplicatedMeetupException());

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.save(meetup));

        assertThat(e)
                .isInstanceOf(DuplicatedMeetupException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).save(meetup);
    }

    @Test
    @DisplayName("Should return an Validation Error: Fields cannot be empty")
    public void shouldNotSaveAMeetupWithEmptyFields_whenTryToSaveAMeetupWithoutEventOrDateTime() {
        MeetupDTORequest request = MeetupDTORequest.builder().build();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<MeetupDTORequest>> violations = validator.validate(request);

        Assertions.assertEquals(2,violations.size());

    }

    //************************************* getById()

    @Test
    @DisplayName("Should get a Registration by id")
    void shouldGetAMeetupById_whenGetByIdMethodIsCalled() {
        Long id = 1L;
        Meetup meetup = createValidMeetupWithRegistrationsByRegistrationAttribute();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(meetup));

        Optional<Meetup> foundMeetup = meetupService.getById(id);

        assertThat(foundMeetup).isPresent();
        assertThat(foundMeetup.get().getId()).isEqualTo(id);
        assertThat(foundMeetup.get().getEvent()).isEqualTo(meetup.getEvent());
        assertThat(foundMeetup.get().getMeetupDate()).isEqualTo(meetup.getMeetupDate());
        assertThat(foundMeetup.get().getRegistrationAttribute()).isEqualTo(meetup.getRegistrationAttribute());
        assertThat(foundMeetup.get().getRegistrations()).isEqualTo(meetup.getRegistrations());

    }

    @Test
    @DisplayName("Should not get a registration because the id was not found")
    void shouldNotGetAMeetupById_whenGetByIdMethodIsCalled() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        String expectedMessage = "Não foi possível encontrar o meetup com o id informado.";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.getById(Mockito.anyLong()));


        assertThat(e)
                .isInstanceOf(MeetupNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should throw illegal argument exception because id is null")
    void shouldThrownAnExceptionARegistrationById_whenIdIsNull() {
        Long id = null;
        String expectedMessage = "Id não pode ser nulo!!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.getById(id));

        assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
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
