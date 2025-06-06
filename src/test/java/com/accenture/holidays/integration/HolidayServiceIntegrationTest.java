package com.accenture.holidays.integration;

import com.accenture.holidays.application.controller.HolidayController;
import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.model.Holiday;
import com.accenture.holidays.domain.usecase.HolidayUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HolidayController.class)
class HolidayServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HolidayUseCase holidayUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMostRecentHolidays_ShouldReturnHolidays() throws Exception {
        List<Holiday> mockHolidays = Arrays.asList(
                new Holiday(
                        LocalDate.of(2025, 4, 20),
                        "LocalName",
                        "Christmas",
                        "NL",
                        false,
                        false,
                        new String[]{},
                        2000,
                        new String[]{}
                )
        );

        when(holidayUseCase.getMostRecentHolidays("NL",3)).thenReturn(mockHolidays);

        mockMvc.perform(get("/api/holidays/most-recent/NL/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].name", is("Christmas")));
    }

    @Test
    void getHolidaysNotOnWeekends_ShouldReturnCounts() throws Exception {
        Map<String, Long> mockResponse = Map.of("NL", 10L, "GB", 8L);
        when(holidayUseCase.getHolidaysNotOnWeekends(2025, Arrays.asList("NL", "GB"))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/holidays/not-weekends?year=2025&countryCodes=NL,GB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.NL", is(10)))
                .andExpect(jsonPath("$.GB", is(8)));
    }

    @Test
    void getCommonHolidays_ShouldReturnCommonHolidays() throws Exception {
        List<CommonHolidayInfo> commonHolidays = List.of(new CommonHolidayInfo(LocalDate.of(2025, 4, 20),"Easter","Easter"));

        when(holidayUseCase.getCommonHolidays(2025, "NL", "GB")).thenReturn(commonHolidays);

        mockMvc.perform(get("/api/holidays/common?year=2025&countryCode1=NL&countryCode2=GB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].localName1", is("Easter")));
    }

    @Test
    void getMostRecentHolidays_ShouldReturnBadRequest_WhenCountryCodeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/holidays/most-recent/N/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getHolidaysNotOnWeekends_ShouldReturnBadRequest_WhenYearIsInvalid() throws Exception {
        mockMvc.perform(get("/api/holidays/not-weekends?year=1999&countryCodes=NL,GB"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getCommonHolidays_ShouldReturnBadRequest_WhenCountryCodeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/holidays/common?year=2025&countryCode1=n&countryCode2=GB"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
