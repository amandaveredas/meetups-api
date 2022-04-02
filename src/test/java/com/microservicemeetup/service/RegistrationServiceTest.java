package com.microservicemeetup.service;

import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationFoundButNotDeletedException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;
import com.microservicemeetup.repository.RegistrationRepository;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    @InjectMocks
    RegistrationServiceImpl registrationService;

    @Mock
    RegistrationRepository repository;

    @BeforeEach
    public void setUp(){
        this.registrationService = new RegistrationServiceImpl(repository);
    }


    //************************************* save()


    @Test
    @DisplayName("Should save an registration")
    public void saveRegistrationWithSucces() throws EmailAlreadyExistsException {

        RegistrationDTORequest registrationDTORequest = createdValidRegistrationDTORequest();
        Registration registration = createdValidRegistrationWithoutId();


        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createdValidRegistrationWithId());

        Registration savedRegistration = registrationService.save(registrationDTORequest);


        assertThat(savedRegistration.getId()).isEqualTo(1L);
        assertThat(savedRegistration.getName()).isEqualTo("Amanda Lima");
        assertThat(savedRegistration.getEmail()).isEqualTo("amanda@teste.com.br");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        assertThat(savedRegistration.getRegistrationVersion()).isEqualTo("001");
    }

    @Test
    @DisplayName("Should return an EmailAlreadyExistsException")
    public void shouldNotSaveRegistrationWithDuplicatedEmail() {
        RegistrationDTORequest registrationDTORequest = createdValidRegistrationDTORequest();
        String expectedMessage = "Já existe um usuário cadastrado com esse email.";


        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.save(registrationDTORequest));


        assertThat(e)
                .isInstanceOf(EmailAlreadyExistsException.class)
                        .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).save(createdValidRegistrationWithoutId());
    }

    @Test
    @DisplayName("Should return an Validation Error: Email or Name cannot be empty")
    public void shouldNotSaveEmptyFields() {
        RegistrationDTORequest registrationDTORequest = createdEmptyEmailAndNameRegistrationDTORequest();


        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RegistrationDTORequest>> violations = validator.validate(registrationDTORequest);


        Assertions.assertEquals(2,violations.size());

    }


    //************************************* getById()


    @Test
    @DisplayName("Should get a Registration by id")
    void getARegistrationByid() throws RegistrationNotFoundException {
        Long id = 1L;
        Registration registration = createdValidRegistrationWithoutId();
        registration.setId(1L);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));


        Optional<Registration> foundRegistration = registrationService.getById(id);


        assertThat(foundRegistration).isPresent();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getEmail()).isEqualTo(registration.getEmail());
        assertThat(foundRegistration.get().getRegistrationVersion()).isEqualTo(registration.getRegistrationVersion());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());

    }

    @Test
    @DisplayName("Should not get a registration because the id was not found")
    void shouldNotGetARegistrationById() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        String expectedMessage = "Não foi possível encontrar o registro com o id informado.";


        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.getById(Mockito.anyLong()));


        assertThat(e)
                .isInstanceOf(RegistrationNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    //*************************************  delete()

    @Test
    @DisplayName("Should delete a registration")
    void deleteRegistrationdWithSucces() {
        Long id = 1L;
        Registration registration = Registration.builder().id(id).build();
        Mockito.when(repository.existsByRegistration(registration)).thenReturn(true);
        Mockito.when(repository.existsById(id)).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> registrationService.delete(registration));
        Mockito.verify(repository,Mockito.times(1)).delete(registration);
    }

    @Test
    @DisplayName("Should thrown an exception because registration is null")
    void shouldNotDeleteRegistrationNull() {
        Registration registration = Registration.builder().build();
        String expectedMessage = "Registro ou registro_id não podem ser nulos!!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.delete(registration));


        assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).delete(registration);
    }

    @Test
    @DisplayName("Should thrown a exception because the Registration was not found.")
    void shouldNotDeleteRegistrationNotFound() {
        Long id = 1L;
        Registration registration = Registration.builder().id(id).build();
        Mockito.when(repository.existsByRegistration(registration)).thenReturn(false);
        String expectedMessage = "Registro não encontrado!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.delete(registration));


        assertThat(e)
                .isInstanceOf(RegistrationNotFoundException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.times(1)).existsByRegistration(registration);
        Mockito.verify(repository,Mockito.never()).delete(registration);

    }

    @Test
    @DisplayName("Should thrown a exception because the Registration was found but not deleted.")
    void shouldNotDeleteRegistrationFoundButNotDeleted() {
        Long id = 1L;
        Registration registration = Registration.builder().id(id).build();
        Mockito.when(repository.existsByRegistration(registration)).thenReturn(true);
        Mockito.when(repository.existsById(id)).thenReturn(true);
        String expectedMessage = "Não foi possível excluir o registro!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.delete(registration));


        assertThat(e)
                .isInstanceOf(RegistrationFoundButNotDeletedException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.times(1)).existsByRegistration(registration);
        Mockito.verify(repository,Mockito.times(1)).existsById(id);
        Mockito.verify(repository,Mockito.times(1)).delete(registration);
    }
    
    //*********************************** update

    @Test
    @DisplayName("Should update a registration with succes.")
    void updateRegistrationAlreadyExistsWithSucces() throws EmailAlreadyExistsException {
        Long id = 1L;
        RegistrationDTORequest receivedRegistration = RegistrationDTORequest.builder()
                .name("Amanda Santos")
                .email("amanda2@teste.com.br")
                .build();

        Registration expectedRegistration = Registration.builder()
                .id(id)
                .name(receivedRegistration.getName())
                .email(receivedRegistration.getEmail())
                .registrationVersion("002")
                .dateOfRegistration(LocalDate.now())
                .build();

        Mockito.when(repository.existsByEmail(receivedRegistration.getEmail())).thenReturn(false);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(createdValidRegistrationWithId()));
        Mockito.when(repository.save(expectedRegistration)).thenReturn(expectedRegistration);


        Registration updatedRegistration = registrationService.update(id, receivedRegistration);


        assertThat(id).isEqualTo(updatedRegistration.getId());
        assertThat(expectedRegistration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(expectedRegistration.getEmail()).isEqualTo(updatedRegistration.getEmail());
        assertThat(expectedRegistration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(expectedRegistration.getRegistrationVersion()).isEqualTo(updatedRegistration.getRegistrationVersion());
        Mockito.verify(repository,Mockito.times(1)).findById(id);
        Mockito.verify(repository,Mockito.times(1)).save(expectedRegistration);
    }

    @Test
    @DisplayName("Create a new Registration when si required to update a registration what doesn't exists.")
    void updateRegistrationDontExistsButWillBeCreatedWithSucces() throws EmailAlreadyExistsException {
        Long receveidId = 5L;
        Long newId = 2L;
        RegistrationDTORequest receivedRegistration = RegistrationDTORequest.builder()
                .name("Amanda Santos")
                .email("amanda2@teste.com.br")
                .build();

        Registration expectedRegistration = Registration.builder()
                .id(newId)
                .name(receivedRegistration.getName())
                .email(receivedRegistration.getEmail())
                .registrationVersion("002")
                .dateOfRegistration(LocalDate.now())
                .build();

        Mockito.when(repository.existsByEmail(receivedRegistration.getEmail())).thenReturn(false);
        Mockito.when(repository.findById(receveidId)).thenReturn(Optional.empty());
        Mockito.when(registrationService.save(receivedRegistration)).thenReturn(expectedRegistration);


        Registration updatedRegistration = registrationService.update(receveidId, receivedRegistration);


        assertThat(newId).isEqualTo(updatedRegistration.getId());
        assertThat(expectedRegistration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(expectedRegistration.getEmail()).isEqualTo(updatedRegistration.getEmail());
        assertThat(expectedRegistration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(expectedRegistration.getRegistrationVersion()).isEqualTo(updatedRegistration.getRegistrationVersion());
        Mockito.verify(repository,Mockito.times(1)).findById(receveidId);
    }

    @Test
    void shouldNotUpdateARegistrationWithAnDuplicatedEmail() throws EmailAlreadyExistsException {
        Long id = 1L;
        RegistrationDTORequest receivedRegistration = RegistrationDTORequest.builder()
                .name("Amanda Santos")
                .email("amanda@teste.com.br")
                .build();
        Registration registrationFoundByEmail = Registration.builder()
                .id(2L).build();

        Mockito.when(repository.existsByEmail(receivedRegistration.getEmail())).thenReturn(true);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(createdValidRegistrationWithId()));
        Mockito.when(repository.findByEmail(receivedRegistration.getEmail())).thenReturn(registrationFoundByEmail);
        String expectedMessage = "Já existe um usuário cadastrado com esse email.";


        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.update(id,receivedRegistration));


        assertThat(e)
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.times(1)).findById(id);
        Mockito.verify(repository,Mockito.times(1)).existsByEmail(receivedRegistration.getEmail());
        Mockito.verify(repository,Mockito.never()).save(createdValidRegistrationWithId());

    }

    private RegistrationDTORequest createdEmptyEmailAndNameRegistrationDTORequest() {
        return RegistrationDTORequest.builder()
                .name("")
                .email("")
                .build();
    }

    private Registration createdValidRegistrationWithId() {
        return Registration.builder()
                .id(1L)
                .name("Amanda Lima")
                .email("amanda@teste.com.br")
                .dateOfRegistration(LocalDate.now())
                .registrationVersion("001")
                .build();
    }


    private RegistrationDTORequest createdValidRegistrationDTORequest() {
        return RegistrationDTORequest.builder()
                .name("Amanda Lima")
                .email("amanda@teste.com.br")
                .build();
    }

    private Registration createdValidRegistrationWithoutId() {
        return Registration.builder()
                .name("Amanda Lima")
                .email("amanda@teste.com.br")
                .dateOfRegistration(LocalDate.now())
                .registrationVersion("001")
                .build();
    }
}
