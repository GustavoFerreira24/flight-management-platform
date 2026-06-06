package isep.aisafe.application.report;

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
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.flightplan.domain.FuelQuantity;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.pilot.domain.Certification;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotStatus;
import isep.aisafe.pilot.domain.PhoneNumber;
import isep.aisafe.report.domain.MonthlyReport;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.weatherdata.domain.WeatherData;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GenerateMonthlyReportControllerTest {

    @Test
    void ensureExecuteRequiresFlightControlPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final GenerateMonthlyReportController controller = controller(authz, new IsolatedFlightPlanRepository());

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_CONTROL, authz.requestedRoles());
    }

    @Test
    void ensureMonthlyReportIsGeneratedForSelectedPeriod() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final IsolatedFlightPlanRepository repository = new IsolatedFlightPlanRepository();
        final FlightPlan januaryFlightPlan = flightPlan("FP010", "TP110", "CS-RP1", "UAA", LocalDateTime.of(2027, 1, 10, 10, 0));
        final FlightPlan januaryFlightPlanWithWeather = flightPlan("FP011", "TP111", "CS-RP2", "UAB", LocalDateTime.of(2027, 1, 20, 12, 0));
        januaryFlightPlanWithWeather.addWeatherData(new WeatherData(
                LocalDate.of(2027, 1, 20),
                AirControlAreaCode.valueOf("LISACC")
        ));
        final FlightPlan februaryFlightPlan = flightPlan("FP012", "TP112", "CS-RP3", "UAC", LocalDateTime.of(2027, 2, 1, 0, 0));
        repository.save(januaryFlightPlan);
        repository.save(januaryFlightPlanWithWeather);
        repository.save(februaryFlightPlan);
        final GenerateMonthlyReportController controller = controller(authz, repository);

        // Act
        final MonthlyReport report = controller.generateMonthlyReport(2027, 1);
        final String renderedReport = new MonthlyReportRenderer().render(report);

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_CONTROL, authz.requestedRoles());
        assertEquals(YearMonth.of(2027, 1), report.period());
        assertEquals(LocalDateTime.of(2027, 2, 1, 9, 30), report.generatedAt());
        assertTrue(renderedReport.contains("Total flight plans: 2"), renderedReport);
        assertTrue(renderedReport.contains("Flight plans with weather data: 1"), renderedReport);
        assertTrue(renderedReport.contains("FP010"), renderedReport);
        assertTrue(renderedReport.contains("FP011"), renderedReport);
        assertFalse(renderedReport.contains("FP012"), renderedReport);
    }

    @Test
    void ensureMonthlyReportHandlesEmptyPeriod() {
        // Arrange
        final GenerateMonthlyReportController controller = controller(
                new CapturingAuthorizationService(),
                new IsolatedFlightPlanRepository()
        );

        // Act
        final String renderedReport = controller.generateMonthlyReportText(2027, 3);

        // Assert
        assertTrue(renderedReport.contains("Total flight plans: 0"));
        assertTrue(renderedReport.contains("No flight plans found for this period."));
    }

    @Test
    void ensureInvalidReportPeriodIsRejected() {
        // Arrange
        final GenerateMonthlyReportController controller = controller(
                new CapturingAuthorizationService(),
                new IsolatedFlightPlanRepository()
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.generateMonthlyReport(2027, 0));
        assertThrows(IllegalArgumentException.class, () -> controller.generateMonthlyReport(2027, 13));
    }

    @Test
    void ensureAuthorizationFailureStopsReportGeneration() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final GenerateMonthlyReportController controller = controller(authz, new IsolatedFlightPlanRepository());

        // Act / Assert
        assertSame(failure, assertThrows(
                RuntimeException.class,
                () -> controller.generateMonthlyReport(2027, 1)
        ));
    }

    private GenerateMonthlyReportController controller(
            final CapturingAuthorizationService authz,
            final FlightPlanRepository repository) {

        return new GenerateMonthlyReportController(
                authz,
                repository,
                new MonthlyReportGenerator(),
                new MonthlyReportRenderer(),
                Clock.fixed(Instant.parse("2027-02-01T09:30:00Z"), ZoneOffset.UTC)
        );
    }

    private static class IsolatedFlightPlanRepository implements FlightPlanRepository {

        private final List<FlightPlan> flightPlans = new ArrayList<>();

        @Override
        public Optional<FlightPlan> findByIdentifier(final FlightPlanIdentifier identifier) {
            return ofIdentity(identifier);
        }

        @Override
        public Iterable<FlightPlan> findByDepartureBetween(
                final LocalDateTime startInclusive,
                final LocalDateTime endExclusive) {

            final List<FlightPlan> result = new ArrayList<>();
            for (final FlightPlan flightPlan : flightPlans) {
                final LocalDateTime departure = flightPlan.flightDepartureDateTime();
                if (!departure.isBefore(startInclusive) && departure.isBefore(endExclusive)) {
                    result.add(flightPlan);
                }
            }
            return result;
        }

        @Override
        public <S extends FlightPlan> S save(final S entity) {
            deleteOfIdentity(entity.identity());
            flightPlans.add(entity);
            return entity;
        }

        @Override
        public Iterable<FlightPlan> findAll() {
            return List.copyOf(flightPlans);
        }

        @Override
        public Optional<FlightPlan> ofIdentity(final FlightPlanIdentifier id) {
            return flightPlans.stream()
                    .filter(flightPlan -> flightPlan.identity().equals(id))
                    .findFirst();
        }

        @Override
        public void delete(final FlightPlan entity) {
            deleteOfIdentity(entity.identity());
        }

        @Override
        public void deleteOfIdentity(final FlightPlanIdentifier entityId) {
            flightPlans.removeIf(flightPlan -> flightPlan.identity().equals(entityId));
        }

        @Override
        public long count() {
            return flightPlans.size();
        }
    }

    private FlightPlan flightPlan(
            final String identifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId,
            final LocalDateTime departureDateTime) {

        return new FlightPlan(
                FlightPlanIdentifier.valueOf(identifier),
                flight(flightDesignator, departureDateTime),
                aircraft(aircraftRegistration),
                pilot(pilotId),
                FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"))
        );
    }

    private Flight flight(final String designator, final LocalDateTime departureDateTime) {
        return new Flight(
                FlightDesignator.valueOf(designator),
                route(designator),
                Departure.valueOf(departureDateTime)
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
                new GpsCoordinates(38.7742, -9.1342),
                new Elevation(114.0),
                area()
        );
    }

    private AirControlArea area() {
        return new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(35.0, 45.0, -12.0, 0.0),
                MinimumFuelRequirement.valueOf(5000, FuelUnit.valueOf("KG"))
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
                AircraftModelDesignator.valueOf("A320", "AIRBUS"),
                AircraftModelType.valueOf("PASSENGER"),
                AircraftModelMotorization.valueOf("TURBOFAN"),
                6150,
                42600,
                77000,
                62500,
                24210,
                12000,
                830,
                122.6,
                0.024,
                0.31,
                180,
                List.of(engineModel)
        );
    }

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("CFM56", "CFM"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET_A"),
                120.0,
                25000.0,
                0.6
        );
    }
}
