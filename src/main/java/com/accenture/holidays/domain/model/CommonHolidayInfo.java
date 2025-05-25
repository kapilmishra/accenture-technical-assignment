package com.accenture.holidays.domain.model;


import java.time.LocalDate;

public record CommonHolidayInfo(
        LocalDate date,
        String localName1,
        String localName2
){}