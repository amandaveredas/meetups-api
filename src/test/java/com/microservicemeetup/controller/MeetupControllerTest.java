package com.microservicemeetup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicemeetup.controller.dto.MeetupDTORequest;
import com.microservicemeetup.controller.resource.MeetupController;
import com.microservicemeetup.exceptions.DuplicatedMeetupException;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.service.MeetupService;
import com.microservicemeetup.service.RegistrationService;
import org.hamcrest.Matchers;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {MeetupController.class})
@AutoConfigureMockMvc
public class MeetupControllerTest {

    final static String MEETUP_API = "/api/meetup";

    @MockBean
    RegistrationService registrationService;

    @MockBean
    MeetupService service;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Should create a meetup and return a 201-Created Status")
    void shouldCreateAMeetupWithSucces() throws Exception {
        MeetupDTORequest dtoRequest = createMeetupDTORequest();
        String json = new ObjectMapper().writeValueAsString(dtoRequest);
        Meetup meetup = createMeetup();

        BDDMockito.given(service.save(any(Meetup.class))).willReturn(meetup);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MEETUP_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("event").value("Alinhamento anual da liderança"))
                .andExpect(jsonPath("meetupDate").value("10-05-2022 19:00:00"))
                .andExpect(jsonPath("registrationAttribute").value("liderança"));
    }

    @Test
    @DisplayName("Should Return A Bad Request Status (400) When Try To Create A Duplicated Metup")
    void shouldReturnABadRequest_WhenTryToCreateADuplicatedMetup() throws Exception {
        MeetupDTORequest dtoRequest = createMeetupDTORequest();
        String json = new ObjectMapper().writeValueAsString(dtoRequest);

        BDDMockito.given(service.save(any(Meetup.class))).willThrow(DuplicatedMeetupException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MEETUP_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isBadRequest());

    }

    //********************************** get

    @Test
    @DisplayName("Should filter meetup")
    public void shouldFindRegistrations_whenAFilterIsGave() throws Exception {
        Meetup meetup = createMeetup();

        BDDMockito.given(service.find(Mockito.any(Meetup.class), Mockito.any(Pageable.class)) )
                .willReturn(new PageImpl<Meetup>(Arrays.asList(meetup), PageRequest.of(0,100), 1));


        String queryString = String.format("?event=%s&registrationAttribute=%s&page=0&size=100",
                meetup.getEvent(), meetup.getRegistrationAttribute());


        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(MEETUP_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements"). value(1))
                .andExpect(jsonPath("pageable.pageSize"). value(100))
                .andExpect(jsonPath("pageable.pageNumber"). value(0));

    }

    private Meetup createMeetup() {
        return Meetup.builder()
                .id(1L)
                .meetupDate(LocalDateTime.of(2022,5,10,19,0))
                .event("Alinhamento anual da liderança")
                .registrationAttribute("liderança")
                .registrations(createListOfRegistrations())
                .build();
    }

    private MeetupDTORequest createMeetupDTORequest() {

        return MeetupDTORequest.builder()
                .meetupDate(LocalDateTime.of(2022,5,10,19,0))
                .event("Alinhamento anual da liderança")
                .registrationAttribute("liderança")
                .build();
    }

    private List<Registration> createListOfRegistrations(){
        Registration registration1 = Registration.builder()
                .id(1L)
                .name("Amanda")
                .email("amanda@teste")
                .registrationAttribute("liderança")
                .dateOfRegistration(LocalDate.now())
                .build();

        Registration registration2 = Registration.builder()
                .id(2L)
                .name("Roni")
                .email("roni@teste")
                .registrationAttribute("liderança")
                .dateOfRegistration(LocalDate.now())
                .build();

        return Arrays.asList(registration1,registration2);

    }
}
