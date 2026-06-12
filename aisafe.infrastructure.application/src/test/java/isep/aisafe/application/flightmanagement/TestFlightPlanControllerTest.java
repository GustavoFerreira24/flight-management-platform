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

import java.time.LocalDate;
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

        // Act / Assert
        assertThrows(NullPointerException.class, () -> new TestFlightPlanController(null, repository));
        assertThrows(NullPointerException.class, () -> new TestFlightPlanController(authz, null));
    }

    @Test
    void ensureAuthenticatedPilotFlightPlansListsOnlyOwnedTestablePlans() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan ownedDraft = flightPlan("FP001", "TP123", "CS-TXA", "ABC", "pilot@example.com");
        final FlightPlan ownedValidated = flightPlan("FP002", "TP124", "CS-TXB", "ABC", "pilot@example.com");
        final FlightPlan otherPilotDraft = flightPlan("FP003", "TP125", "CS-TXC", "XYZ", "other@example.com");
        ownedValidated.validate();
        repository.save(ownedDraft);
        repository.save(ownedValidated);
        repository.save(otherPilotDraft);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);

        // Act
        final List<FlightPlan> flightPlans = controller.authenticatedPilotFlightPlans();

        // Assert
        assertEquals(List.of(ownedDraft), flightPlans);
    }

    @Test
    void ensureFlightPlanCanBeTestedAndValidatedWithoutDslFile() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP004", "TP126", "CS-TXD", "ABC", "pilot@example.com");
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(authz, repository);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP004");

        // Assert
        assertTrue(result.isSuccessful());
        assertEquals(FlightPlanStatus.VALIDATED, result.status());
        assertEquals("Flight plan validated successfully.", result.messages().getFirst());
        assertTrue(result.messages().contains("Flight: TP126"));
        assertTrue(result.messages().contains("Aircraft: CS-TXD"));
        assertTrue(result.messages().contains("Pilot: ABC"));
        assertEquals(FlightPlanStatus.VALIDATED, flightPlan.status());
        assertEquals(FlightPlanStatus.VALIDATED, repository.findByIdentifier(FlightPlanIdentifier.valueOf("FP004"))
                .orElseThrow()
                .status());
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureOperationalValidationFailureRejectsFlightPlan() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP005", "TP127", "CS-TXE", "ABC", "pilot@example.com");
        flightPlan.aircraft().decommission();
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP005");

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Aircraft cannot be used for flights."), result.messages());
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
    }

    @Test
    void ensureRouteValidationFailureRejectsFlightPlan() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP006", "TP128", "CS-TXF", "ABC", "pilot@example.com");
        flightPlan.flight().route().deactivateFrom(LocalDate.now().plusDays(1));
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP006");

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Flight route is not active."), result.messages());
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
    }

    @Test
    void ensurePilotCertificationFailureRejectsFlightPlan() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan(
                "FP007",
                "TP129",
                "CS-TXG",
                "ABC",
                "pilot@example.com",
                Set.of(AircraftModelDesignator.valueOf("B737"))
        );
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP007");

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Pilot is not certified for the aircraft model."), result.messages());
        assertEquals(FlightPlanStatus.REJECTED, flightPlan.status());
    }

    @Test
    void ensureValidatedFlightPlanCannotBeTestedAgain() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP008", "TP130", "CS-TXH", "ABC", "pilot@example.com");
        flightPlan.validate();
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP008");

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.VALIDATED, result.status());
        assertEquals(List.of("Flight plan is already validated."), result.messages());
    }

    @Test
    void ensureRejectedFlightPlanCannotBeTestedAgain() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan("FP009", "TP131", "CS-TXI", "ABC", "pilot@example.com");
        flightPlan.reject();
        repository.save(flightPlan);
        final TestFlightPlanController controller = controller(new CapturingAuthorizationService(), repository);

        // Act
        final TestFlightPlanResult result = controller.testFlightPlan("FP009");

        // Assert
        assertFalse(result.isSuccessful());
        assertEquals(FlightPlanStatus.REJECTED, result.status());
        assertEquals(List.of("Flight plan is rejected and cannot be tested."), result.messages());
    }

    @Test
    void ensureFlightPlanFromAnotherPilotIsRejected() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        authz.authenticatedEmail("other-pilot@example.com");
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        repository.save(flightPlan("FP010", "TP132", "CS-TXJ", "ABC", "pilot@example.com"));
        final TestFlightPlanController controller = controller(authz, repository);

        // Act / Assert
        assertEquals("Flight plan does not belong to authenticated pilot.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan("FP010")
        ).getMessage());
    }

    @Test
    void ensureUnknownFlightPlanIsRejected() {
        // Arrange
        final TestFlightPlanController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository()
        );

        // Act / Assert
        assertEquals("Flight plan not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan("FP404")
        ).getMessage());
    }

    @Test
    void ensureInvalidInputsAreRejected() {
        // Arrange
        final TestFlightPlanController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository()
        );

        // Act / Assert
        assertEquals("Flight plan identifier is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan(null)
        ).getMessage());
        assertEquals("Flight plan identifier is required.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.testFlightPlan(" ")
        ).getMessage());
    }

    @Test
    void ensureAuthorizationFailureStopsTest() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final TestFlightPlanController controller = controller(authz, new InMemoryFlightPlanRepository());

        // Act / Assert
        assertSame(failure, assertThrows(
                RuntimeException.class,
                () -> controller.testFlightPlan("FP001")
        ));
    }

    private TestFlightPlanController controller(
            final CapturingAuthorizationService authz,
            final InMemoryFlightPlanRepository repository) {

        return new TestFlightPlanController(authz, repository);
    }

    private FlightPlan flightPlan(
            final String flightPlanIdentifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId,
            final String pilotEmail) {

        return flightPlan(
                flightPlanIdentifier,
                flightDesignator,
                aircraftRegistration,
                pilotId,
                pilotEmail,
                Set.of(AircraftModelDesignator.valueOf("A320"))
        );
    }

    private FlightPlan flightPlan(
            final String flightPlanIdentifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId,
            final String pilotEmail,
            final Set<AircraftModelDesignator> certifiedModels) {

        return new FlightPlan(
                FlightPlanIdentifier.valueOf(flightPlanIdentifier),
                flight(flightDesignator),
                aircraft(aircraftRegistration),
                pilot(pilotId, pilotEmail, certifiedModels),
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

    private Pilot pilot(
            final String id,
            final String email,
            final Set<AircraftModelDesignator> certifiedModels) {

        final Certification certification = new Certification("01-01-2026", "01-01-2028");

        return new Pilot(
                PilotID.valueOf(id),
                certification,
                PilotStatus.valueOf(certification),
                IATACode.valueOf("TP"),
                EmailAddress.valueOf(email),
                PhoneNumber.valueOf("+351912345678"),
                "Ben",
                "Dover",
                certifiedModels
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
