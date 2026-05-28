package isep.aisafe.application.weather;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for weather data consultation actions.
 */
public class ConsultWeatherDataController {

    private final AisafeAuthorizationService authz;
    private final WeatherDataRepository weatherDataRepository;
    private final AirControlAreaRepository airControlAreaRepository;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public ConsultWeatherDataController() {
        authz = new AisafeAuthorizationService();
        weatherDataRepository = null;
        airControlAreaRepository = null;
    }

    ConsultWeatherDataController(final AisafeAuthorizationService authz) {
        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        weatherDataRepository = null;
        airControlAreaRepository = null;
    }

    ConsultWeatherDataController(
            final AisafeAuthorizationService authz,
            final WeatherDataRepository weatherDataRepository,
            final AirControlAreaRepository airControlAreaRepository) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.weatherDataRepository = Objects.requireNonNull(weatherDataRepository, "weatherDataRepository cannot be null");
        this.airControlAreaRepository = Objects.requireNonNull(airControlAreaRepository, "airControlAreaRepository cannot be null");
    }

    /**
     * Authorizes access to weather data consultation functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_CONSULTATION);
    }

    /**
     * Consults weather data for a given day and air control area.
     *
     * @param day weather data day in ISO format
     * @param areaCode air control area code
     * @return weather data for the selected filters, if available
     */
    public Optional<WeatherData> consultWeatherData(final String day, final String areaCode) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_CONSULTATION);

        ensureNotBlank(day, "Weather data day is required.");
        ensureNotBlank(areaCode, "Air control area code is required.");

        final LocalDate measurementDay = parseDay(day);
        final AirControlAreaCode airControlAreaCode = AirControlAreaCode.valueOf(areaCode);

        airControlAreaRepository().findByCode(airControlAreaCode)
                .orElseThrow(() -> new IllegalArgumentException("Air control area not found."));

        return weatherDataRepository().findByDayAndAirControlArea(measurementDay, airControlAreaCode);
    }

    private LocalDate parseDay(final String day) {
        try {
            return LocalDate.parse(day);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Weather data day must use ISO date format YYYY-MM-DD.", ex);
        }
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
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
