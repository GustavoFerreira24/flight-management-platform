package isep.aisafe.application.flightmanagement;

import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftOperationalStatus;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRegistrationCountry;
import isep.aisafe.aircraft.domain.CabinConfiguration;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.Elevation;
import isep.aisafe.airport.domain.GpsCoordinates;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanStatus;
import isep.aisafe.flightplan.domain.FuelQuantity;
import isep.aisafe.flightplan.domain.InMemoryFlightPlanRepository;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.pilot.domain.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        final FlightPlan updatedFlightPlan = controller.insertWeatherData("FP001", "2027-01-10", "CONACC");

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION, authz.requestedRoles());
        assertSame(flightPlan, updatedFlightPlan);
        assertEquals(1, updatedFlightPlan.weatherDataIds().size());
        assertEquals(weatherData.identity(), updatedFlightPlan.weatherDataIds().get(0));
        assertEquals(FlightPlanStatus.DRAFT, updatedFlightPlan.status());
        assertEquals(1, flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("FP001"))
                .orElseThrow()
                .weatherDataIds()
                .size());
    }

    /**
     * Purpose: verify previous flight plan validation invalidation through the use case.
     * Scenario: weather data is inserted in a previously validated flight plan.
     * Expected output: the updated and persisted flight plan returns to draft status.
     */
    @Test
    void ensureWeatherDataInsertionInvalidatesPreviousFlightPlanValidation() {
        // Arrange
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final FlightPlan flightPlan = flightPlan();
        final WeatherData weatherData = weatherData();
        flightPlan.validate();
        flightPlanRepository.save(flightPlan);
        weatherDataRepository.save(weatherData);
        final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController(
                new CapturingAuthorizationService(),
                flightPlanRepository,
                weatherDataRepository
        );

        // Act
        final FlightPlan updatedFlightPlan = controller.insertWeatherData("FP001", "2027-01-10", "CONACC");

        // Assert
        assertEquals(FlightPlanStatus.DRAFT, updatedFlightPlan.status());
        assertEquals(FlightPlanStatus.DRAFT, flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("FP001"))
                .orElseThrow()
                .status());
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
                () -> controller.insertWeatherData("FP404", "2027-01-10", "CONACC")
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
                () -> controller.insertWeatherData("FP001", "2099-12-31", "NOACC")
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
        authz.authenticatedUsername("XYZ");
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
                () -> controller.insertWeatherData("FP001", "2027-01-10", "CONACC")
        ).getMessage());
        assertTrue(flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("FP001"))
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
                () -> controller.insertWeatherData("FP001", null, "CONACC")
        ).getMessage());
        assertEquals("Weather data day is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("FP001", " ", "CONACC")
        ).getMessage());
        assertEquals("Weather data day must use ISO date format YYYY-MM-DD.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("FP001", "2027/01/10", "CONACC")
        ).getMessage());
        assertEquals("Air control area code is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("FP001", "2027-01-10", null)
        ).getMessage());
        assertEquals("Air control area code is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.insertWeatherData("FP001", "2027-01-10", " ")
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
                () -> controller.insertWeatherData("FP001", "2027-01-10", "CONACC")
        ));
    }

    private FlightPlan flightPlan() {
        return new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                pilot(),
                FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"))
        );
    }

    private Flight flight() {
        return new Flight(
                FlightDesignator.valueOf("TP123"),
                route(),
                isep.aisafe.flight.domain.Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 0))
        );
    }

    private FlightRoute route() {
        return new FlightRoute(
                RouteDesignator.valueOf("TP123"),
                company(),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
        );
    }

    private Aircraft aircraft() {
        final EngineModel engineModel = engineModel();

        return new Aircraft(
                AircraftRegistration.valueOf("CS-TXA"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company(),
                aircraftModel(engineModel),
                engineModel
        );
    }

    private Pilot pilot() {
        final Certification certification = new Certification("01-01-2026", "01-01-2028");

        return new Pilot(
                PilotID.valueOf("ABC"),
                certification,
                PilotStatus.valueOf(certification),
                IATACode.valueOf("TP"),
                EmailAddress.valueOf("pilot@example.com"),
                PhoneNumber.valueOf("+351912345678"),
                "Ben",
                "Dover",
                Set.of(AircraftModelDesignator.valueOf("A320"))
        );
    }

    private Airport airport(
            final String icao,
            final String iata,
            final String name,
            final String town) {

        return new Airport(
                AirportICAOCode.valueOf(icao),
                AirportIATACode.valueOf(iata),
                name,
                Country.valueOf("Portugal"),
                town,
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(icao, name)
        );
    }

    private AirControlArea airControlArea(final String icao, final String name) {
        return new AirControlArea(
                AirControlAreaCode.valueOf(icao + "FIR"),
                name + " Control",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );
    }

    private AirTransportCompany company() {
        return new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );
    }

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("LEAP-X", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private AircraftModel aircraftModel(final EngineModel certifiedEngineModel) {
        return new AircraftModel(
                AircraftModelDesignator.valueOf("A320", "Airbus"),
                AircraftModelType.valueOf("PASSENGER"),
                AircraftModelMotorization.valueOf("TURBOFAN"),
                6300,
                44500,
                79000,
                64500,
                27200,
                12000,
                833,
                122.6,
                0.024,
                0.62,
                180,
                List.of(certifiedEngineModel)
        );
    }

    private WeatherData weatherData() {
        final WeatherData weatherData = new WeatherData(
                LocalDate.of(2027, 1, 10),
                AirControlAreaCode.valueOf("CONACC")
        );

        final WeatherProviderType providerType = WeatherProviderType.valueOf("GROUND_SENSOR");

        weatherData.addMeasurement(
                new WeatherProvider("IPMA", List.of(providerType)),
                providerType,
                WeatherParameter.valueOf("TEMPERATURE"),
                18.5,
                WeatherUnit.valueOf("C"),
                1200,
                WeatherCoordinates.valueOf(38.72, -9.14)
        );

        return weatherData;
    }
}