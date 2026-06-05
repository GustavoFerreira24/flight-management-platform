package isep.aisafe.flightplan.domain;

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
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.pilot.domain.Certification;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFlightPlanRepositoryTest {

    /**
     * Purpose: verify repository lookup by flight plan identifier.
     * Scenario: save a flight plan and search by its identifier.
     * Expected output: the saved flight plan is returned.
     */
    @Test
    void ensureFindByIdentifierReturnsSavedFlightPlan() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan flightPlan = flightPlan();
        repository.save(flightPlan);

        // Act
        final Optional<FlightPlan> result = repository.findByIdentifier(FlightPlanIdentifier.valueOf("FP001"));

        // Assert
        assertTrue(result.isPresent());
        assertSame(flightPlan, result.orElseThrow());
    }

    /**
     * Purpose: verify repository lookup when the flight plan does not exist.
     * Scenario: search for an unknown flight plan identifier.
     * Expected output: no flight plan is returned.
     */
    @Test
    void ensureFindByIdentifierReturnsEmptyWhenFlightPlanDoesNotExist() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();

        // Act
        final Optional<FlightPlan> result = repository.findByIdentifier(FlightPlanIdentifier.valueOf("FP404"));

        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Purpose: verify monthly/period lookup by flight departure.
     * Scenario: save flight plans inside and outside a selected departure interval.
     * Expected output: only flight plans departing inside the interval are returned.
     */
    @Test
    void ensureFindByDepartureBetweenReturnsOnlyFlightPlansInsideInterval() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final FlightPlan inside = flightPlan("FP010", "TP110", LocalDateTime.of(2027, 1, 10, 10, 0));
        final FlightPlan atEnd = flightPlan("FP011", "TP111", LocalDateTime.of(2027, 2, 1, 0, 0));
        final FlightPlan before = flightPlan("FP012", "TP112", LocalDateTime.of(2026, 12, 31, 23, 59));
        repository.save(inside);
        repository.save(atEnd);
        repository.save(before);

        // Act
        final Iterable<FlightPlan> result = repository.findByDepartureBetween(
                LocalDateTime.of(2027, 1, 1, 0, 0),
                LocalDateTime.of(2027, 2, 1, 0, 0)
        );

        // Assert
        assertTrue(containsSameFlightPlan(result, inside));
        assertFalse(containsSameFlightPlan(result, atEnd));
        assertFalse(containsSameFlightPlan(result, before));
    }

    @Test
    void ensureFindByDepartureBetweenRejectsInvalidInterval() {
        // Arrange
        final InMemoryFlightPlanRepository repository = new InMemoryFlightPlanRepository();
        final LocalDateTime start = LocalDateTime.of(2027, 1, 1, 0, 0);
        final LocalDateTime end = LocalDateTime.of(2027, 2, 1, 0, 0);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> repository.findByDepartureBetween(null, end));
        assertThrows(IllegalArgumentException.class, () -> repository.findByDepartureBetween(start, null));
        assertThrows(IllegalArgumentException.class, () -> repository.findByDepartureBetween(end, start));
    }

    private FlightPlan flightPlan() {
        return flightPlan("FP001", "TP123", LocalDateTime.of(2027, 1, 10, 10, 0));
    }

    private FlightPlan flightPlan(
            final String identifier,
            final String flightDesignator,
            final LocalDateTime departureDateTime) {

        return new FlightPlan(
                FlightPlanIdentifier.valueOf(identifier),
                flight(flightDesignator, departureDateTime),
                aircraft(),
                pilot(),
                FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"))
        );
    }

    private Flight flight() {
        return flight("TP123", LocalDateTime.of(2027, 1, 10, 10, 0));
    }

    private Flight flight(final String designator, final LocalDateTime departureDateTime) {
        return new Flight(
                FlightDesignator.valueOf(designator),
                route(designator),
                isep.aisafe.flight.domain.Departure.valueOf(departureDateTime)
        );
    }

    private FlightRoute route() {
        return route("TP123");
    }

    private FlightRoute route(final String designator) {
        return new FlightRoute(
                RouteDesignator.valueOf(designator),
                company(),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
        );
    }

    private boolean containsSameFlightPlan(final Iterable<FlightPlan> flightPlans, final FlightPlan expected) {
        for (final FlightPlan flightPlan : flightPlans) {
            if (flightPlan == expected) {
                return true;
            }
        }
        return false;
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
                IATACode.valueOf("TP")
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
