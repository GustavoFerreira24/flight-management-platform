package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.weatherdata.domain.InMemoryWeatherDataRepository;
import isep.aisafe.weatherdata.domain.WeatherData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegisterWeatherDataControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting test doubles.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new RegisterWeatherDataController());
    }

    /**
     * Purpose: verify constructor dependency validation.
     * Scenario: create the controller with null injected dependencies.
     * Expected output: an IllegalArgumentException is thrown for each missing dependency.
     */
    @Test
    void ensureConstructorRejectsNullDependencies() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final InMemoryAirControlAreaRepository airControlAreaRepository = new InMemoryAirControlAreaRepository();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new RegisterWeatherDataController(
                null,
                weatherDataRepository,
                airControlAreaRepository
        ));
        assertThrows(IllegalArgumentException.class, () -> new RegisterWeatherDataController(
                authz,
                null,
                airControlAreaRepository
        ));
        assertThrows(IllegalArgumentException.class, () -> new RegisterWeatherDataController(
                authz,
                weatherDataRepository,
                null
        ));
    }

    /**
     * Purpose: verify successful weather measurement registration.
     * Scenario: a Weather Person registers valid temperature data for an existing air control area.
     * Expected output: authorization is checked and weather data is persisted with one measurement.
     */
    @Test
    void ensureValidWeatherMeasurementIsRegistered() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(authz, weatherDataRepository);

        // Act
        final WeatherData weatherData = controller.registerWeatherMeasurement(
                "2026-06-01",
                "LISACC",
                "Manual Station",
                "Manual",
                "temperature",
                18.5,
                "C",
                1200,
                38.72,
                -9.14
        );

        // Assert
        assertArrayEquals(AisafePermissions.WEATHER_DATA_MANAGEMENT, authz.requestedRoles());
        assertEquals(LocalDate.of(2026, 6, 1), weatherData.day());
        assertEquals(AirControlAreaCode.valueOf("LISACC"), weatherData.airControlAreaCode());
        assertEquals(1, weatherData.measurements().size());
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 1),
                AirControlAreaCode.valueOf("LISACC")
        ).isPresent());
    }

    /**
     * Purpose: verify that weather data is grouped by day and air control area.
     * Scenario: register two measurements for the same day and area.
     * Expected output: the same aggregate is reused and contains both measurements.
     */
    @Test
    void ensureExistingWeatherDataAggregateIsReused() {
        // Arrange
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );

        // Act
        controller.registerWeatherMeasurement(
                "2026-06-02", "LISACC", "Manual Station", "Manual",
                "temperature", 18.5, "C", 1200, 38.72, -9.14
        );
        final WeatherData weatherData = controller.registerWeatherMeasurement(
                "2026-06-02", "LISACC", "Manual Station", "Manual",
                "wind speed", 12.0, "M/S", 1500, 38.80, -9.10
        );

        // Assert
        assertEquals(2, weatherData.measurements().size());
        assertEquals(2, weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 2),
                AirControlAreaCode.valueOf("LISACC")
        ).orElseThrow().measurements().size());
    }

    /**
     * Purpose: verify that weather data can only be registered for existing air control areas.
     * Scenario: submit valid measurement data for an unknown area code.
     * Expected output: an IllegalArgumentException is thrown and nothing is persisted.
     */
    @Test
    void ensureUnknownAirControlAreaIsRejected() {
        // Arrange
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = new RegisterWeatherDataController(
                new CapturingAuthorizationService(),
                weatherDataRepository,
                new InMemoryAirControlAreaRepository()
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-03", "UNKNOWN", "Manual Station", "Manual",
                "temperature", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 3),
                AirControlAreaCode.valueOf("UNKNOWN")
        ).isEmpty());
    }

    /**
     * Purpose: verify that invalid measurement values are rejected by the registration flow.
     * Scenario: submit humidity above the accepted range.
     * Expected output: an IllegalArgumentException is thrown and no weather data is persisted.
     */
    @Test
    void ensureInvalidWeatherMeasurementIsRejected() {
        // Arrange
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-04", "LISACC", "Manual Station", "Manual",
                "humidity", 150, "PERCENT", 1200, 38.72, -9.14
        ));
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 4),
                AirControlAreaCode.valueOf("LISACC")
        ).isEmpty());
    }

    /**
     * Purpose: ensure authorization is enforced before registering weather data.
     * Scenario: authorization service rejects the current actor.
     * Expected output: the same exception is propagated and no weather data is persisted.
     */
    @Test
    void ensureAuthorizationFailureStopsRegistration() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(authz, weatherDataRepository);

        // Act / Assert
        assertSame(failure, assertThrows(RuntimeException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-05", "LISACC", "Manual Station", "Manual",
                "temperature", 18.5, "C", 1200, 38.72, -9.14
        )));
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 5),
                AirControlAreaCode.valueOf("LISACC")
        ).isEmpty());
    }

    private RegisterWeatherDataController controller(
            final CapturingAuthorizationService authz,
            final InMemoryWeatherDataRepository weatherDataRepository) {

        final InMemoryAirControlAreaRepository airControlAreaRepository = new InMemoryAirControlAreaRepository();
        airControlAreaRepository.save(new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre"
        ));

        return new RegisterWeatherDataController(
                authz,
                weatherDataRepository,
                airControlAreaRepository
        );
    }
}
