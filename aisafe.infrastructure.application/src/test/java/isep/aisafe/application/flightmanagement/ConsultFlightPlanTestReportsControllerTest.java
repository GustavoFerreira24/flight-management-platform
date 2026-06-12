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
import isep.aisafe.flightplan.domain.FlightPlanTestReport;
import isep.aisafe.flightplan.domain.FlightPlanTestReportIdentifier;
import isep.aisafe.flightplan.domain.FuelQuantity;
import isep.aisafe.flightplan.domain.InMemoryFlightPlanTestReportRepository;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.pilot.domain.Certification;
import isep.aisafe.pilot.domain.PhoneNumber;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotStatus;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultFlightPlanTestReportsControllerTest {

    @BeforeEach
    void clearReports() {
        new InMemoryFlightPlanTestReportRepository().clear();
    }

    @Test
    void ensureAuthenticatedPilotReportsRequiresFlightPlanManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final ConsultFlightPlanTestReportsController controller = controller(
                authz,
                new InMemoryFlightPlanTestReportRepository()
        );

        // Act
        controller.authenticatedPilotReports();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureAuthenticatedPilotReportsListsOnlyOwnedReportsOrderedByMostRecent() {
        // Arrange
        final InMemoryFlightPlanTestReportRepository repository = new InMemoryFlightPlanTestReportRepository();
        final FlightPlanTestReport olderOwned = report(
                "RPT001",
                "FP001",
                "TP101",
                "CS-TXA",
                "ABC",
                "pilot@example.com",
                LocalDateTime.of(2026, 6, 12, 10, 0)
        );
        final FlightPlanTestReport newerOwned = report(
                "RPT002",
                "FP002",
                "TP102",
                "CS-TXB",
                "ABC",
                "pilot@example.com",
                LocalDateTime.of(2026, 6, 12, 11, 0)
        );
        final FlightPlanTestReport otherPilot = report(
                "RPT003",
                "FP003",
                "TP103",
                "CS-TXC",
                "XYZ",
                "other@example.com",
                LocalDateTime.of(2026, 6, 12, 12, 0)
        );
        repository.save(olderOwned);
        repository.save(newerOwned);
        repository.save(otherPilot);
        final ConsultFlightPlanTestReportsController controller = controller(
                new CapturingAuthorizationService(),
                repository
        );

        // Act
        final List<FlightPlanTestReport> reports = controller.authenticatedPilotReports();

        // Assert
        assertEquals(List.of(newerOwned, olderOwned), reports);
    }

    @Test
    void ensureConstructorRejectsNullDependencies() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryFlightPlanTestReportRepository repository = new InMemoryFlightPlanTestReportRepository();

        // Act / Assert
        assertThrows(NullPointerException.class, () -> new ConsultFlightPlanTestReportsController(null, repository));
        assertThrows(NullPointerException.class, () -> new ConsultFlightPlanTestReportsController(authz, null));
    }

    private ConsultFlightPlanTestReportsController controller(
            final CapturingAuthorizationService authz,
            final InMemoryFlightPlanTestReportRepository repository) {

        return new ConsultFlightPlanTestReportsController(authz, repository);
    }

    private FlightPlanTestReport report(
            final String reportIdentifier,
            final String flightPlanIdentifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId,
            final String pilotEmail,
            final LocalDateTime testedAt) {

        return new FlightPlanTestReport(
                FlightPlanTestReportIdentifier.valueOf(reportIdentifier),
                flightPlan(flightPlanIdentifier, flightDesignator, aircraftRegistration, pilotId, pilotEmail),
                testedAt,
                pilotEmail,
                true,
                FlightPlanStatus.DRAFT,
                FlightPlanStatus.UNDER_VALIDATION,
                FlightPlanStatus.VALIDATED,
                List.of("Flight plan validated successfully.")
        );
    }

    private FlightPlan flightPlan(
            final String flightPlanIdentifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId,
            final String pilotEmail) {

        return new FlightPlan(
                FlightPlanIdentifier.valueOf(flightPlanIdentifier),
                flight(flightDesignator),
                aircraft(aircraftRegistration),
                pilot(pilotId, pilotEmail),
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

    private Pilot pilot(final String id, final String email) {
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
