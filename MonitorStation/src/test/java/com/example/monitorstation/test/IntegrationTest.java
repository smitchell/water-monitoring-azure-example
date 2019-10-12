package com.example.monitorstation.test;

import com.example.monitorstation.domain.StationPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void testSimulationPreferences() throws Exception {
        StationPreferences preferences = new StationPreferences();
        preferences.setStationId("An Id");
        preferences.setName("A Name");
        preferences.setGatewayUrl("http://www.gateway.com");
        preferences.setIncrementValue(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP));
        preferences.setLat(BigDecimal.valueOf(39.099728D).setScale(6, RoundingMode.HALF_UP));
        preferences.setLon(BigDecimal.valueOf(-94.578568D).setScale(6, RoundingMode.HALF_UP));
        String json = new ObjectMapper().writeValueAsString(preferences);
        mockMvc.perform(put("/stationPreferences")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/stationPreferences")
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationId").value(equalTo(preferences.getStationId())))
                .andExpect(jsonPath("$.name").value(equalTo(preferences.getName())))
                .andExpect(jsonPath("$.gatewayUrl").value(equalTo(preferences.getGatewayUrl())));
    }
}
