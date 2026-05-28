package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.weatherdata.domain.InMemoryWeatherDataRepository;
import isep.aisafe.weatherdata.domain.WeatherCoordinates;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherParameter;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderType;
import isep.aisafe.weatherdata.domain.WeatherUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsultWeatherDataControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new ConsultWeatherDataController());
    }

    /**
     * Purpose: verify the permission required to consult weather data.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: WEATHER_DATA_CONSULTATION permission is requested.
     */
    @Test
    void ensureExecuteRequiresWeatherDataConsultationPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final ConsultWeatherDataController controller = new ConsultWeatherDataController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.WEATHER_DATA_CONSULTATION, authz.requestedRoles());
    }

    /**
     * Purpose: verify constructor dependency validation.
     * Scenario: create the controller with null injected dependencies.
     * Expected output: a NullPointerException is thrown for each missing dependency.
     */
    @Test
    void ensureConstructorRejectsNullDependencies() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();

        // Act / Assert
        assertThrows(NullPointerException.class, () -> new ConsultWeatherDataController(
                null,
                weatherDataRepository
        ));
        assertThrows(NullPointerException.class, () -> new ConsultWeatherDataController(
                authz,
                null
        ));
    }

    /**
     * Purpose: verify successful weather data consultation.
     * Scenario: weather data exists for the selected day and air control area.
     * Expected output: the matching weather data aggregate is returned.
     */
    @Test
    void ensureWeatherDataCanBeConsultedByDayAndArea() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final WeatherData expectedWeatherData = weatherData(
                LocalDate.of(2027, 1, 10),
                AirControlAreaCode.valueOf("CONACC")
        );
        weatherDataRepository.save(expectedWeatherData);
        final ConsultWeatherDataController controller = new ConsultWeatherDataController(authz, weatherDataRepository);

        // Act
        final Optional<WeatherData> weatherData = controller.consultWeatherData("2027-01-10", "CONACC");

        // Assert
        assertArrayEquals(AisafePermissions.WEATHER_DATA_CONSULTATION, authz.requestedRoles());
        assertTrue(weatherData.isPresent());
        assertSame(expectedWeatherData, weatherData.get());
        assertEquals(1, weatherData.get().measurements().size());
    }

    /**
     * Purpose: verify that consultation only returns matching weather data.
     * Scenario: weather data exists for another day or another air control area.
     * Expected output: no weather data is returned for the selected filters.
     */
    @Test
    void ensureConsultationReturnsEmptyWhenNoWeatherDataMatches() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        weatherDataRepository.save(weatherData(
                LocalDate.of(2027, 1, 11),
                AirControlAreaCode.valueOf("CONACC")
        ));
        final ConsultWeatherDataController controller = new ConsultWeatherDataController(authz, weatherDataRepository);

        // Act / Assert
        assertTrue(controller.consultWeatherData("2027-01-12", "CONACC").isEmpty());
        assertTrue(controller.consultWeatherData("2027-01-11", "ALTACC").isEmpty());
        assertArrayEquals(AisafePermissions.WEATHER_DATA_CONSULTATION, authz.requestedRoles());
    }

    /**
     * Purpose: verify authorization failure handling.
     * Scenario: authorization service rejects the current actor.
     * Expected output: the same exception is propagated before consulting the repository.
     */
    @Test
    void ensureAuthorizationFailureStopsConsultation() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final ConsultWeatherDataController controller = new ConsultWeatherDataController(
                authz,
                new InMemoryWeatherDataRepository()
        );

        // Act / Assert
        assertSame(failure, assertThrows(RuntimeException.class, () -> controller.consultWeatherData(
                "2026-06-01",
                "LISACC"
        )));
    }

    private WeatherData weatherData(final LocalDate day, final AirControlAreaCode airControlAreaCode) {
        final WeatherData weatherData = new WeatherData(day, airControlAreaCode);
        weatherData.addMeasurement(
                WeatherProvider.valueOf("IPMA", WeatherProviderType.valueOf("GROUND_SENSOR")),
                WeatherParameter.valueOf("TEMPERATURE"),
                18.5,
                WeatherUnit.valueOf("C"),
                1200,
                WeatherCoordinates.valueOf(38.72, -9.14)
        );
        return weatherData;
    }
}
