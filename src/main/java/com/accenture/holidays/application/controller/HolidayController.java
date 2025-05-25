package com.accenture.holidays.application.controller;

import com.accenture.holidays.domain.model.CommonHolidayInfo;
import com.accenture.holidays.domain.model.Holiday;
import com.accenture.holidays.domain.usecase.HolidayUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holidays")
@Tag(name = "Holiday Controller", description = "APIs for retrieving holiday information")
@Validated
public class HolidayController {

    private final HolidayUseCase holidayUseCase;

    public HolidayController(HolidayUseCase holidayUseCase) {
        this.holidayUseCase = holidayUseCase;
    }

    @Operation(summary = "Get most recent holidays for a country",
            description = "Retrieves the most recent  holidays that were celebrated in the specified country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved holidays",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Holiday.class))),
            @ApiResponse(responseCode = "400", description = "Invalid country code provided")
    })
    @GetMapping("/most-recent/{countryCode}/{count}")
    public List<Holiday> getMostRecentHolidays(
            @Parameter(description = "ISO 3166-1 alpha-2 country code (e.g., NL, GB, DE)")
            @PathVariable
            @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters")
            String countryCode,

            @Parameter(description = "Number of holidays")
            @PathVariable int count) {
        return holidayUseCase.getMostRecentHolidays(countryCode, count);
    }

    @Operation(summary = "Get holidays not falling on weekends",
            description = "Returns the count of public holidays that don't fall on weekends for each country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved holiday counts"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/not-weekends")
    public Map<String, Long> getHolidaysNotOnWeekends(
            @Parameter(description = "Year to check holidays for")
            @RequestParam
            @Min(value = 2000, message = "Year must be 2000 or later")
            @Max(value = 2100, message = "Year must be 2100 or earlier")
            int year,

            @Parameter(description = "List of country codes to check")
            @RequestParam
            @Size(min = 1, message = "At least one country code must be provided")
            List<@Size(min = 2, max = 2)
            @Pattern(regexp = "^[A-Z]{2}$") String> countryCodes) {
        return holidayUseCase.getHolidaysNotOnWeekends(year, countryCodes);
    }

    @Operation(summary = "Get common holidays between two countries",
            description = "Returns a list of holidays that are celebrated in both countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved common holidays"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/common")
    public List<CommonHolidayInfo> getCommonHolidays(
            @Parameter(description = "Year to check holidays for")
            @RequestParam
            @Min(value = 2000, message = "Year must be 2000 or later")
            @Max(value = 2100, message = "Year must be 2100 or earlier")
            int year,

            @Parameter(description = "First country code")
            @RequestParam
            @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters")
            String countryCode1,

            @Parameter(description = "Second country code")
            @RequestParam
            @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters")
            String countryCode2) {
        return holidayUseCase.getCommonHolidays(year, countryCode1, countryCode2);
    }
}