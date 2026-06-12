package isep.aisafe.application.flightmanagement;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Application use case for US082: insert weather data in a flight plan.
 */
public class InsertWeatherDataInFlightPlanController {

    private final AisafeAuthorizationService authz;
    private final FlightPlanRepository flightPlanRepository;
    private final WeatherDataRepository weatherDataRepository;

    /**
     * Creates the controller with production services.
     */
    public InsertWeatherDataInFlightPlanController() {
        authz = new AisafeAuthorizationService();
        flightPlanRepository = null;
        weatherDataRepository = null;
    }

    InsertWeatherDataInFlightPlanController(
            final AisafeAuthorizationService authz,
            final FlightPlanRepository flightPlanRepository,
            final WeatherDataRepository weatherDataRepository) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.flightPlanRepository = Objects.requireNonNull(
                flightPlanRepository,
                "flightPlanRepository cannot be null"
        );
        this.weatherDataRepository = Objects.requireNonNull(
                weatherDataRepository,
                "weatherDataRepository cannot be null"
        );
    }

    /**
     * Authorizes access to the insertion use case.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION);
    }

    /**
     * Associates existing weather data with an existing flight plan.
     *
     * @param flightPlanIdentifier selected flight plan identifier
     * @param weatherDataDay weather data day in ISO format
     * @param airControlAreaCode weather data air control area code
     * @return updated flight plan
     */
    public FlightPlan insertWeatherData(
            final String flightPlanIdentifier,
            final String weatherDataDay,
            final String airControlAreaCode) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION);

        ensureNotBlank(flightPlanIdentifier, "Flight plan identifier is required.");
        ensureNotBlank(weatherDataDay, "Weather data day is required.");
        ensureNotBlank(airControlAreaCode, "Air control area code is required.");

        final FlightPlanIdentifier identifier = FlightPlanIdentifier.valueOf(flightPlanIdentifier);
        final FlightPlan flightPlan = flightPlanRepository().findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Flight plan not found."));

        ensureFlightPlanBelongsToAuthenticatedPilot(flightPlan);

        final LocalDate day = parseDay(weatherDataDay);
        final AirControlAreaCode areaCode = AirControlAreaCode.valueOf(airControlAreaCode);
        final WeatherData weatherData = weatherDataRepository().findByDayAndAirControlArea(day, areaCode)
                .orElseThrow(() -> new IllegalArgumentException("Weather data not found."));

        flightPlan.addWeatherData(weatherData);
        return flightPlanRepository().save(flightPlan);
    }

    private void ensureFlightPlanBelongsToAuthenticatedPilot(final FlightPlan flightPlan) {
        final String authenticatedUsername = authz.authenticatedUsername();
        final String authenticatedEmail = authz.authenticatedEmail();

        final boolean matchesPilotId = flightPlan.pilot().identity().toString().equalsIgnoreCase(authenticatedUsername);
        final boolean matchesPilotEmail = flightPlan.pilot().email().toString().equalsIgnoreCase(authenticatedEmail);

        if (!matchesPilotId && !matchesPilotEmail) {
            throw new IllegalArgumentException("Flight plan does not belong to authenticated pilot.");
        }
    }

    private LocalDate parseDay(final String weatherDataDay) {
        try {
            return LocalDate.parse(weatherDataDay);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Weather data day must use ISO date format YYYY-MM-DD.", ex);
        }
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private FlightPlanRepository flightPlanRepository() {
        if (flightPlanRepository != null) {
            return flightPlanRepository;
        }
        return PersistenceContext.repositories().flightPlans();
    }

    private WeatherDataRepository weatherDataRepository() {
        if (weatherDataRepository != null) {
            return weatherDataRepository;
        }
        return PersistenceContext.repositories().weatherData();
    }

}
