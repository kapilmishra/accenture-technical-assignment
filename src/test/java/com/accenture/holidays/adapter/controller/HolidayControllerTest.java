package com.accenture.holidays.adapter.controller;

import com.accenture.holidays.application.controller.HolidayController;
import com.accenture.holidays.domain.usecase.HolidayUseCase;
import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.model.Holiday;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayControllerTest {

    @Mock
    private HolidayUseCase holidayService;

    private HolidayController holidayController;

    @BeforeEach
    void setUp() {
        holidayController = new HolidayController(holidayService);
    }

    @Test
    void getLastNHolidays_ShouldReturnNHolidays() {
        // Arrange
        String countryCode = "NL";
        int count = 3;
        List<Holiday> expectedHolidays = Arrays.asList(
            createHoliday(LocalDate.now().minusDays(1), "Yesterday"),
            createHoliday(LocalDate.now().minusDays(2), "Day Before"),
            createHoliday(LocalDate.now().minusDays(3), "Three Days Ago")
        );
        when(holidayService.getMostRecentHolidays(countryCode, count)).thenReturn(expectedHolidays);

        // Act
        List<Holiday> result = holidayController.getMostRecentHolidays(countryCode, count);

        // Assert
        assertNotNull(result);
        assertEquals(count, result.size());
        verify(holidayService).getMostRecentHolidays(countryCode, count);
    }

    @Test
    void getHolidaysNotOnWeekends_ShouldReturnCounts() {
        // Arrange
        int year = 2025;
        List<String> countryCodes = Arrays.asList("NL", "GB");
        Map<String, Long> expectedCounts = Map.of(
            "NL", 5L,
            "GB", 3L
        );
        when(holidayService.getHolidaysNotOnWeekends(year, countryCodes)).thenReturn(expectedCounts);

        // Act
        Map<String, Long> result = holidayController.getHolidaysNotOnWeekends(year, countryCodes);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5L, result.get("NL"));
        assertEquals(3L, result.get("GB"));
        verify(holidayService).getHolidaysNotOnWeekends(year, countryCodes);
    }

    @Test
    void getCommonHolidays_ShouldReturnCommonHolidays() {
        // Arrange
        int year = 2025;
        String countryCode1 = "NL";
        String countryCode2 = "GB";
        List<CommonHolidayInfo> expectedHolidays = Arrays.asList(
            new CommonHolidayInfo(
                LocalDate.of(2025, 1, 1),
                "New Year NL",
                "New Year GB"
            )
        );
        when(holidayService.getCommonHolidays(year, countryCode1, countryCode2))
            .thenReturn(expectedHolidays);

        // Act
        List<CommonHolidayInfo> result = holidayController.getCommonHolidays(
            year, countryCode1, countryCode2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CommonHolidayInfo commonHoliday = result.get(0);
        assertEquals(LocalDate.of(2025, 1, 1), commonHoliday.date());
        assertEquals("New Year NL", commonHoliday.localName1());
        assertEquals("New Year GB", commonHoliday.localName2());
        verify(holidayService).getCommonHolidays(year, countryCode1, countryCode2);
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