package com.microservicemeetup.service;

import com.microservicemeetup.exceptions.RegistrationAlreadyExistsException;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;
import com.microservicemeetup.repository.RegistrationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    @MockBean
    RegistrationRepository repository;

    @BeforeEach
    public void setUp(){
        this.registrationService = new RegistrationServiceImpl(repository);
    }

    @Test
    @DisplayName("Shoud save an registration")
    public void saveRegistrationWithSucces() throws RegistrationAlreadyExistsException {

        //arrange
        RegistrationDTORequest registrationDTORequest = createdValidRegistrationDTORequest();
        Registration registration = createdValidRegistration();

        //act
        Mockito.when(repository.existsByNameAndEmail(Mockito.anyString(),Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createdValidRegistration());

        Registration savedRegistration = registrationService.save(registrationDTORequest);

        //assert
        assertThat(savedRegistration.getName()).isEqualTo("Amanda Lima");
        assertThat(savedRegistration.getEmail()).isEqualTo("amanda@teste.com.br");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        assertThat(savedRegistration.getRegistrationVersion()).isEqualTo("001");


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
