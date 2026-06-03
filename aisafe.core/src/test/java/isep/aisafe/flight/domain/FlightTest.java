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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightTest {

    @Test
    void ensureFlightCanBeCreatedWithRequiredAggregateState() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");
        final FlightRoute route = route("TP123");
        final Departure departure = departure();

        // Act
        final Flight flight = new Flight(designator, route, departure);

        // Assert
        assertEquals(designator, flight.identity());
        assertEquals(designator, flight.designator());
        assertSame(route, flight.route());
        assertEquals(departure, flight.departure());
    }

    @Test
    void ensureFlightRejectsMissingDesignator() {
        // Arrange
        final FlightRoute route = route("TP123");
        final Departure departure = departure();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new Flight(null, route, departure));
    }

    @Test
    void ensureFlightRejectsMissingRoute() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");
        final Departure departure = departure();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new Flight(designator, null, departure));
    }

    @Test
    void ensureFlightRejectsMissingDeparture() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");
        final FlightRoute route = route("TP123");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new Flight(designator, route, null));
    }

    @Test
    void ensureFlightRejectsInactiveRoute() {
        // Arrange
        final FlightRoute route = route("TP123");
        route.deactivateFrom(LocalDate.now().plusDays(1));
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");
        final Departure departure = departure();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new Flight(designator, route, departure));
    }

    @Test
    void ensureFlightRejectsDesignatorFromAnotherCompany() {
        // Arrange
        final FlightRoute tapRoute = route("TP123");
        final FlightDesignator ryanairDesignator = FlightDesignator.valueOf("FR123");
        final Departure departure = departure();

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new Flight(ryanairDesignator, tapRoute, departure));
    }

    @Test
    void ensureSameAsUsesFlightDesignatorIdentity() {
        // Arrange
        final Flight flight = flight("TP123");
        final Flight sameFlight = flight("TP123");
        final Flight otherFlight = flight("TP124");

        // Act
        final boolean sameInstance = flight.sameAs(flight);
        final boolean sameIdentity = flight.sameAs(sameFlight);
        final boolean differentIdentity = flight.sameAs(otherFlight);
        final boolean differentType = flight.sameAs("TP123");

        // Assert
        assertTrue(sameInstance);
        assertTrue(sameIdentity);
        assertFalse(differentIdentity);
        assertFalse(differentType);
    }

    private Flight flight(final String designator) {
        return new Flight(
                FlightDesignator.valueOf(designator),
                route(designator),
                departure()
        );
    }

    private Departure departure() {
        return Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 0));
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
