package com.accenture.holidays.adapter.exception;

import com.accenture.holidays.domain.exception.ErrorResponse;
import com.accenture.holidays.application.exception.GlobalExceptionHandler;
import com.accenture.holidays.domain.exception.HolidayApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/holidays");
    }


    @Test
    void handleHolidayApiException_ShouldReturnBadRequest() {
        // Arrange
        HolidayApiException ex = new HolidayApiException("API Error", new RuntimeException());

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHolidayApiException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("API Error", response.getBody().getError());
    }

    @Test
    void handleHttpClientErrorException_ShouldReturnClientError() {
        // Arrange
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpClientErrorException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("External API Error", response.getBody().getError());
    }

    @Test
    void handleHttpServerErrorException_ShouldReturnServiceUnavailable() {
        // Arrange
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpServerErrorException(ex, request);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("External API Error", response.getBody().getError());
    }

}