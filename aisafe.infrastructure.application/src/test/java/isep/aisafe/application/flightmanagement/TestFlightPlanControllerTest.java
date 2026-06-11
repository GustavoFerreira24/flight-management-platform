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
import isep.aisafe.flight.domain.Departure;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanStatus;
import isep.aisafe.flightplan.domain.FuelQuantity;
import isep.aisafe.flightplan.domain.InMemoryFlightPlanRepository;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslErrorType;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslSyntaxValidator;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.pilot.domain.Certification;
import isep.aisafe.pilot.domain.PhoneNumber;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotStatus;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestFlightPlanControllerTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    void ensureExecuteRequiresFlightPlanManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final TestFlightPlanController controller = controller(authz, new InMemoryFlightPlanRepository());

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureConstructorRejectsNullDependencies() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlanDslSyntaxValidator validator = new FlightPlanDslSyntaxValidator();

        // Act / Assert
        assertThrows(NullPointerException.class, () -> new TestFlightPlanController(null, repository, validator));
        assertThrows(NullPointerException.class, () -> new TestFlightPlanController(authz, null, validator));
        assertThrows(NullPointerException.class, () -> new TestFlightPlanController(authz, repository, null));
    }

    @Test
    void ensureFlightPlanCanBeTestedAndValidated() throws IOException {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP001", "TP123", "CS-TXA", "ABC");
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(authz, repository);
        final Path dslFile = writeFlightPlan("valid.fpl", validDsl("TP123", "CS-TXA"));

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP001", dslFile);

        // Assert
        assertTrue(result.isSuccessful());
        assertEquals(FlightPlanStatus.VALIDATED, result.status());
        assertEquals(List.of("Flight plan validated successfully."), result.messages());
        assertTrue(result.dslErrors().isEmpty());
        assertEquals(FlightPlanStatus.VALIDATED, flightPlan.status());
        assertEquals(FlightPlanStatus.VALIDATED, repository.findByIdentifier(FlightPlanIdentifier.valueOf("FP001"))
                .orElseThrow()
                .status());
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureInvalidDslFileFailsAndRejectsFlightPlan() throws IOException {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP002", "TP124", "CS-TXB", "ABC");
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);
        final Path dslFile = writeFlightPlan("invalid.fpl", """
                flight TP124 regular {
                  aircraft CS-TXB;
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T09:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0);
                    fuel 5000 kg;
                  }
                }
                """);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP002", dslFile);

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Flight DSL validation failed."), result.messages());
        assertEquals(List.of(FlightPlanDslErrorType.SEMANTIC), result.dslErrors().stream()
                .map(error -> error.type())
                .distinct()
                .toList());
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
    }

    @Test
    void ensureDslFlightMismatchFailsAndRejectsFlightPlan() throws IOException {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP003", "TP125", "CS-TXC", "ABC");
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);
        final Path dslFile = writeFlightPlan("other-flight.fpl", validDsl("TP999", "CS-TXC"));

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP003", dslFile);

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Flight DSL file does not describe flight TP125."), result.messages());
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
    }

    @Test
    void ensureDslAircraftMismatchFailsAndRejectsFlightPlan() throws IOException {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP004", "TP126", "CS-TXD", "ABC");
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);
        final Path dslFile = writeFlightPlan("other-aircraft.fpl", validDsl("TP126", "CS-TXE"));

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP004", dslFile);

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Flight DSL aircraft does not match the selected flight plan."), result.messages());
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
    }

    @Test
    void ensureValidatedFlightPlanCannotBeTestedAgain() throws IOException {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP005", "TP127", "CS-TXF", "ABC");
        flightPlan.validate();
        repository.save(flightPlan);

        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);
        final Path dslFile = writeFlightPlan("valid.fpl", validDsl("TP127", "CS-TXF"));

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP005", dslFile);

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.VALIDATED, result.status());
        assertEquals(List.of("Flight plan is already validated."), result.messages());
    }

    @Test
    void ensureRejectedFlightPlanCannotBeTestedAgain() throws IOException {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP006", "TP128", "CS-TXG", "ABC");
        flightPlan.reject();
        repository.save(flightPlan);

        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);
        final Path dslFile = writeFlightPlan("valid.fpl", validDsl("TP128", "CS-TXG"));

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP006", dslFile);

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Flight plan is rejected and cannot be tested."), result.messages());
    }

    @Test
    void ensureFlightPlanFromAnotherPilotIsRejected() throws IOException {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        authz.authenticatedEmail("other-pilot@example.com");

        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        repository.save(flightPlan("FP007", "TP129", "CS-TXH", "ABC"));

        final TestFlightPlanController controller = controller(authz, repository);
        final Path dslFile = writeFlightPlan("valid.fpl", validDsl("TP129", "CS-TXH"));

        // Act / Assert
        assertEquals("Flight plan does not belong to authenticated pilot.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan("FP007", dslFile)
        ).getMessage());
    }

    @Test
    void ensureUnknownFlightPlanIsRejected() throws IOException {
        // Arrange
        final TestFlightPlanController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository()
        );
        final Path dslFile = writeFlightPlan("valid.fpl", validDsl("TP130", "CS-TXI"));

        // Act / Assert
        assertEquals("Flight plan not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan("FP404", dslFile)
        ).getMessage());
    }

    @Test
    void ensureInvalidInputsAreRejected() {
        // Arrange
        final TestFlightPlanController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository()
        );
        final Path missingFile = temporaryDirectory.resolve("missing.fpl");

        // Act / Assert
        assertEquals("Flight plan identifier is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan(null, missingFile)
        ).getMessage());
        assertEquals("Flight plan identifier is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan(" ", missingFile)
        ).getMessage());
        assertEquals("Flight DSL file is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan("FP001", null)
        ).getMessage());
        assertEquals("Flight DSL file must exist and be readable.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan("FP001", missingFile)
        ).getMessage());
    }

    @Test
    void ensureAuthorizationFailureStopsTest() throws IOException {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final TestFlightPlanController controller = controller(authz, new InMemoryFlightPlanRepository());
        final Path dslFile = writeFlightPlan("valid.fpl", validDsl("TP131", "CS-TXJ"));

        // Act / Assert
        assertSame(failure, assertThrows(
                RuntimeException.class,
                () -> controller.testFlightPlan("FP001", dslFile)
        ));
    }

    private TestFlightPlanController controller(
            final CapturingAuthorizationService authz,
            final InMemoryFlightPlanRepository repository) {

        return new TestFlightPlanController(authz, repository, new FlightPlanDslSyntaxValidator());
    }

    private Path writeFlightPlan(final String fileName, final String source) throws IOException {
        final Path file = temporaryDirectory.resolve(fileName);
        Files.writeString(file, source);
        return file;
    }

    private String validDsl(
            final String flightDesignator,
            final String aircraftRegistration) {

        return """
                flight %s regular {
                  aircraft %s;
                  load 15000 kg;
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0) speed 450 knots altitude [1000,2000] m width 10 km wind 90 deg 12 m/s;
                    fuel 5000 kg;
                  }
                }
                """.formatted(flightDesignator, aircraftRegistration);
    }

    private FlightPlan flightPlan(
            final String flightPlanIdentifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId) {

        return new FlightPlan(
                FlightPlanIdentifier.valueOf(flightPlanIdentifier),
                flight(flightDesignator),
                aircraft(aircraftRegistration),
                pilot(pilotId),
                FuelQuantity.valueOf(7000, FuelUnit.valueOf("KG"))
        );
    }

    private Flight flight(final String designator) {
        return new Flight(
                FlightDesignator.valueOf(designator),
                route(designator),
                Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 0))
        );
    }

    private FlightRoute route(final String designator) {
        return new FlightRoute(
                RouteDesignator.valueOf(designator),
                company(),
                airport("LPPR", "OPO", "Porto Airport", "Porto"),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon")
        );
    }

    private Aircraft aircraft(final String registration) {
        final EngineModel engineModel = engineModel();

        return new Aircraft(
                AircraftRegistration.valueOf(registration),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company(),
                aircraftModel(engineModel),
                engineModel
        );
    }

    private Pilot pilot(final String id) {
        final Certification certification = new Certification("01-01-2026", "01-01-2028");

        return new Pilot(
                PilotID.valueOf(id),
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
                new GpsCoordinates(41.242, -8.678),
                new Elevation(70.0),
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

    private AircraftModel aircraftModel(final EngineModel engineModel) {
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
                List.of(engineModel)
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
}