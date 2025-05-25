package com.accenture.holidays.infrastructure.config;

import com.accenture.holidays.domain.model.Holiday;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.mockito.Mockito;

import java.time.LocalDate;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        
        Holiday[] mockHolidays = {
            createHoliday(LocalDate.of(2025, 1, 1), "New Year's Day"),
            createHoliday(LocalDate.of(2025, 12, 25), "Christmas Day")
        };
        
        Mockito.when(restTemplate.getForObject(
            Mockito.contains("/PublicHolidays/2025/NL"),
            Mockito.eq(Holiday[].class)
        )).thenReturn(mockHolidays);

        Mockito.when(restTemplate.getForObject(
            Mockito.contains("/PublicHolidays/2025/GB"),
            Mockito.eq(Holiday[].class)
        )).thenReturn(mockHolidays);

        // Mock error response for invalid requests
        Mockito.when(restTemplate.getForObject(
            Mockito.contains("/PublicHolidays/1999/"),
            Mockito.eq(Holiday[].class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        return restTemplate;
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