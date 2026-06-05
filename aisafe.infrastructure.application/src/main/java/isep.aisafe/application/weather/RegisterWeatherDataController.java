package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;
import isep.aisafe.weatherdata.domain.WeatherCoordinates;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;
import isep.aisafe.weatherdata.domain.WeatherParameter;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderType;
import isep.aisafe.weatherdata.domain.WeatherUnit;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Coordinates manual weather data registration.
 */
public class RegisterWeatherDataController {

    private final AisafeAuthorizationService authz;
    private final WeatherDataRepository weatherDataRepository;
    private final AirControlAreaRepository airControlAreaRepository;

    /**
     * Creates the controller with production authorization and persistence services.
     */
    public RegisterWeatherDataController() {
        authz = new AisafeAuthorizationService();
        weatherDataRepository = null;
        airControlAreaRepository = null;
    }

    RegisterWeatherDataController(
            final AisafeAuthorizationService authz,
            final WeatherDataRepository weatherDataRepository,
            final AirControlAreaRepository airControlAreaRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (weatherDataRepository == null) {
            throw new IllegalArgumentException("Weather data repository cannot be null.");
        }

        if (airControlAreaRepository == null) {
            throw new IllegalArgumentException("Air control area repository cannot be null.");
        }

        this.authz = authz;
        this.weatherDataRepository = weatherDataRepository;
        this.airControlAreaRepository = airControlAreaRepository;
    }

    /**
     * Registers one weather measurement for a day and air control area.
     *
     * @return persisted weather data aggregate containing the measurement
     */
    public WeatherData registerWeatherMeasurement(
            final String day,
            final String areaCode,
            final String parameter,
            final double value,
            final String unit,
            final double altitude,
            final double latitude,
            final double longitude) {

        return registerWeatherMeasurement(
                day,
                areaCode,
                authz.authenticatedUsername(),
                "MANUAL_INPUT",
                parameter,
                value,
                unit,
                altitude,
                latitude,
                longitude
        );
    }

    public WeatherData registerWeatherMeasurement(
            final String day,
            final String areaCode,
            final String providerName,
            final String providerType,
            final String parameter,
            final double value,
            final String unit,
            final double altitude,
            final double latitude,
            final double longitude) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        ensureNotBlank(day, "Weather data day is required.");
        ensureNotBlank(areaCode, "Air control area code is required.");
        ensureNotBlank(providerName, "Weather provider name is required.");
        ensureNotBlank(providerType, "Weather provider type is required.");
        ensureNotBlank(parameter, "Weather parameter is required.");
        ensureNotBlank(unit, "Weather unit is required.");

        final LocalDate measurementDay = parseDay(day);
        final AirControlAreaCode airControlAreaCode = AirControlAreaCode.valueOf(areaCode);

        final AirControlArea airControlArea = airControlAreaRepository().findByCode(airControlAreaCode)
                .orElseThrow(() -> new IllegalArgumentException("Air control area not found."));

        if (!airControlArea.containsCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException("Weather coordinates must be inside the air control area.");
        }

        final WeatherData weatherData = weatherDataRepository()
                .findByDayAndAirControlArea(measurementDay, airControlAreaCode)
                .orElseGet(() -> new WeatherData(measurementDay, airControlAreaCode));

        weatherData.addMeasurement(
                WeatherProvider.valueOf(providerName, WeatherProviderType.valueOf(providerType)),
                WeatherParameter.valueOf(parameter),
                value,
                WeatherUnit.valueOf(unit),
                altitude,
                WeatherCoordinates.valueOf(latitude, longitude)
        );

        return weatherDataRepository().save(weatherData);
    }

    /**
     * Lists air control areas whose geographic boundary contains the given coordinates.
     *
     * @param latitude weather measurement latitude
     * @param longitude weather measurement longitude
     * @return sorted area selection labels
     */
    public List<String> airControlAreasContaining(final double latitude, final double longitude) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        WeatherCoordinates.valueOf(latitude, longitude);

        final List<String> areas = new ArrayList<>();

        for (final AirControlArea area : airControlAreaRepository().findAll()) {
            if (area.containsCoordinates(latitude, longitude)) {
                areas.add(area.code() + " | " + area.name());
            }
        }

        areas.sort(String::compareTo);
        return areas;
    }

    /**
     * Lists available air control areas for weather data registration.
     *
     * @return sorted area selection labels
     */
    public List<String> airControlAreas() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        final List<String> areas = new ArrayList<>();

        for (final AirControlArea area : airControlAreaRepository().findAll()) {
            areas.add(area.code() + " | " + area.name());
        }

        areas.sort(String::compareTo);
        return areas;
    }

    /**
     * Checks whether coordinates belong to the selected air control area.
     *
     * @param areaCode selected air control area code
     * @param latitude weather measurement latitude
     * @param longitude weather measurement longitude
     * @return true if the coordinates are inside the selected area
     */
    public boolean airControlAreaContains(final String areaCode, final double latitude, final double longitude) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);
        ensureNotBlank(areaCode, "Air control area code is required.");

        WeatherCoordinates.valueOf(latitude, longitude);

        final AirControlAreaCode code = AirControlAreaCode.valueOf(areaCode);
        final AirControlArea airControlArea = airControlAreaRepository().findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Air control area not found."));

        return airControlArea.containsCoordinates(latitude, longitude);
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private LocalDate parseDay(final String day) {
        try {
            return LocalDate.parse(day);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Weather data day must use ISO date format YYYY-MM-DD.", ex);
        }
    }

    private WeatherDataRepository weatherDataRepository() {
        if (weatherDataRepository != null) {
            return weatherDataRepository;
        }

        return PersistenceContext.repositories().weatherData();
    }

    private AirControlAreaRepository airControlAreaRepository() {
        if (airControlAreaRepository != null) {
            return airControlAreaRepository;
        }

        return PersistenceContext.repositories().airControlAreas();
    }
}
