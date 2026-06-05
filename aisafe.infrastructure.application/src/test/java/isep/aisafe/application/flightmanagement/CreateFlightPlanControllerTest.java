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
import isep.aisafe.aircraft.domain.InMemoryAircraftRepository;
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
import isep.aisafe.flight.domain.InMemoryFlightRepository;
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
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateFlightPlanControllerTest {

    @Test
    void ensureExecuteRequiresFlightPlanManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final CreateFlightPlanController controller = controller(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureConstructorRejectsNullDependencies() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryFlightRepository flightRepository = new InMemoryFlightRepository();
        final InMemoryAircraftRepository aircraftRepository = new InMemoryAircraftRepository();
        final InMemoryPilotRepository pilotRepository = new InMemoryPilotRepository();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new CreateFlightPlanController(
                null,
                flightPlanRepository,
                flightRepository,
                aircraftRepository,
                pilotRepository
        ));
        assertThrows(IllegalArgumentException.class, () -> new CreateFlightPlanController(
                authz,
                null,
                flightRepository,
                aircraftRepository,
                pilotRepository
        ));
        assertThrows(IllegalArgumentException.class, () -> new CreateFlightPlanController(
                authz,
                flightPlanRepository,
                null,
                aircraftRepository,
                pilotRepository
        ));
        assertThrows(IllegalArgumentException.class, () -> new CreateFlightPlanController(
                authz,
                flightPlanRepository,
                flightRepository,
                null,
                pilotRepository
        ));
        assertThrows(IllegalArgumentException.class, () -> new CreateFlightPlanController(
                authz,
                flightPlanRepository,
                flightRepository,
                aircraftRepository,
                null
        ));
    }

    @Test
    void ensureFlightPlanCanBeCreated() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final InMemoryFlightRepository flightRepository = new InMemoryFlightRepository();
        final InMemoryAircraftRepository aircraftRepository = new InMemoryAircraftRepository();
        final InMemoryPilotRepository pilotRepository = new InMemoryPilotRepository();
        final Flight flight = flight("TP8501");
        final Aircraft aircraft = aircraft("CS-U80");
        final Pilot pilot = pilot("UAA");
        flightRepository.save(flight);
        aircraftRepository.save(aircraft);
        pilotRepository.save(pilot);
        final CreateFlightPlanController controller = new CreateFlightPlanController(
                authz,
                flightPlanRepository,
                flightRepository,
                aircraftRepository,
                pilotRepository
        );

        // Act
        final FlightPlan flightPlan = controller.createFlightPlan("FP8501", "TP8501", "CS-U80", "UAA", 7000, "KG");

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
        assertEquals(FlightPlanIdentifier.valueOf("FP8501"), flightPlan.identity());
        assertSame(flight, flightPlan.flight());
        assertSame(aircraft, flightPlan.aircraft());
        assertSame(pilot, flightPlan.pilot());
        assertEquals(FuelQuantity.valueOf(7000, FuelUnit.valueOf("KG")), flightPlan.fuelQuantity());
        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
        assertSame(flightPlan, flightPlanRepository.findByIdentifier(FlightPlanIdentifier.valueOf("FP8501")).orElseThrow());
    }

    @Test
    void ensureDuplicateFlightPlanIdentifierIsRejected() {
        // Arrange
        final InMemoryFlightPlanRepository flightPlanRepository = new InMemoryFlightPlanRepository();
        final FlightPlan existingFlightPlan = flightPlan("FP8502", "TP8502", "CS-U82", "UAB");
        flightPlanRepository.save(existingFlightPlan);
        final CreateFlightPlanController controller = new CreateFlightPlanController(
                new CapturingAuthorizationService(),
                flightPlanRepository,
                new InMemoryFlightRepository(),
                new InMemoryAircraftRepository(),
                new InMemoryPilotRepository()
        );

        // Act / Assert
        assertEquals("Flight plan identifier already exists.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.createFlightPlan("FP8502", "TP9999", "CS-U99", "UAC", 7000, "KG")
        ).getMessage());
    }

    @Test
    void ensureUnknownFlightIsRejected() {
        // Arrange
        final CreateFlightPlanController controller = controller(new CapturingAuthorizationService());

        // Act / Assert
        assertEquals("Flight was not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.createFlightPlan("FP8503", "TP8503", "CS-U83", "UAD", 7000, "KG")
        ).getMessage());
    }

    @Test
    void ensureUnknownAircraftIsRejected() {
        // Arrange
        final InMemoryFlightRepository flightRepository = new InMemoryFlightRepository();
        flightRepository.save(flight("TP8504"));
        final CreateFlightPlanController controller = new CreateFlightPlanController(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository(),
                flightRepository,
                new InMemoryAircraftRepository(),
                new InMemoryPilotRepository()
        );

        // Act / Assert
        assertEquals("Aircraft was not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.createFlightPlan("FP8504", "TP8504", "CS-U84", "UAE", 7000, "KG")
        ).getMessage());
    }

    @Test
    void ensureUnknownPilotIsRejected() {
        // Arrange
        final InMemoryFlightRepository flightRepository = new InMemoryFlightRepository();
        final InMemoryAircraftRepository aircraftRepository = new InMemoryAircraftRepository();
        flightRepository.save(flight("TP8505"));
        aircraftRepository.save(aircraft("CS-U85"));
        final CreateFlightPlanController controller = new CreateFlightPlanController(
                new CapturingAuthorizationService(),
                new InMemoryFlightPlanRepository(),
                flightRepository,
                aircraftRepository,
                new InMemoryPilotRepository()
        );

        // Act / Assert
        assertEquals("Pilot was not found.", assertThrows(
                IllegalArgumentException.class,
                () -> controller.createFlightPlan("FP8505", "TP8505", "CS-U85", "UAF", 7000, "KG")
        ).getMessage());
    }

    @Test
    void ensureAuthorizationFailureStopsCreation() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final CreateFlightPlanController controller = controller(authz);

        // Act / Assert
        assertSame(failure, assertThrows(
                RuntimeException.class,
                () -> controller.createFlightPlan("FP8506", "TP8506", "CS-U86", "UAG", 7000, "KG")
        ));
    }

    private CreateFlightPlanController controller(final CapturingAuthorizationService authz) {
        return new CreateFlightPlanController(
                authz,
                new InMemoryFlightPlanRepository(),
                new InMemoryFlightRepository(),
                new InMemoryAircraftRepository(),
                new InMemoryPilotRepository()
        );
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
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
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
                "Dover"
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
}
