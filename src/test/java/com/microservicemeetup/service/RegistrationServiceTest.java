package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.EmailAlreadyExistsException;
import com.microservicemeetup.exceptions.RegistrationNotFoundById;
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

    @Test
    @DisplayName("Should save an registration")
    public void saveRegistrationWithSucces() throws EmailAlreadyExistsException {

        //arrange
        RegistrationDTORequest registrationDTORequest = createdValidRegistrationDTORequest();
        Registration registration = createdValidRegistration();


        //act
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createdValidRegistrationWithId());

        Registration savedRegistration = registrationService.save(registrationDTORequest);

        //assert
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
        Mockito.verify(repository,Mockito.never()).save(createdValidRegistration());
    }

    @Test
    @DisplayName("Should return an Validation Error: Email or Name cannot be empty")
    public void shouldNotSaveEmptyFields() {
        RegistrationDTORequest registrationDTORequest = createdEmptyEmailAndNameRegistrationDTORequest();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RegistrationDTORequest>> violations = validator.validate(registrationDTORequest);

        Assertions.assertEquals(2,violations.size());

    }

    @Test
    @DisplayName("Shoul get a Registration by id")
    void getARegistrationByid() throws RegistrationNotFoundById {
        Long id = 1L;
        Registration registration = createdValidRegistration();
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
    void shouldNotGetARegistrationById() throws RegistrationNotFoundById {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        String expectedMessage = "Não foi possível encontrar o registro com o id informado.";

        Throwable e = org.assertj.core.api.Assertions.catchThrowable(() -> registrationService.getById(Mockito.anyLong()));

        assertThat(e)
                .isInstanceOf(RegistrationNotFoundById.class)
                .hasMessage(expectedMessage);
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

    private Registration createdValidRegistration() {
        return Registration.builder()
                .name("Amanda Lima")
                .email("amanda@teste.com.br")
                .dateOfRegistration(LocalDate.now())
                .registrationVersion("001")
                .build();
    }
}
