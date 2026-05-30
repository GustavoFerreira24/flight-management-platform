package isep.aisafe.airport.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.airtransportcompany.domain.Country;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportTest {

    @Test
    void ensureAirportWithCorrectDataIsCreated() {
        final AirControlAreaCode areaCode = AirControlAreaCode.valueOf("LPPTFIR");
        final AirControlArea area = airControlArea(areaCode);

        final Airport airport = new Airport(
                AirportICAOCode.valueOf("LPPT"),
                AirportIATACode.valueOf("LIS"),
                "Lisbon Airport",
                new Country("Country"),
                "Lisbon",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                area
        );

        assertNotNull(airport);
        assertEquals(AirportICAOCode.valueOf("LPPT"), airport.identity());
        assertEquals("LPPT", airport.icaoCode().toString());
        assertEquals("LIS", airport.iataCode().toString());
        assertEquals("Lisbon Airport", airport.name());
        assertEquals("Lisbon", airport.town());
    }

    @Test
    void ensureAirportFailsWithShortIcaoCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("TAP"),
                AirportIATACode.valueOf("LIS"),
                "Name",
                new Country("Country"),
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(AirControlAreaCode.valueOf("LPPTFIR"))
        ));
    }

    @Test
    void ensureAirportFailsWithShortIataCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("LPPT"),
                AirportIATACode.valueOf("TP"),
                "Name",
                new Country("Country"),
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(AirControlAreaCode.valueOf("LPPTFIR"))
        ));
    }

    @Test
    void ensureAirportFailsWithNullIcaoCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                null,
                AirportIATACode.valueOf("LIS"),
                "Name",
                new Country("Country"),
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(AirControlAreaCode.valueOf("LPPTFIR"))
        ));
    }

    @Test
    void ensureAirportFailsWithNullIataCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("LPPT"),
                null,
                "Name",
                new Country("Country"),
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(AirControlAreaCode.valueOf("LPPTFIR"))
        ));
    }

    @Test
    void ensureAirportFailsWithNullAirControlArea() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("LPPT"),
                AirportIATACode.valueOf("LIS"),
                "Name",
                new Country("Country"),
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                null
        ));
    }

    private AirControlArea airControlArea(final AirControlAreaCode areaCode) {
        return new AirControlArea(
                areaCode,
                "Lisbon Control",
                GeographicBoundary.valueOf(-90, 90, -180, 180),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        );
    }
}
