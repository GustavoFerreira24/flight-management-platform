package isep.aisafe.airport.domain;

import isep.aisafe.IATACode;
import isep.aisafe.ICAOCode;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Airport aggregate.
 */
class AirportTest {

    @Test
    void ensureAirportWithCorrectDataIsCreated() {
        // Código sem hífens para respeitar a Regex [A-Z0-9]{2,10}
        AirControlAreaCode areaCode = AirControlAreaCode.valueOf("LPPTFIR");
        AirControlArea area = new AirControlArea(areaCode, "Lisbon Control");

        Airport airport = new Airport(
                new ICAOCode("LPPT"),
                new IATACode("LIS"),
                "Lisbon Airport", "Lisbon",
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                area
        );

        assertNotNull(airport);
        assertEquals(new ICAOCode("LPPT"), airport.identity());
    }

    @Test
    void ensureAirportFailsWithShortICAO() {
        // Airports MUST have 4 letters, even if the ValueObject allows 2-4
        assertThrows(IllegalArgumentException.class, () -> {
            new Airport(new ICAOCode("TAP"), new IATACode("LIS"), "Name", "Town", null, null, null);
        });
    }

    @Test
    void ensureAirportFailsWithShortIATA() {
        // Airports MUST have 3 letters
        assertThrows(IllegalArgumentException.class, () -> {
            new Airport(new ICAOCode("LPPT"), new IATACode("TP"), "Name", "Town", null, null, null);
        });
    }
}
