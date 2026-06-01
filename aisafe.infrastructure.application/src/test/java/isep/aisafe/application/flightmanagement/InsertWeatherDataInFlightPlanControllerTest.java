package isep.aisafe.application.flightmanagement;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanTestStatus;
import isep.aisafe.flightplan.domain.InMemoryFlightPlanRepository;
import isep.aisafe.flightplan.domain.PilotIdentifier;
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

import static org.junit.jupiter.api.Assertions.*;

class InsertWeatherDataInFlightPlanControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting test doubles.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new InsertWeatherDataInFlightPlanController());
    }

    /**
     * Purpose: verify the permission required by the insertion entry point.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: flight plan weather data insertion permission is requested.
     */
    @Test
    void ensureExecuteRequiresFlightPlanWeatherDataInsertionPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                authz,
                new InMemoryFlightPlanRepository(),
                new InMemoryWeatherDataRepository()
        );

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION, authz.requestedRoles());
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
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();

        // Act / Assert
        assertThrows(NullPointerException.class, () -> new InsertWeatherDataInFlightPlanController(
                null,
                flightPlanRepository,
                weatherDataRepository
        ));
        assertThrows(NullPointerException.class, () -> new InsertWeatherDataInFlightPlanController(
                authz,
                null,
                weatherDataRepository
        ));
        assertThrows(NullPointerException.class, () -> new InsertWeatherDataInFlightPlanController(
                authz,
                flightPlanRepository,
                null
        ));
    }

    /**
     * Purpose: verify successful weather data insertion in a flight plan.
     * Scenario: both selected flight plan and weather data exist.
     * Expected output: weather data is associated with the flight plan and the update is persisted.
     */
    @Test
    void ensureWeatherDataCanBeInsertedInFlightPlan() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final FlightPlan flightPlan = flightPlan();
        final WeatherData weatherData = weatherData();
        flightPlanRepository.save(flightPlan);
        weatherDataRepository.save(weatherData);
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                authz,
                flightPlanRepository,
                weatherDataRepository
        );

        // Act
        final FlightPlan updatedFlightPlan = controller.insertWeatherData("TP123", "2027-01-10", "CONACC");

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION, authz.requestedRoles());
        assertSame(flightPlan, updatedFlightPlan);
        assertEquals(1, updatedFlightPlan.weatherDataIds().size());
        assertEquals(weatherData.identity(), updatedFlightPlan.weatherDataIds().get(0));
        assertEquals(1, flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("TP123"))
                .orElseThrow()
                .weatherDataIds()
                .size());
    }

    /**
     * Purpose: verify previous flight plan test invalidation through the use case.
     * Scenario: weather data is inserted in a previously tested flight plan.
     * Expected output: the updated and persisted flight plan has a void test status.
     */
    @Test
    void ensureWeatherDataInsertionVoidsPreviousFlightPlanTest() {
        // Arrange
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final FlightPlan flightPlan = flightPlan();
        final WeatherData weatherData = weatherData();
        flightPlan.markTested();
        flightPlanRepository.save(flightPlan);
        weatherDataRepository.save(weatherData);
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                new CapturingAuthorizationService(),
                flightPlanRepository,
                weatherDataRepository
        );

        // Act
        final FlightPlan updatedFlightPlan = controller.insertWeatherData("TP123", "2027-01-10", "CONACC");

        // Assert
        assertEquals(FlightPlanTestStatus.VOID, updatedFlightPlan.testStatus());
        assertEquals(FlightPlanTestStatus.VOID, flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("TP123"))
                .orElseThrow()
                .testStatus());
    }

    /**
     * Purpose: verify missing flight plan handling.
     * Scenario: weather data insertion is requested for an unknown flight plan.
     * Expected output: the operation is rejected with a clear message.
     */
    @Test
    void ensureUnknownFlightPlanIsRejected() {
        // Arrange
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository(),
                new InMemoryWeatherDataRepository()
        );

        // Act / Assert
        assertEquals("Flight plan not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP404", "2027-01-10", "CONACC")
        ).getMessage());
    }

    /**
     * Purpose: verify missing weather data handling.
     * Scenario: flight plan exists but no weather data matches the selected day and area.
     * Expected output: the operation is rejected with a clear message.
     */
    @Test
    void ensureUnknownWeatherDataIsRejected() {
        // Arrange
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        flightPlanRepository.save(flightPlan());
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                new CapturingAuthorizationService(),
                flightPlanRepository,
                new InMemoryWeatherDataRepository()
        );

        // Act / Assert
        assertEquals("Weather data not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", "2099-12-31", "NOACC")
        ).getMessage());
    }

    /**
     * Purpose: verify flight plan ownership validation.
     * Scenario: authenticated pilot tries to update another pilot's flight plan.
     * Expected output: the operation is rejected before associating weather data.
     */
    @Test
    void ensureFlightPlanFromAnotherPilotIsRejected() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        authz.authenticatedUsername("pilot02");
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        flightPlanRepository.save(flightPlan());
        weatherDataRepository.save(weatherData());
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                authz,
                flightPlanRepository,
                weatherDataRepository
        );

        // Act / Assert
        assertEquals("Flight plan does not belong to authenticated pilot.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", "2027-01-10", "CONACC")
        ).getMessage());
        assertTrue(flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("TP123"))
                .orElseThrow()
                .weatherDataIds()
                .isEmpty());
    }

    /**
     * Purpose: verify insertion input validation.
     * Scenario: weather data insertion is requested with missing or malformed filters.
     * Expected output: invalid input is rejected with clear messages.
     */
    @Test
    void ensureInvalidInsertionInputsAreRejected() {
        // Arrange
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        flightPlanRepository.save(flightPlan());
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                new CapturingAuthorizationService(),
                flightPlanRepository,
                new InMemoryWeatherDataRepository()
        );

        // Act / Assert
        assertEquals("Flight plan identifier is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData(null, "2027-01-10", "CONACC")
        ).getMessage());
        assertEquals("Flight plan identifier is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData(" ", "2027-01-10", "CONACC")
        ).getMessage());
        assertEquals("Weather data day is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", null, "CONACC")
        ).getMessage());
        assertEquals("Weather data day is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", " ", "CONACC")
        ).getMessage());
        assertEquals("Weather data day must use ISO date format YYYY-MM-DD.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", "2027/01/10", "CONACC")
        ).getMessage());
        assertEquals("Air control area code is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", "2027-01-10", null)
        ).getMessage());
        assertEquals("Air control area code is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("TP123", "2027-01-10", " ")
        ).getMessage());
    }

    /**
     * Purpose: verify authorization failure handling.
     * Scenario: authorization service rejects the current actor.
     * Expected output: the same exception is propagated before insertion.
     */
    @Test
    void ensureAuthorizationFailureStopsInsertion() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                authz,
                new InMemoryFlightPlanRepository(),
                new InMemoryWeatherDataRepository()
        );

        // Act / Assert
        assertSame(failure, assertThrows(
                RuntimeException.class,
                () -> controller.insertWeatherData("TP123", "2027-01-10", "CONACC")
        ));
    }

    private FlightPlan flightPlan() {
        return new FlightPlan(
                FlightPlanIdentifier.valueOf("TP123"),
                PilotIdentifier.valueOf("pilot01")
        );
    }

    private WeatherData weatherData() {
        final WeatherData weatherData = new WeatherData(
                LocalDate.of(2027, 1, 10),
                AirControlAreaCode.valueOf("CONACC")
        );
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
