package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.weatherdata.domain.WeatherCoordinates;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;
import isep.aisafe.weatherdata.domain.WeatherParameter;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderType;
import isep.aisafe.weatherdata.domain.WeatherUnit;

import java.time.LocalDate;

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
            final String providerName,
            final String providerType,
            final String parameter,
            final double value,
            final String unit,
            final double altitude,
            final double latitude,
            final double longitude) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        final LocalDate measurementDay = LocalDate.parse(day);
        final AirControlAreaCode airControlAreaCode = AirControlAreaCode.valueOf(areaCode);

        airControlAreaRepository().findByCode(airControlAreaCode)
                .orElseThrow(() -> new IllegalArgumentException("Air control area not found."));

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
