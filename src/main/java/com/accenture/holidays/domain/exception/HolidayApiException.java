package com.accenture.holidays.domain.exception;

public class HolidayApiException extends RuntimeException {
    public HolidayApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
