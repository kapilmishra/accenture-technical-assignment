package com.accenture.holidays.infrastructure.adapter;

import com.accenture.holidays.infrastructure.config.HolidayApiProperties;
import com.accenture.holidays.domain.gateway.HolidayApiClient;
import com.accenture.holidays.domain.exception.HolidayApiException;
import com.accenture.holidays.domain.model.Holiday;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@AllArgsConstructor
@Slf4j
public class HolidayApiClientImpl implements HolidayApiClient {

    private final RestTemplate restTemplate;
    private final HolidayApiProperties apiProperties;

    public Holiday[] fetchHolidaysByCountry(int year, String countryCode) throws HolidayApiException {
        String url = buildHolidayUrl(year, countryCode);
        try {
            return restTemplate.getForObject(url, Holiday[].class);
        } catch (HttpClientErrorException e) {
            log.error("Failed to fetch holidays for country {} in year {}", countryCode, year, e);
            throw new HolidayApiException("Failed to fetch holidays for country: " + countryCode, e);
        }
    }

    private String buildHolidayUrl(int year, String countryCode) {
        return UriComponentsBuilder.fromHttpUrl(apiProperties.getBaseUrl())
                .path("/PublicHolidays/{year}/{countryCode}")
                .buildAndExpand(year, countryCode)
                .toUriString();
    }
}