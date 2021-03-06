package com.microservicemeetup.service.registration;

import com.microservicemeetup.controller.dto.registration.RegistrationDTORequest;
import com.microservicemeetup.exceptions.registration.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.registration.RegistrationNotFoundException;
import com.microservicemeetup.model.Registration;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

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
    public void shouldSaveARegistrationWithSuccess_whenAllTheRequirementsAreSatisfied() throws Exception {

        Registration registration = createdValidRegistrationWithoutId();
        Mockito.when(registrationService.verifyIfExistsByEmail(registration)).thenReturn(false);

        Mockito.when(repository.save(registration)).thenReturn(createdValidRegistrationWithId());

        Registration savedRegistration = registrationService.save(registration);


        assertThat(savedRegistration.getId()).isEqualTo(1L);
        assertThat(savedRegistration.getName()).isEqualTo("Amanda Lima");
        assertThat(savedRegistration.getEmail()).isEqualTo("amanda@teste.com.br");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        assertThat(savedRegistration.getRegistrationAttribute()).isEqualTo("001");
    }

    @Test
    @DisplayName("Should return an EmailAlreadyExistsException")
    public void shouldNotSaveRegistration_whenItHasADuplicatedEmail() throws Exception {
        Registration registration = createdValidRegistrationWithoutId();
        String expectedMessage = "J?? existe um registro cadastrado com esse email.";
        Mockito.when(registrationService.verifyIfExistsByEmail(registration)).thenThrow(new EmailAlreadyExistsException());

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.save(registration));

        assertThat(e)
                .isInstanceOf(EmailAlreadyExistsException.class)
                        .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).save(createdValidRegistrationWithoutId());
    }

    @Test
    @DisplayName("Should return an Validation Error: Fields cannot be empty")
    public void shouldNotSaveRegistration_whenItsHasEmptyFields() {
        RegistrationDTORequest registrationDTORequest = createdEmptyFieldsRegistrationDTORequest();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RegistrationDTORequest>> violations = validator.validate(registrationDTORequest);

        Assertions.assertEquals(3,violations.size());

    }


    //************************************* getById()


    @Test
    @DisplayName("Should get a Registration by id")
    void shouldGetARegistrationById_whenThisRegistrationExists() throws RegistrationNotFoundException {
        Long id = 1L;
        Registration registration = createdValidRegistrationWithoutId();
        registration.setId(1L);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));


        Optional<Registration> foundRegistration = registrationService.getById(id);

        assertThat(foundRegistration).isPresent();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getEmail()).isEqualTo(registration.getEmail());
        assertThat(foundRegistration.get().getRegistrationAttribute()).isEqualTo(registration.getRegistrationAttribute());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());

    }

    @Test
    @DisplayName("Should not get a registration because the id was not found")
    void shouldNotGetARegistrationById_whenThisRegistrationDontExists() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        String expectedMessage = "N??o foi poss??vel encontrar o registration com o id: 0.";


        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.getById(Mockito.anyLong()));


        assertThat(e)
                .isInstanceOf(RegistrationNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should throw illegal argument exception because id is null when try to get")
    void shouldThrownAnExceptionAtGetById_whenIdRegistrationIsNull() {
        Long id = null;
        String expectedMessage = "Id n??o pode ser nulo!!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.getById(id));

        assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }


    //*************************************  delete()

    @Test
    @DisplayName("Should delete a registration")
    void shouldDeleteARegistrationWithSuccess_whenThisRegistrationExists() throws RegistrationNotFoundException {
        Long id = 1L;
        Registration registration = Registration.builder().id(id).build();
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(registration));
        Mockito.when(registrationService.getById(id)).thenReturn(Optional.of(registration));

        Assertions.assertDoesNotThrow(() -> registrationService.delete(id));
        Mockito.verify(repository,Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should thrown an exception because registration is null when try to delete")
    void shouldThrownAnExceptionAtDeleteById_whenIdRegistrationIsNull() {
        Registration registration = Registration.builder().build();
        String expectedMessage = "Id n??o pode ser nulo!!";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.delete(registration.getId()));


        assertThat(e)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).delete(registration);
    }

    @Test
    @DisplayName("Should thrown a exception because the Registration was not found.")
    void shouldNotDeleteARegistrationWithSuccess_whenThisRegistrationDontExists() {
        Long id = 1L;
        Registration registration = Registration.builder().id(id).build();
        String expectedMessage = "N??o foi poss??vel encontrar o registration com o id: 1.";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.delete(id));

        assertThat(e)
                .isInstanceOf(RegistrationNotFoundException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.never()).delete(registration);

    }
    
    //*********************************** update

    @Test
    @DisplayName("Should update a registration with succes.")
    void shouldUpdateARegistration_whenThisRegistrationExists() throws EmailAlreadyExistsException {
        Long id = 1L;
        Registration receivedRegistration = Registration.builder()
                .name("Amanda Santos")
                .email("amanda2@teste.com.br")
                .registrationAttribute("002")
                .build();


        Registration expectedRegistration = Registration.builder()
                .id(id)
                .name(receivedRegistration.getName())
                .email(receivedRegistration.getEmail())
                .registrationAttribute(receivedRegistration.getRegistrationAttribute())
                .dateOfRegistration(LocalDate.now())
                .build();

        Mockito.when(registrationService.verifyDuplicatedEmailWhenTryToUpdate(id,receivedRegistration)).thenReturn(false);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(createdValidRegistrationWithId()));
        Mockito.when(repository.save(expectedRegistration)).thenReturn(expectedRegistration);


        Registration updatedRegistration = registrationService.update(id, receivedRegistration);


        assertThat(id).isEqualTo(updatedRegistration.getId());
        assertThat(expectedRegistration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(expectedRegistration.getEmail()).isEqualTo(updatedRegistration.getEmail());
        assertThat(expectedRegistration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(expectedRegistration.getRegistrationAttribute()).isEqualTo(updatedRegistration.getRegistrationAttribute());
        Mockito.verify(repository,Mockito.times(1)).findById(id);
        Mockito.verify(repository,Mockito.times(1)).save(expectedRegistration);
    }

    @Test
    @DisplayName("Create a new Registration when is required to update a registration that don't exists.")
    void shouldCreateARegistration_whenTryToUpdateARegistrationThatDontExists() throws EmailAlreadyExistsException {
        Long newId = 2L;
        Registration receivedRegistration = Registration.builder()
                .name("Amanda Santos")
                .email("amanda2@teste.com.br")
                .build();

        Registration expectedRegistration = Registration.builder()
                .id(newId)
                .name(receivedRegistration.getName())
                .email(receivedRegistration.getEmail())
                .registrationAttribute("002")
                .dateOfRegistration(LocalDate.now())
                .build();

        Mockito.when(repository.existsByEmail(receivedRegistration.getEmail())).thenReturn(false);
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(registrationService.save(receivedRegistration)).thenReturn(expectedRegistration);


        Registration updatedRegistration = registrationService.update(anyLong(), receivedRegistration);


        assertThat(newId).isEqualTo(updatedRegistration.getId());
        assertThat(expectedRegistration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(expectedRegistration.getEmail()).isEqualTo(updatedRegistration.getEmail());
        assertThat(expectedRegistration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(expectedRegistration.getRegistrationAttribute()).isEqualTo(updatedRegistration.getRegistrationAttribute());
        Mockito.verify(repository,Mockito.times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should thrown an exception when try to update with a duplicated email.")
    void shouldNotUpdateARegistration_whenItHasADuplicatedEmail() throws EmailAlreadyExistsException {
        Long id = 1L;
        Registration receivedRegistration = Registration.builder()
                .name("Amanda Santos")
                .email("amanda@teste.com.br")
                .build();
        Registration registrationFoundByEmail = Registration.builder()
                .id(2L).build();

        Mockito.when(registrationService.verifyDuplicatedEmailWhenTryToUpdate(id,receivedRegistration)).thenThrow(new EmailAlreadyExistsException());
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(createdValidRegistrationWithId()));
        String expectedMessage = "J?? existe um registro cadastrado com esse email.";


        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.update(id,receivedRegistration));


        assertThat(e)
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(expectedMessage);
        Mockito.verify(repository,Mockito.times(1)).findById(id);
        Mockito.verify(repository,Mockito.times(1)).existsByEmail(receivedRegistration.getEmail());
        Mockito.verify(repository,Mockito.never()).save(createdValidRegistrationWithId());

    }

    //*********************************** findAll

    @Test
    @DisplayName("Should return a page with all matches with filter")
    void shouldFindRegistrations_whenAFilterIsGave() {
        Registration registration = createdValidRegistrationWithId();
        PageRequest pageRequest = PageRequest.of(0,10);
        List<Registration> registrations = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration),
                PageRequest.of(0,10),1);

        Mockito.when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        Page<Registration> result = registrationService.find(registration,pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(registrations);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private RegistrationDTORequest createdEmptyFieldsRegistrationDTORequest() {
        return RegistrationDTORequest.builder()
                .name("")
                .email("")
                .registrationAttribute("")
                .build();
    }

    private Registration createdValidRegistrationWithId() {
        return Registration.builder()
                .id(1L)
                .name("Amanda Lima")
                .email("amanda@teste.com.br")
                .dateOfRegistration(LocalDate.now())
                .registrationAttribute("001")
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
                .registrationAttribute("001")
                .build();
    }
}
