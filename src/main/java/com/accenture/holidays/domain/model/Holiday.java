package com.accenture.holidays.domain.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Holiday {
    private LocalDate date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    private boolean global;
    private String[] counties;
    private int launchYear;
    private String[] types;
} 