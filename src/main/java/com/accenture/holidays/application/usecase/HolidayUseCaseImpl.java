package com.accenture.holidays.application.usecase;

import com.accenture.holidays.domain.gateway.HolidayApiClient;
import com.accenture.holidays.domain.exception.HolidayApiException;
import com.accenture.holidays.domain.model.Holiday;
import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.usecase.HolidayUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HolidayUseCaseImpl implements HolidayUseCase {

    private final HolidayApiClient apiClient;
    private final Executor asyncExecutor;

    public HolidayUseCaseImpl(HolidayApiClient apiClient) {
        this.apiClient = apiClient;
        this.asyncExecutor = ForkJoinPool.commonPool();
    }

    @Override
    public List<Holiday> getMostRecentHolidays(String countryCode, int count) {
        List<Holiday> pastHolidays = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        for (int yearOffset = 0; pastHolidays.size() < count; yearOffset++) {
            int year = currentYear - yearOffset;
            try {
                Holiday[] holidays = apiClient.fetchHolidaysByCountry(year, countryCode);
                if (holidays != null) {
                    Arrays.stream(holidays)
                            .filter(h -> h.getDate().isBefore(currentDate))
                            .sorted(Comparator.comparing(Holiday::getDate).reversed())
                            .forEach(pastHolidays::add);
                }
            } catch (HolidayApiException e) {
                log.error("Error occurred while calling holiday API: {}", e.getMessage());
                break;
            }
        }

        return pastHolidays.stream()
                .sorted(Comparator.comparing(Holiday::getDate).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Map<String, Long> getHolidaysNotOnWeekends(int year, List<String> countryCodes) {
        return countryCodes.stream()
                .collect(Collectors.toMap(
                    countryCode -> countryCode,
                    countryCode -> countWeekdayHolidays(year, countryCode)
                ));
    }

    @Override
    public List<CommonHolidayInfo> getCommonHolidays(int year, String countryCode1, String countryCode2) {
        try {
            CompletableFuture<Holiday[]> future1 = CompletableFuture.supplyAsync(
                    () -> apiClient.fetchHolidaysByCountry(year, countryCode1), asyncExecutor);
            CompletableFuture<Holiday[]> future2 = CompletableFuture.supplyAsync(
                    () -> apiClient.fetchHolidaysByCountry(year, countryCode2), asyncExecutor);

            CompletableFuture.allOf(future1, future2).join();

            Holiday[] holidays1 = future1.get();
            Holiday[] holidays2 = future2.get();

            if (holidays1 == null || holidays2 == null) {
                return Collections.emptyList();
            }

            Map<LocalDate, Holiday> holiday1Map = Arrays.stream(holidays1)
                    .collect(Collectors.toMap(Holiday::getDate, Function.identity()));

            return Arrays.stream(holidays2)
                    .filter(h -> holiday1Map.containsKey(h.getDate()))
                    .map(h2 -> new CommonHolidayInfo(
                            h2.getDate(),
                            holiday1Map.get(h2.getDate()).getLocalName(),
                            h2.getLocalName()
                    ))
                    .toList();

        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HolidayApiException("Failed to fetch holidays for comparison", e);
        }
    }

    private long countWeekdayHolidays(int year, String countryCode) {
        try {
            Holiday[] holidays = apiClient.fetchHolidaysByCountry(year, countryCode);
            if (holidays == null) return 0L;

            return Arrays.stream(holidays)
                    .map(Holiday::getDate)
                    .filter(date -> !isWeekend(date.getDayOfWeek()))
                    .count();
        } catch (HolidayApiException e) {
            log.error("Failed to count weekday holidays for country {} in year {}", countryCode, year, e);
            return 0L;
        }
    }

    private boolean isWeekend(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
} 