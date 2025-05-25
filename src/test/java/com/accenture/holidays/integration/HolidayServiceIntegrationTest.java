package com.accenture.holidays.integration;

import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.model.Holiday;
import com.accenture.holidays.infrastructure.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class HolidayServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL= "http://localhost:%d/api/holidays/";
    @Test
    void getMostRecentHolidays_ShouldReturnHolidays() {
        // Arrange
        String countryCode = "NL";
        int count = 3;
        String url = String.format(BASE_URL+"most-recent/%s/%d", port, countryCode, count);

        // Act
        ResponseEntity<Holiday[]> response = restTemplate.getForEntity(url, Holiday[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length <= count);
    }

    @Test
    void getHolidaysNotOnWeekends_ShouldReturnCounts() {
        // Arrange
        int year = 2025;
        String countryCodes = "NL,GB";
        String url = String.format(BASE_URL+"not-weekends?year=%d&countryCodes=%s",
            port, year, countryCodes);

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("NL"));
        assertTrue(response.getBody().containsKey("GB"));
    }

    @Test
    void getCommonHolidays_ShouldReturnCommonHolidays() {
        // Arrange
        int year = 2025;
        String countryCode1 = "NL";
        String countryCode2 = "GB";
        String url = String.format(BASE_URL+"common?year=%d&countryCode1=%s&countryCode2=%s",
            port, year, countryCode1, countryCode2);

        // Act
        ResponseEntity<CommonHolidayInfo[]> response = restTemplate.getForEntity(url, CommonHolidayInfo[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getMostRecentHolidays_ShouldReturnBadRequest_WhenCountryCodeIsInvalid() {
        // Arrange
        String invalidCountryCode = "N"; // Too short
        int count = 3;
        String url = String.format(BASE_URL+"most-recent/%s/%d", port, invalidCountryCode, count);

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void getHolidaysNotOnWeekends_ShouldReturnBadRequest_WhenYearIsInvalid() {
        // Arrange
        int invalidYear = 1999;
        String countryCodes = "NL,GB";
        String url = String.format(BASE_URL+"not-weekends?year=%d&countryCodes=%s",
            port, invalidYear, countryCodes);

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void getCommonHolidays_ShouldReturnBadRequest_WhenCountryCodeIsInvalid() {
        // Arrange
        int year = 2025;
        String invalidCountryCode = "n";
        String countryCode2 = "GB";
        String url = String.format(BASE_URL+"/common?year=%d&countryCode1=%s&countryCode2=%s",
            port, year, invalidCountryCode, countryCode2);

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }
} 