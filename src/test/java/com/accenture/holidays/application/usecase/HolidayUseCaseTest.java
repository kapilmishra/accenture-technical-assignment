package com.accenture.holidays.application.usecase;

import com.accenture.holidays.domain.gateway.HolidayApiClient;
import com.accenture.holidays.domain.exception.HolidayApiException;
import com.accenture.holidays.domain.model.Holiday;
import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.usecase.HolidayUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayUseCaseTest {

    @Mock
    private HolidayApiClient apiClient;

    private HolidayUseCase holidayUseCase;

    @BeforeEach
    void setUp() {
        holidayUseCase = new HolidayUseCaseImpl(apiClient);
    }

    @Test
    void getMostRecentHolidays_ShouldReturnLastNHolidays() throws HolidayApiException {
        // Arrange
        String countryCode = "NL";
        int count = 2;
        LocalDate today = LocalDate.now();
        Holiday[] holidays = {
            createHoliday(today.minusDays(1), "Yesterday"),
            createHoliday(today.minusDays(2), "Day Before"),
            createHoliday(today.minusDays(3), "Three Days Ago")
        };
        when(apiClient.fetchHolidaysByCountry(anyInt(), eq(countryCode))).thenReturn(holidays);

        // Act
        List<Holiday> result = holidayUseCase.getMostRecentHolidays(countryCode, count);

        // Assert
        assertEquals(count, result.size());
        assertEquals("Yesterday", result.get(0).getLocalName());
        assertEquals("Day Before", result.get(1).getLocalName());
    }

    @Test
    void getHolidaysNotOnWeekends_ShouldReturnCorrectCount() throws HolidayApiException {
        // Arrange
        int year = 2025;
        List<String> countryCodes = Arrays.asList("NL", "GB");
        Holiday[] nlHolidays = {
            createHoliday(LocalDate.of(2025, 1, 1), "New Year"), // Wednesday
            createHoliday(LocalDate.of(2025, 1, 4), "Weekend"), // Saturday
            createHoliday(LocalDate.of(2025, 1, 5), "Weekend")  // Sunday
        };
        Holiday[] gbHolidays = {
            createHoliday(LocalDate.of(2025, 1, 1), "New Year"), // Monday
            createHoliday(LocalDate.of(2025, 1, 4), "Weekend")  // Saturday
        };
        when(apiClient.fetchHolidaysByCountry(year, "NL")).thenReturn(nlHolidays);
        when(apiClient.fetchHolidaysByCountry(year, "GB")).thenReturn(gbHolidays);

        // Act
        Map<String, Long> result = holidayUseCase.getHolidaysNotOnWeekends(year, countryCodes);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get("NL"));
        assertEquals(1L, result.get("GB"));
    }

    @Test
    void getCommonHolidays_ShouldReturnCommonHolidays() throws HolidayApiException {
        // Arrange
        int year = 2025;
        String countryCode1 = "NL";
        String countryCode2 = "GB";
        LocalDate commonDate = LocalDate.of(2025, 1, 1);
        Holiday[] nlHolidays = {
            createHoliday(commonDate, "New Year NL"),
            createHoliday(LocalDate.of(2025, 1, 2), "NL Only")
        };
        Holiday[] gbHolidays = {
            createHoliday(commonDate, "New Year GB"),
            createHoliday(LocalDate.of(2025, 1, 3), "GB Only")
        };
        when(apiClient.fetchHolidaysByCountry(year, countryCode1)).thenReturn(nlHolidays);
        when(apiClient.fetchHolidaysByCountry(year, countryCode2)).thenReturn(gbHolidays);

        // Act
        List<CommonHolidayInfo> result = holidayUseCase.getCommonHolidays(year, countryCode1, countryCode2);

        // Assert
        assertEquals(1, result.size());
        CommonHolidayInfo commonHoliday = result.get(0);
        assertEquals(commonDate, commonHoliday.date());
        assertEquals("New Year NL", commonHoliday.localName1());
        assertEquals("New Year GB", commonHoliday.localName2());
    }

    @Test
    void getMostRecentHolidays_ShouldHandleApiException() throws HolidayApiException {
        // Arrange
        String countryCode = "NL";
        when(apiClient.fetchHolidaysByCountry(anyInt(), eq(countryCode)))
            .thenThrow(new HolidayApiException("API is not reachable", new RuntimeException()));

        // Act & Assert
        assertDoesNotThrow(() -> {
            List<Holiday> result = holidayUseCase.getMostRecentHolidays(countryCode, 3);
            assertTrue(result.isEmpty());
        });
    }

    private Holiday createHoliday(LocalDate date, String name) {
        Holiday holiday = new Holiday();
        holiday.setDate(date);
        holiday.setName(name);
        holiday.setLocalName(name);
        holiday.setCountryCode("NL");
        return holiday;
    }
} 