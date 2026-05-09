package isep.aisafe.airport.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportIATACodeTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirportIATACode code = new AirportIATACode();

        assertNotNull(code);
    }

    @Test
    void ensureValidAirportIataCodeIsAccepted() {
        final AirportIATACode code = AirportIATACode.valueOf("OPO");

        assertEquals("OPO", code.toString());
    }

    @Test
    void ensureAirportIataCodeIsNormalizedToUppercase() {
        final AirportIATACode code = AirportIATACode.valueOf("opo");

        assertEquals("OPO", code.toString());
    }

    @Test
    void ensureAirportIataCodeTrimsSpaces() {
        final AirportIATACode code = AirportIATACode.valueOf(" opo ");

        assertEquals("OPO", code.toString());
    }

    @Test
    void ensureNullAirportIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportIATACode.valueOf(null));
    }

    @Test
    void ensureEmptyAirportIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportIATACode.valueOf("   "));
    }

    @Test
    void ensureAirportIataCodeWithTwoLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportIATACode.valueOf("OP"));
    }

    @Test
    void ensureAirportIataCodeWithFourLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportIATACode.valueOf("OPOO"));
    }

    @Test
    void ensureAirportIataCodeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportIATACode.valueOf("OP1"));
    }

    @Test
    void ensureAirportIataCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirportIATACode.valueOf("OP-"));
    }

    @Test
    void ensureSameAirportIataCodeInstanceIsEqual() {
        final AirportIATACode code = AirportIATACode.valueOf("OPO");

        assertEquals(code, code);
    }

    @Test
    void ensureAirportIataCodesWithSameValueAreEqual() {
        final AirportIATACode first = AirportIATACode.valueOf("OPO");
        final AirportIATACode second = AirportIATACode.valueOf("opo");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureAirportIataCodesWithDifferentValuesAreNotEqual() {
        final AirportIATACode first = AirportIATACode.valueOf("OPO");
        final AirportIATACode second = AirportIATACode.valueOf("LIS");

        assertNotEquals(first, second);
    }

    @Test
    void ensureAirportIataCodeIsNotEqualToDifferentObjectType() {
        final AirportIATACode code = AirportIATACode.valueOf("OPO");

        assertNotEquals(code, "OPO");
    }

    @Test
    void ensureAirportIataCodeIsNotEqualToNull() {
        final AirportIATACode code = AirportIATACode.valueOf("OPO");

        assertNotEquals(null, code);
    }

    @Test
    void ensureAirportIataCodesCanBeCompared() {
        final AirportIATACode first = AirportIATACode.valueOf("AAA");
        final AirportIATACode second = AirportIATACode.valueOf("BBB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(AirportIATACode.valueOf("AAA")));
    }
}