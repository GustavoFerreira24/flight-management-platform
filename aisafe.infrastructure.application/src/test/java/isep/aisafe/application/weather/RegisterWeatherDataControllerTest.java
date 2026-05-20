package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.weatherdata.domain.InMemoryWeatherDataRepository;
import isep.aisafe.weatherdata.domain.WeatherData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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

    @Test
    void ensureAuthenticatedProviderIsUsedWhenProviderIsNotSubmitted() {
        final CapturingAuthorizationService authz = new AuthenticatedAuthorizationService("meteo.provider@aisafe.com");
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(authz, weatherDataRepository);

        final WeatherData weatherData = controller.registerWeatherMeasurement(
                "2026-07-15",
                "LISACC",
                "temperature",
                18.5,
                "C",
                1200,
                38.72,
                -9.14
        );

        assertEquals("meteo.provider@aisafe.com", weatherData.measurements().getFirst().provider().name());
        assertEquals("AUTHENTICATED_PROVIDER", weatherData.measurements().getFirst().provider().type().toString());
    }

    @Test
    void ensureAirControlAreasContainingCoordinatesCanBeListed() {
        final InMemoryAirControlAreaRepository airControlAreaRepository = new InMemoryAirControlAreaRepository();
        airControlAreaRepository.save(new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        ));
        airControlAreaRepository.save(new AirControlArea(
                AirControlAreaCode.valueOf("IBRACC"),
                "Iberian Area Control Centre",
                GeographicBoundary.valueOf(35.0, 43.0, -10.0, 0.0),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        ));
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RegisterWeatherDataController controller = new RegisterWeatherDataController(
                authz,
                new InMemoryWeatherDataRepository(),
                airControlAreaRepository
        );

        final List<String> result = controller.airControlAreasContaining(40.4936, -3.56676);

        assertArrayEquals(AisafePermissions.WEATHER_DATA_MANAGEMENT, authz.requestedRoles());
        assertTrue(result.containsAll(List.of(
                "IBRACC | Iberian Area Control Centre",
                "LISACC | Lisbon Area Control Centre"
        )));
    }

    @Test
    void ensureNoAirControlAreasContainingCoordinatesReturnsEmptyList() {
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );

        final List<String> result = controller.airControlAreasContaining(50.0, 20.0);

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureAirControlAreasCanBeListedForSelection() {
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );

        final List<String> result = controller.airControlAreas();

        assertTrue(result.contains("LISACC | Lisbon Area Control Centre"));
    }

    @Test
    void ensureSelectedAirControlAreaCoordinatesCanBeChecked() {
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );

        assertTrue(controller.airControlAreaContains("LISACC", 38.72, -9.14));
        assertFalse(controller.airControlAreaContains("LISACC", 50.0, 20.0));
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
     * Purpose: verify that required weather registration fields are validated before persistence.
     * Scenario: submit blank day, area, provider, provider type, parameter and unit values.
     * Expected output: each submission throws an IllegalArgumentException and no weather data is persisted.
     */
    @Test
    void ensureRequiredWeatherRegistrationFieldsAreRejected() {
        // Arrange
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                " ", "LISACC", "Manual Station", "Manual", "temperature", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-06", " ", "Manual Station", "Manual", "temperature", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-06", "LISACC", " ", "Manual", "temperature", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-06", "LISACC", "Manual Station", " ", "temperature", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-06", "LISACC", "Manual Station", "Manual", " ", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-06", "LISACC", "Manual Station", "Manual", "temperature", 18.5, " ", 1200, 38.72, -9.14
        ));
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 6),
                AirControlAreaCode.valueOf("LISACC")
        ).isEmpty());
    }

    /**
     * Purpose: verify that measurement coordinates must belong to the selected air control area.
     * Scenario: submit otherwise valid weather data with coordinates outside the area's boundary.
     * Expected output: an IllegalArgumentException is thrown and no weather data is persisted.
     */
    @Test
    void ensureCoordinatesOutsideAirControlAreaAreRejected() {
        // Arrange
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final RegisterWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerWeatherMeasurement(
                "2026-06-07", "LISACC", "Manual Station", "Manual",
                "temperature", 18.5, "C", 1200, 50.0, -9.14
        ));
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2026, 6, 7),
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
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        ));

        return new RegisterWeatherDataController(
                authz,
                weatherDataRepository,
                airControlAreaRepository
        );
    }

    private static class AuthenticatedAuthorizationService extends CapturingAuthorizationService {

        private final String username;

        AuthenticatedAuthorizationService(final String username) {
            this.username = username;
        }

        @Override
        public String authenticatedUsername() {
            return username;
        }
    }
}
