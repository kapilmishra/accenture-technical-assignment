package com.accenture.holidays.application.util;

import com.accenture.holidays.domain.gateway.HolidayApiClient;
import com.accenture.holidays.domain.exception.HolidayApiException;
import com.accenture.holidays.domain.model.Holiday;
import com.accenture.holidays.infrastructure.adapter.HolidayApiClientImpl;
import com.accenture.holidays.infrastructure.config.HolidayApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HolidayApiProperties apiProperties;

    private HolidayApiClient apiClient;

    @BeforeEach
    void setUp() {
        when(apiProperties.getBaseUrl()).thenReturn("https://test-api.example.com");
        apiClient = new HolidayApiClientImpl(restTemplate, apiProperties);
    }

    @Test
    void fetchHolidaysByCountry_ShouldReturnHolidays() {
        // Arrange
        int year = 2025;
        String countryCode = "NL";
        Holiday[] expectedHolidays = {
            createHoliday("2025-01-01", "New Year's Day"),
            createHoliday("2025-12-25", "Christmas Day")
        };
        when(restTemplate.getForObject(anyString(), eq(Holiday[].class)))
            .thenReturn(expectedHolidays);

        // Act
        Holiday[] result = apiClient.fetchHolidaysByCountry(year, countryCode);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("New Year's Day", result[0].getName());
        assertEquals("Christmas Day", result[1].getName());
        verify(restTemplate).getForObject(
            "https://test-api.example.com/PublicHolidays/2025/NL",
            Holiday[].class
        );
    }

    @Test
    void fetchHolidaysByCountry_ShouldThrowException_WhenApiCallFails() {
        // Arrange
        int year = 2025;
        String countryCode = "NL";
        when(restTemplate.getForObject(anyString(), eq(Holiday[].class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));


        // Act & Assert
        assertThrows(HolidayApiException.class, () ->
            apiClient.fetchHolidaysByCountry(year, countryCode)
        );
    }

    private Holiday createHoliday(String date, String name) {
        Holiday holiday = new Holiday();
        holiday.setDate(java.time.LocalDate.parse(date));
        holiday.setName(name);
        holiday.setLocalName(name);
        holiday.setCountryCode("NL");
        return holiday;
    }
} 