package com.microservicemeetup.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicemeetup.controller.dto.MeetupDTORequest;
import com.microservicemeetup.controller.resource.RegistrationController;
import com.microservicemeetup.model.Meetup;
import com.microservicemeetup.model.Registration;
import com.microservicemeetup.service.MeetupService;
import com.microservicemeetup.service.RegistrationService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {MeetupControllerTest.class})
@AutoConfigureMockMvc
public class MeetupControllerTest {

    final static String MEETUP_API = "/api/meetup";

    @MockBean
    RegistrationService registrationService;

    @MockBean
    MeetupService meetupService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Should create a meetup and return a 201-Created Status")
    void shouldCreateAMeetupWithSucces() throws Exception {
        MeetupDTORequest dtoRequest = createMeetupDTORequest();
        String json = new ObjectMapper().writeValueAsString(dtoRequest);
        Meetup meetup = createMeetup();

        BDDMockito.when(meetupService.save(Mockito.any(Meetup.class))).thenReturn(meetup);
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
                .andExpect(jsonPath("registered").value(true))
                .andExpect(jsonPath("registrations").value(createListOfRegistrations().toString()))
                .andExpect(jsonPath("meetupDate").value(createMeetup().getMeetupDate().toString()));

    }

    private Meetup createMeetup() {
        Meetup meetup = Meetup.builder()
                .id(1L)
                .meetupDate(LocalDateTime.of(2022,05,10,19,00))
                .event("Alinhamento anual da liderança")
                .registrations(createListOfRegistrations())
                .build();

        return meetup;
    }

    private MeetupDTORequest createMeetupDTORequest() {
        MeetupDTORequest dtoRequest = MeetupDTORequest.builder()
                .meetupDate(LocalDateTime.of(2022,05,10,19,00))
                .event("Alinhamento anual da liderança")
                .registrationAttribute("liderança")
                .build();

        return dtoRequest;
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
