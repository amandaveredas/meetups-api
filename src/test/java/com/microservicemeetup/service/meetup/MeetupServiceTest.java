package com.microservicemeetup.service.meetup;

import com.microservicemeetup.controller.dto.meetup.MeetupDTORequest;
import com.microservicemeetup.exceptions.meetup.MeetupAlreadyExistsException;
import com.microservicemeetup.exceptions.meetup.MeetupNotFoundException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.repository.MeetupRepository;
import com.microservicemeetup.service.registration.RegistrationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

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
    public void shouldCreateAMeetupWithSucces_whenNoRegistrationsAreGave() throws RegistrationNotFoundException {
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
    public void shouldCreateAMeetupWithSuccess_whenARegistrationAttributeAreGave() throws RegistrationNotFoundException {
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
    public void shouldCreateAMeetupWithSuccess_whenARegistrationsListIsGave() throws RegistrationNotFoundException {
        Meetup meetupRequest = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();
        Mockito.when(registrationService.getByRegistrationAttribute(meetupRequest.getRegistrationAttribute())).thenReturn(new LinkedHashSet<>());
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
    public void shouldCreateAMeetupWithSuccess_whenARegistrationsListAndRegistrationAttributeAreGave() throws RegistrationNotFoundException {
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
        Set<Registration> addedRegistrationList = new LinkedHashSet<>(meetup.getRegistrations());
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
        String expectedMessage = "Já existe um meetup cadastrado com esses dados.";
        Mockito.when(meetupService.verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTime(meetup)).thenThrow(new MeetupAlreadyExistsException());

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.save(meetup));

        assertThat(e)
                .isInstanceOf(MeetupAlreadyExistsException.class)
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
        String expectedMessage = "Não foi possível encontrar o meetup com o id: 0.";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.getById(Mockito.anyLong()));


        assertThat(e)
                .isInstanceOf(MeetupNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should throw illegal argument exception because id is null")
    void shouldThrownAnExceptionAMeetupById_whenTryToGetWithNullId() {
        Long id = null;
        String expectedMessage = "Id não pode ser nulo!!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.getById(id));

        assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    //*************************************  delete()

    @Test
    @DisplayName("Should delete a meetup")
    void shouldDeleteRegistrationdWithSucces_whenDeleteMethodIsCalled() {
        Long id = 1L;
        Meetup  meetup = Meetup.builder().id(id).build();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(meetup));
        Mockito.when(meetupService.getById(id)).thenReturn(Optional.of(meetup));

        Assertions.assertDoesNotThrow(() -> meetupService.delete(id));
        Mockito.verify(repository,Mockito.times(1)).delete(meetup);
    }

    @Test
    @DisplayName("Should thrown a exception because the meetup was not found.")
    void shouldNotDeleteAMeetup_whenMeetupIsNotFound() {
        Long id = 1L;
        Meetup meetup = Meetup.builder().id(id).build();
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        String expectedMessage = "Não foi possível encontrar o meetup com o id: 1.";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.delete(id));

        assertThat(e)
                .isInstanceOf(MeetupNotFoundException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).delete(meetup);
        Mockito.verify(repository,Mockito.times(1)).findById(id);

    }

    @Test
    @DisplayName("Should throw illegal argument exception because id is null")
    void shouldThrownAnExceptionAMeetupById_whenTryToDeleteWithNullId() {
        Long id = null;
        String expectedMessage = "Id não pode ser nulo!!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.getById(id));

        assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    //*********************************** update

    @Test
    @DisplayName("Should update a meetup with succes.")
    void shouldUpdateAMeetupWithSuccess_whenUpdateMethodIsCalled() throws RegistrationNotFoundException {
        Long id = 1L;
        Meetup receivedMeetup = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();
        Meetup expectedMeetup = createValidMeetupWithRegistrationsAndNullRegistrationAttribute();


        Mockito.when(meetupService.verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTimeWhenTryToUpdate(id,receivedMeetup)).thenReturn(false);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(createValidMeetupWithRegistrationsAndNullRegistrationAttribute()));
        Mockito.when(repository.save(expectedMeetup)).thenReturn(expectedMeetup);


        Meetup updatedMeetup = meetupService.update(id, receivedMeetup);


        assertThat(id).isEqualTo(updatedMeetup.getId());
        assertThat(expectedMeetup.getEvent()).isEqualTo(updatedMeetup.getEvent());
        assertThat(expectedMeetup.getMeetupDate()).isEqualTo(updatedMeetup.getMeetupDate());
        assertThat(expectedMeetup.getRegistrationAttribute()).isEqualTo(updatedMeetup.getRegistrationAttribute());
        assertThat(expectedMeetup.getRegistrations()).isEqualTo(updatedMeetup.getRegistrations());
        Mockito.verify(repository,Mockito.times(1)).findById(id);
        Mockito.verify(repository,Mockito.times(1)).save(expectedMeetup);
    }

    @Test
    @DisplayName("Should Create a new Meetup when is required to update a meetup what doesn't exists.")
    void shouldCreateANewMeetup_whenTryToUpdateAMeetupThatDontExists() throws RegistrationNotFoundException {
        Long newId = 2L;
        Meetup receivedMeetup = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();

        Meetup expectedMeetup = createValidMeetupWithRegistrationsAndNullRegistrationAttribute();
        expectedMeetup.setId(newId);

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(meetupService.save(receivedMeetup)).thenReturn(expectedMeetup);

        Meetup updatedMeetup = meetupService.update(anyLong(), receivedMeetup);


        assertThat(newId).isEqualTo(updatedMeetup.getId());
        assertThat(expectedMeetup.getEvent()).isEqualTo(updatedMeetup.getEvent());
        assertThat(expectedMeetup.getMeetupDate()).isEqualTo(updatedMeetup.getMeetupDate());
        assertThat(expectedMeetup.getRegistrationAttribute()).isEqualTo(updatedMeetup.getRegistrationAttribute());
        assertThat(expectedMeetup.getRegistrations()).isEqualTo(updatedMeetup.getRegistrations());
        Mockito.verify(repository,Mockito.times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should thrown an exception when try to update with a duplicated meetup.")
    void shouldNotUpdateAMeetup_whenAlreadExistsTheSameMeetup() {
        Long id = 1L;
        Meetup receivedMeetup = createValidMeetupWithRegistrationsAndNullRegistrationAttributeAndNulId();
        Meetup foundMeetup = createValidMeetupWithRegistrationsAndNullRegistrationAttribute();
        foundMeetup.setId(2L);

        Mockito.when(meetupService.verifyIfAlreadyExistsAMeetupWithSameEventAndSameDateTimeWhenTryToUpdate(id,receivedMeetup)).thenThrow(new MeetupAlreadyExistsException());
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(createValidMeetupWithRegistrationsAndNullRegistrationAttribute()));

        String expectedMessage = "Já existe um meetup cadastrado com esses dados.";


        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> meetupService.update(id,receivedMeetup));


        assertThat(e)
                .isInstanceOf(MeetupAlreadyExistsException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.times(1)).findById(id);
        Mockito.verify(repository,Mockito.times(1)).existsByEventIgnoringCaseAndMeetupDate(receivedMeetup.getEvent(), receivedMeetup.getMeetupDate());
        Mockito.verify(repository,Mockito.never()).save(receivedMeetup);

    }

    //*********************************** findAll

    @Test
    @DisplayName("Should return a page with all matches with filter")
    void shouldFindMeetups_whenAFilterIsGave() {
        Meetup meetup = createValidMeetupWithRegistrationsByRegistrationAttribute();
        PageRequest pageRequest = PageRequest.of(0,10);
        List<Meetup> meetups = Arrays.asList(meetup);
        Page<Meetup> page = new PageImpl<Meetup>(Arrays.asList(meetup),
                PageRequest.of(0,10),1);

        Mockito.when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        Page<Meetup> result = meetupService.find(meetup,pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(meetups);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
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
        Set<Registration> registrations = createListOfRegistrations();
        meetup.setRegistrations(registrations);
        return meetup;
    }

    private Meetup createValidMeetupWithRegistrationsByRegistrationAttribute() {
        Meetup meetup = createValidMeetupWithoutRegistrationsAndNulId();
        meetup.setId(1L);
        Set<Registration> registrations = createListOfRegistrations();
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

    private Set<Registration> createListOfRegistrations() {
        Registration registration1 = Registration.builder()
                .id(1L)
                .registrationAttribute("gestão").build();
        Registration registration2 = Registration.builder()
                .id(2L)
                .registrationAttribute("gestão").build();
        return new LinkedHashSet<>(Arrays.asList(registration1,registration2));
    }


}
