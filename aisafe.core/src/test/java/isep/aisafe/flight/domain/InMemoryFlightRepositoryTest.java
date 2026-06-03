package isep.aisafe.flight.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.Elevation;
import isep.aisafe.airport.domain.GpsCoordinates;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFlightRepositoryTest {

    @Test
    void ensureFindByDesignatorReturnsSavedFlight() {
        // Arrange
        final InMemoryFlightRepository repository = new InMemoryFlightRepository();
        final Flight flight = flight("TP123", route("TP123"));
        repository.save(flight);

        // Act
        final var result = repository.findByDesignator(FlightDesignator.valueOf("TP123"));

        // Assert
        assertTrue(result.isPresent());
        assertSame(flight, result.orElseThrow());
    }

    @Test
    void ensureFindByDesignatorReturnsEmptyWhenFlightDoesNotExist() {
        // Arrange
        final InMemoryFlightRepository repository = new InMemoryFlightRepository();

        // Act
        final var result = repository.findByDesignator(FlightDesignator.valueOf("TP404"));

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureFindByRouteReturnsOnlyFlightsFromSelectedRoute() {
        // Arrange
        final InMemoryFlightRepository repository = new InMemoryFlightRepository();
        final FlightRoute selectedRoute = route("TP123");
        final Flight selectedFlight = flight("TP123", selectedRoute);
        final Flight otherFlight = flight("TP124", route("TP124"));
        repository.save(selectedFlight);
        repository.save(otherFlight);

        // Act
        final Iterable<Flight> result = repository.findByRoute(selectedRoute);

        // Assert
        assertTrue(containsSameFlight(result, selectedFlight));
        assertFalse(containsSameFlight(result, otherFlight));
    }

    @Test
    void ensureFindByRouteReturnsEmptyWhenNoFlightMatchesRoute() {
        // Arrange
        final InMemoryFlightRepository repository = new InMemoryFlightRepository();
        repository.save(flight("TP123", route("TP123")));

        // Act
        final Iterable<Flight> result = repository.findByRoute(route("TP9999"));

        // Assert
        assertFalse(result.iterator().hasNext());
    }

    private boolean containsSameFlight(final Iterable<Flight> flights, final Flight expected) {
        for (final Flight flight : flights) {
            if (flight == expected) {
                return true;
            }
        }
        return false;
    }

    private Flight flight(final String designator, final FlightRoute route) {
        return new Flight(
                FlightDesignator.valueOf(designator),
                route,
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
}
