package com.microservicemeetup.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicemeetup.exception.EmailAlreadyExistsException;
import com.microservicemeetup.exception.RegistrationNotFoundException;
import com.microservicemeetup.model.entity.Registration;
import com.microservicemeetup.model.dto.RegistrationDTORequest;
import com.microservicemeetup.service.RegistrationService;
import com.microservicemeetup.service.RegistrationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    final static String REGISTRATION_API = "/api/registration";

    @MockBean
    RegistrationService service;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Should create an registration with succes.")
    public void createRegistrationWithSucces() throws Exception {

        RegistrationDTORequest dtoRequest = createDTORequest();
        Registration created = createRegistration();
        String json = new ObjectMapper().writeValueAsString(dtoRequest);

        BDDMockito.given(service.save(any(Registration.class))).willReturn(created);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Amanda Lima"))
                .andExpect(jsonPath("email").value("amanda@teste.com"))
                .andExpect(jsonPath("dateOfRegistration").value(LocalDate.now().toString()))
                .andExpect(jsonPath("registrationVersion").value("001"));


    }

    @Test
    @DisplayName("Should return a BadRequest Status because of duplicated email.")
    void dontCreatARegistrationWithDuplicatedEmail() throws Exception {
        RegistrationDTORequest dtoRequest = createDTORequest();
        Registration registration = Registration.builder()
                .name(dtoRequest.getName())
                .email(dtoRequest.getEmail())
                .build();
        String json = new ObjectMapper().writeValueAsString(dtoRequest);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        BDDMockito.given(service.save(registration)).willThrow(EmailAlreadyExistsException.class);

        mockMvc
                .perform(request)
                .andExpect(status().isBadRequest());
    }


    //********************************** get by id


    @Test
    @DisplayName("Should get a Registration with succes")
    void getRegistrationByIdWithSuccess() throws Exception {
        Long id = 1L;
        Registration foundRegistration = createRegistration();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(foundRegistration));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(foundRegistration.getId()))
                .andExpect(jsonPath("name").value(foundRegistration.getName()))
                .andExpect(jsonPath("email").value(foundRegistration.getEmail()))
                .andExpect(jsonPath("dateOfRegistration").value(foundRegistration.getDateOfRegistration().toString()))
                .andExpect(jsonPath("registrationVersion").value(foundRegistration.getRegistrationVersion()));
    }

    @Test
    @DisplayName("Should return a NotFound Status because of the Registration wasn't found.")
    void notFoundARegistrationById() throws Exception {
        Long id = 1L;

        BDDMockito.given(service.getById(id)).willThrow(RegistrationNotFoundException.class);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    //********************************** get

    //********************************** delete


    @Test
    @DisplayName("Should delete a registration by id and return No Content Status")
    void deleteRegistrationWithSucces() throws Exception {
        Long id = 1L;
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(createRegistration()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Should return a NotFound Status because of the Registration wasn't found.")
    void notFoundARegistrationDeleteById() throws Exception {
        Long id = 1L;
        BDDMockito.given(service.getById(id)).willThrow(RegistrationNotFoundException.class);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }
//********************************************* update


    @Test
    @DisplayName("Should update and registration with succes")
    void updatedRegistrationWithSucces() throws Exception {
        Long id = 1L;

        RegistrationDTORequest dtoRequest = RegistrationDTORequest.builder()
                .name("Amanda Lima Santos")
                .email("amanda2@teste.com")
                .build();

        String json = new ObjectMapper().writeValueAsString(dtoRequest);


        Registration updatedRegistration = Registration.builder()
                .id(id)
                .name("Amanda Lima Santos")
                .dateOfRegistration(LocalDate.now())
                .email("amanda2@teste.com")
                .registrationVersion("002").build();

        BDDMockito.given(service.update(eq(id),any(Registration.class))).willReturn(updatedRegistration);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/"+id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Amanda Lima Santos"))
                .andExpect(jsonPath("email").value("amanda2@teste.com"))
                .andExpect(jsonPath("dateOfRegistration").value(LocalDate.now().toString()))
                .andExpect(jsonPath("registrationVersion").value("002"));

    }

    @Test
    @DisplayName("Should return a BadRequest Status because of duplicated email.")
    void dontUpdateARegistrationWithDuplicatedEmail() throws Exception {
        Long id = 1L;

        RegistrationDTORequest dtoRequest = RegistrationDTORequest.builder()
                .name("Amanda Lima Santos")
                .email("amanda2@teste.com")
                .build();

        String json = new ObjectMapper().writeValueAsString(dtoRequest);

        BDDMockito.given(service.update(eq(id),any(Registration.class))).willThrow(EmailAlreadyExistsException.class);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/"+id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isBadRequest());

    }


    private RegistrationDTORequest createDTORequestWithEmptyName() {
        return RegistrationDTORequest.builder()
                .name("")
                .email("amanda@teste.com")
                .build();
    }

    private RegistrationDTORequest createDTORequestWithEmptyEmail() {
        return RegistrationDTORequest.builder()
                .name("Amanda Lima")
                .email("")
                .build();
    }

    private Registration createRegistration() {
        return Registration.builder()
                .id(1L)
                .name("Amanda Lima")
                .email("amanda@teste.com")
                .dateOfRegistration(LocalDate.now())
                .registrationVersion("001")
                .build();
    }

    private RegistrationDTORequest createDTORequest() {
        return RegistrationDTORequest.builder()
                .name("Amanda Lima")
                .email("amanda@teste.com")
                .build();
    }
}
