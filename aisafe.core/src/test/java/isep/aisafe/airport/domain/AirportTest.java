package isep.aisafe.airport.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportTest {

    @Test
    void ensureAirportWithCorrectDataIsCreated() {
        final AirControlAreaCode areaCode = AirControlAreaCode.valueOf("LPPTFIR");
        final AirControlArea area = new AirControlArea(areaCode, "Lisbon Control");

        final Airport airport = new Airport(
                AirportICAOCode.valueOf("LPPT"),
                AirportIATACode.valueOf("LIS"),
                "Lisbon Airport",
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
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                new AirControlArea(AirControlAreaCode.valueOf("LPPTFIR"), "Lisbon Control")
        ));
    }

    @Test
    void ensureAirportFailsWithShortIataCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("LPPT"),
                AirportIATACode.valueOf("TP"),
                "Name",
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                new AirControlArea(AirControlAreaCode.valueOf("LPPTFIR"), "Lisbon Control")
        ));
    }

    @Test
    void ensureAirportFailsWithNullIcaoCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                null,
                AirportIATACode.valueOf("LIS"),
                "Name",
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                new AirControlArea(AirControlAreaCode.valueOf("LPPTFIR"), "Lisbon Control")
        ));
    }

    @Test
    void ensureAirportFailsWithNullIataCode() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("LPPT"),
                null,
                "Name",
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                new AirControlArea(AirControlAreaCode.valueOf("LPPTFIR"), "Lisbon Control")
        ));
    }

    @Test
    void ensureAirportFailsWithNullAirControlArea() {
        assertThrows(IllegalArgumentException.class, () -> new Airport(
                AirportICAOCode.valueOf("LPPT"),
                AirportIATACode.valueOf("LIS"),
                "Name",
                "Town",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                null
        ));
    }
}