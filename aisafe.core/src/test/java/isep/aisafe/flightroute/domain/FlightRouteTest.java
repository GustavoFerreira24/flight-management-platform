package isep.aisafe.flightroute.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.Elevation;
import isep.aisafe.airport.domain.GpsCoordinates;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FlightRouteTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final FlightRoute route = new FlightRoute();

        assertNotNull(route);
    }

    @Test
    void ensureValidFlightRouteIsAccepted() {
        final AirTransportCompany company = company("TAP Air Portugal", "Portugal", "TP", "TAP");
        final Airport departure = airport("LPPT", "LIS", "Lisbon Airport", "Lisbon");
        final Airport destination = airport("LPPR", "OPO", "Porto Airport", "Porto");

        final FlightRoute route = new FlightRoute(
                RouteDesignator.valueOf("TP123"),
                company,
                departure,
                destination
        );

        assertEquals(RouteDesignator.valueOf("TP123"), route.identity());
        assertEquals(RouteDesignator.valueOf("TP123"), route.routeDesignator());
        assertEquals(FlightRouteType.regular(), route.routeType());
        assertEquals(FlightRouteStatus.active(), route.routeStatus());
        assertTrue(route.isActive());
        assertNull(route.deactivatedFrom());
        assertEquals(company, route.company());
        assertEquals(departure, route.departureAirport());
        assertEquals(destination, route.destinationAirport());
    }

    @Test
    void ensureCustomRouteTypeIsAccepted() {
        final FlightRoute route = new FlightRoute(
                RouteDesignator.valueOf("TP124"),
                FlightRouteType.valueOf("seasonal"),
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
        );

        assertEquals(FlightRouteType.valueOf("SEASONAL"), route.routeType());
    }

    @Test
    void ensureNullMandatoryFieldsAreRejected() {
        final AirTransportCompany company = company("TAP Air Portugal", "Portugal", "TP", "TAP");
        final Airport departure = airport("LPPT", "LIS", "Lisbon Airport", "Lisbon");
        final Airport destination = airport("LPPR", "OPO", "Porto Airport", "Porto");

        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(null, company, departure, destination));
        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(RouteDesignator.valueOf("TP123"), null, departure, destination));
        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(RouteDesignator.valueOf("TP123"), company, null, destination));
        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(RouteDesignator.valueOf("TP123"), company, departure, null));
        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(RouteDesignator.valueOf("TP123"), null, company, departure, destination));
    }

    @Test
    void ensureDepartureAndDestinationMustBeDifferent() {
        final AirTransportCompany company = company("TAP Air Portugal", "Portugal", "TP", "TAP");
        final Airport airport = airport("LPPT", "LIS", "Lisbon Airport", "Lisbon");

        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(
                RouteDesignator.valueOf("TP123"),
                company,
                airport,
                airport
        ));
    }

    @Test
    void ensureRouteDesignatorMustBelongToCompany() {
        assertThrows(IllegalArgumentException.class, () -> new FlightRoute(
                RouteDesignator.valueOf("FR123"),
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
        ));
    }

    @Test
    void ensureFlightRouteCanBeDeactivatedFromFutureDate() {
        final FlightRoute route = route("TP123");
        final LocalDate deactivatedFrom = LocalDate.now().plusDays(1);

        route.deactivateFrom(deactivatedFrom);

        assertFalse(route.isActive());
        assertEquals(FlightRouteStatus.inactive(), route.routeStatus());
        assertEquals(deactivatedFrom, route.deactivatedFrom());
    }

    @Test
    void ensureFlightRouteCannotBeDeactivatedWithInvalidDate() {
        final FlightRoute route = route("TP123");

        assertThrows(IllegalArgumentException.class, () -> route.deactivateFrom(null));
        assertThrows(IllegalArgumentException.class, () -> route.deactivateFrom(LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () -> route.deactivateFrom(LocalDate.now().minusDays(1)));
    }

    @Test
    void ensureFlightRouteCannotBeDeactivatedTwice() {
        final FlightRoute route = route("TP123");

        route.deactivateFrom(LocalDate.now().plusDays(1));

        assertThrows(IllegalStateException.class, () -> route.deactivateFrom(LocalDate.now().plusDays(2)));
    }

    @Test
    void ensureSameAsUsesRouteDesignatorIdentity() {
        final FlightRoute first = route("TP123");
        final FlightRoute second = route("TP123");
        final FlightRoute third = route("TP124");

        assertTrue(first.sameAs(second));
        assertFalse(first.sameAs(third));
        assertFalse(first.sameAs("TP123"));
    }

    private FlightRoute route(final String designator) {
        return new FlightRoute(
                RouteDesignator.valueOf(designator),
                company("TAP Air Portugal", "Portugal", "TP", "TAP"),
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
                new AirControlArea(AirControlAreaCode.valueOf(icao + "FIR"), name + " Control")
        );
    }

    private AirTransportCompany company(
            final String name,
            final String country,
            final String iata,
            final String icao) {

        return new AirTransportCompany(
                name,
                Country.valueOf(country),
                IATACode.valueOf(iata),
                ICAOCode.valueOf(icao)
        );
    }
}
