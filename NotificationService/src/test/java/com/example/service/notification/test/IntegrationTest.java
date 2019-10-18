package com.example.service.notification.test;

import com.example.service.notification.domain.NotificationPreference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddNotificationPreferences() throws Exception {
        NotificationPreference preference = TestHelper.generateNotificationPreference();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(preference);
        MvcResult create = mockMvc.perform(post("/api/v1/notificationPreferences")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();

        String uuid = parseIdFromLocation(create.getResponse().getHeader("Location"));

        mockMvc.perform(get("/api/v1/notificationPreferences/".concat(uuid))
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", equalTo(preference.getEmailAddress())))
                .andExpect(jsonPath("$.mobileNumber", equalTo(preference.getMobileNumber())))
                .andExpect(jsonPath("$.county", equalTo(preference.getCounty())))
                .andExpect(jsonPath("$.state", equalTo(preference.getState())))
                .andExpect(jsonPath("$.postal", equalTo(preference.getPostal())))
                .andExpect(jsonPath("$.emailEnabled", equalTo(preference.isEmailEnabled())))
                .andExpect(jsonPath("$.smsEnabled", equalTo(preference.isSmsEnabled())));

        mockMvc.perform(get("/api/v1/notificationPreferences/search/findByEmailAddress?emailAddress=".concat(preference.getEmailAddress()))
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", equalTo(preference.getEmailAddress())));


        mockMvc.perform(get("/api/v1/notificationPreferences/search/findByMobileNumber?mobileNumber=".concat(preference.getMobileNumber()))
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber", equalTo(preference.getMobileNumber())));
    }

    @Test
    public void testUpdateNotificationPreferences() throws Exception {
        NotificationPreference preference = TestHelper.generateNotificationPreference();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(preference);
        MvcResult create = mockMvc.perform(post("/api/v1/notificationPreferences")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();

        String uuid = parseIdFromLocation(create.getResponse().getHeader("Location"));

        String newEmailAddress = "new-email@sink.sendgrid.net";
        preference.setId(uuid);
        preference.setEmailAddress(newEmailAddress);
        json = mapper.writeValueAsString(preference);

        mockMvc.perform(put("/api/v1/notificationPreferences/".concat(uuid))
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().is2xxSuccessful()).andReturn();

        mockMvc.perform(get("/api/v1/notificationPreferences/".concat(uuid))
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", equalTo(newEmailAddress)));
    }

    @Test
    public void testGetId() {
        String baseURL = "http://localhost/api/v1/notificationPreferences/";
        String uuid = UUID.randomUUID().toString();
        String s = parseIdFromLocation(baseURL.concat(uuid));
        assertThat(s, equalTo(uuid));
    }

    /**
     * The part of the location after the 6th slash
     * @param s The location string (e.g., http://localhost/api/v1/notificationPreferences/bf84211a-b430-462b-93d7-7de5467d0c95)
     * @return The UUID (e.g., bf84211a-b430-462b-93d7-7de5467d0c95)
     */
    private String parseIdFromLocation(String s) {
        return s.split("/")[6];
    }
}
