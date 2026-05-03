package isep.aisafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IATACodeTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final IATACode code = new IATACode();

        assertNotNull(code);
    }

    @Test
    void ensureValidIataCodeIsAccepted() {
        final IATACode code = IATACode.valueOf("TP");

        assertEquals("TP", code.toString());
    }

    @Test
    void ensureIataCodeIsNormalizedToUppercase() {
        final IATACode code = IATACode.valueOf("tp");

        assertEquals("TP", code.toString());
    }

    @Test
    void ensureIataCodeTrimsSpaces() {
        final IATACode code = IATACode.valueOf(" tp ");

        assertEquals("TP", code.toString());
    }

    @Test
    void ensureNullIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf(null));
    }

    @Test
    void ensureEmptyIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("   "));
    }

    @Test
    void ensureIataCodeWithOneLetterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("T"));
    }

    @Test
    void ensureIataCodeWithThreeLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("TAP"));
    }

    @Test
    void ensureIataCodeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("T1"));
    }

    @Test
    void ensureIataCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("T-"));
    }

    @Test
    void ensureSameIataCodeInstanceIsEqual() {
        final IATACode code = IATACode.valueOf("TP");

        assertEquals(code, code);
    }

    @Test
    void ensureIataCodesWithSameValueAreEqual() {
        final IATACode first = IATACode.valueOf("TP");
        final IATACode second = IATACode.valueOf("tp");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureIataCodesWithDifferentValuesAreNotEqual() {
        final IATACode first = IATACode.valueOf("TP");
        final IATACode second = IATACode.valueOf("FR");

        assertNotEquals(first, second);
    }

    @Test
    void ensureIataCodeIsNotEqualToDifferentObjectType() {
        final IATACode code = IATACode.valueOf("TP");

        assertNotEquals(code, "TP");
    }

    @Test
    void ensureIataCodeIsNotEqualToNull() {
        final IATACode code = IATACode.valueOf("TP");

        assertNotEquals(null, code);
    }

    @Test
    void ensureIataCodesCanBeCompared() {
        final IATACode first = IATACode.valueOf("AA");
        final IATACode second = IATACode.valueOf("BB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(IATACode.valueOf("AA")));
    }
}