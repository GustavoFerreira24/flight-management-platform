package isep.aisafe.airport.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportICAOCodeTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirportICAOCode code = new AirportICAOCode();

        assertNotNull(code);
    }

    @Test
    void ensureValidAirportIcaoCodeIsAccepted() {
        final AirportICAOCode code = AirportICAOCode.valueOf("LPPR");

        assertEquals("LPPR", code.toString());
    }

    @Test
    void ensureAirportIcaoCodeIsNormalizedToUppercase() {
        final AirportICAOCode code = AirportICAOCode.valueOf("lppr");

        assertEquals("LPPR", code.toString());
    }

    @Test
    void ensureAirportIcaoCodeTrimsSpaces() {
        final AirportICAOCode code = AirportICAOCode.valueOf(" lppr ");

        assertEquals("LPPR", code.toString());
    }

    @Test
    void ensureNullAirportIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportICAOCode.valueOf(null));
    }

    @Test
    void ensureEmptyAirportIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportICAOCode.valueOf("   "));
    }

    @Test
    void ensureAirportIcaoCodeWithThreeLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportICAOCode.valueOf("LPP"));
    }

    @Test
    void ensureAirportIcaoCodeWithFiveLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportICAOCode.valueOf("LPPRR"));
    }

    @Test
    void ensureAirportIcaoCodeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportICAOCode.valueOf("LPP1"));
    }

    @Test
    void ensureAirportIcaoCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportICAOCode.valueOf("LPP-"));
    }

    @Test
    void ensureSameAirportIcaoCodeInstanceIsEqual() {
        final AirportICAOCode code = AirportICAOCode.valueOf("LPPR");

        assertEquals(code, code);
    }

    @Test
    void ensureAirportIcaoCodesWithSameValueAreEqual() {
        final AirportICAOCode first = AirportICAOCode.valueOf("LPPR");
        final AirportICAOCode second = AirportICAOCode.valueOf("lppr");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureAirportIcaoCodesWithDifferentValuesAreNotEqual() {
        final AirportICAOCode first = AirportICAOCode.valueOf("LPPR");
        final AirportICAOCode second = AirportICAOCode.valueOf("LPPT");

        assertNotEquals(first, second);
    }

    @Test
    void ensureAirportIcaoCodeIsNotEqualToDifferentObjectType() {
        final AirportICAOCode code = AirportICAOCode.valueOf("LPPR");

        assertNotEquals(code, "LPPR");
    }

    @Test
    void ensureAirportIcaoCodeIsNotEqualToNull() {
        final AirportICAOCode code = AirportICAOCode.valueOf("LPPR");

        assertNotEquals(null, code);
    }

    @Test
    void ensureAirportIcaoCodesCanBeCompared() {
        final AirportICAOCode first = AirportICAOCode.valueOf("AAAA");
        final AirportICAOCode second = AirportICAOCode.valueOf("BBBB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(AirportICAOCode.valueOf("AAAA")));
    }
}