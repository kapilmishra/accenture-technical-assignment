package com.accenture.holidays.domain.usecase;

import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.model.Holiday;

import java.util.List;
import java.util.Map;

public interface HolidayUseCase {
    
    List<Holiday> getMostRecentHolidays(String countryCode, int count);

    Map<String, Long> getHolidaysNotOnWeekends(int year, List<String> countryCodes);

    List<CommonHolidayInfo> getCommonHolidays(int year, String countryCode1, String countryCode2);
} 