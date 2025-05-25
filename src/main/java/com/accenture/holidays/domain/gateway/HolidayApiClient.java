package com.accenture.holidays.domain.gateway;

import com.accenture.holidays.domain.exception.HolidayApiException;
import com.accenture.holidays.domain.model.Holiday;

public interface HolidayApiClient {

    Holiday[] fetchHolidaysByCountry(int year, String countryCode) throws HolidayApiException;

}
