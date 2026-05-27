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

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFlightRouteRepositoryTest {

    @Test
    void ensureFindByRouteDesignatorReturnsSavedRoute() {
        final InMemoryFlightRouteRepository repository = new InMemoryFlightRouteRepository();
        final FlightRoute route = route("TP123", company("TAP Air Portugal", "Portugal", "TP", "TAP"));

        repository.save(route);

        assertTrue(repository.findByRouteDesignator(RouteDesignator.valueOf("TP123")).isPresent());
        assertEquals(route, repository.findByRouteDesignator(RouteDesignator.valueOf("TP123")).get());
    }

    @Test
    void ensureFindByRouteDesignatorReturnsEmptyWhenRouteDoesNotExist() {
        final InMemoryFlightRouteRepository repository = new InMemoryFlightRouteRepository();

        assertTrue(repository.findByRouteDesignator(RouteDesignator.valueOf("TP9999")).isEmpty());
    }

    @Test
    void ensureFindByCompanyReturnsSavedRoutesFromCompany() {
        final InMemoryFlightRouteRepository repository = new InMemoryFlightRouteRepository();
        final AirTransportCompany tap = company("TAP Air Portugal", "Portugal", "TP", "TAP");
        final FlightRoute route = route("TP123", tap);

        repository.save(route);

        final Iterable<FlightRoute> result = repository.findByCompany(tap);

        assertTrue(result.iterator().hasNext());
        assertEquals(route, result.iterator().next());
    }

    @Test
    void ensureFindByCompanyDoesNotReturnRoutesFromAnotherCompany() {
        final InMemoryFlightRouteRepository repository = new InMemoryFlightRouteRepository();
        final AirTransportCompany tap = company("TAP Air Portugal", "Portugal", "TP", "TAP");
        final AirTransportCompany ryanair = company("Ryanair", "Ireland", "FR", "RYR");

        repository.save(route("TP123", tap));

        assertFalse(repository.findByCompany(ryanair).iterator().hasNext());
    }

    private FlightRoute route(final String designator, final AirTransportCompany company) {
        return new FlightRoute(
                RouteDesignator.valueOf(designator),
                company,
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
