package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirControlAreaTest {

    /**
     * Purpose: verify successful air control area creation.
     * Scenario: provide a valid code and name.
     * Expected output: the aggregate stores the values and uses the code as identity.
     */
    @Test
    void ensureValidAirControlAreaIsCreated() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LISACC");

        final AirControlArea area = new AirControlArea(code, "Lisbon Area Control Centre");

        assertEquals(code, area.code());
        assertEquals("Lisbon Area Control Centre", area.name());
        assertEquals(code, area.identity());
    }

    /**
     * Purpose: verify that the air control area code is mandatory.
     * Scenario: create an air control area without a code.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(null, "Lisbon Area Control Centre")
        );
    }

    /**
     * Purpose: verify that the air control area name is mandatory.
     * Scenario: create an air control area with a blank name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(AirControlAreaCode.valueOf("LISACC"), " ")
        );
    }

    /**
     * Purpose: verify name normalization.
     * Scenario: create an air control area with surrounding spaces in the name.
     * Expected output: the stored name is trimmed.
     */
    @Test
    void ensureNameIsTrimmed() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                " Lisbon Area Control Centre "
        );

        assertEquals("Lisbon Area Control Centre", area.name());
    }

    /**
     * Purpose: verify aggregate identity comparison.
     * Scenario: compare two air control areas with the same code and different names.
     * Expected output: sameAs returns true only for another air control area with the same identity.
     */
    @Test
    void ensureSameAsUsesIdentity() {
        final AirControlArea first = new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre"
        );
        final AirControlArea second = new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Other Name"
        );

        assertTrue(first.sameAs(second));
        assertFalse(first.sameAs("not an air control area"));
    }
}