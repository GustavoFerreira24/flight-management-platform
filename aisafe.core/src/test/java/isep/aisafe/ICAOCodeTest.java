package isep.aisafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ICAOCodeTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final ICAOCode code = new ICAOCode();

        assertNotNull(code);
    }

    @Test
    void ensureValidIcaoCodeWithTwoLettersIsAccepted() {
        final ICAOCode code = ICAOCode.valueOf("TP");

        assertEquals("TP", code.toString());
    }

    @Test
    void ensureValidIcaoCodeWithThreeLettersIsAccepted() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertEquals("TAP", code.toString());
    }

    @Test
    void ensureIcaoCodeIsNormalizedToUppercase() {
        final ICAOCode code = ICAOCode.valueOf("tap");

        assertEquals("TAP", code.toString());
    }

    @Test
    void ensureIcaoCodeTrimsSpaces() {
        final ICAOCode code = ICAOCode.valueOf(" tap ");

        assertEquals("TAP", code.toString());
    }

    @Test
    void ensureNullIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf(null));
    }

    @Test
    void ensureEmptyIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("   "));
    }

    @Test
    void ensureIcaoCodeWithOneLetterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("T"));
    }

    @Test
    void ensureIcaoCodeWithFourLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("TAPP"));
    }

    @Test
    void ensureIcaoCodeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("TA1"));
    }

    @Test
    void ensureIcaoCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("TA-"));
    }

    @Test
    void ensureSameIcaoCodeInstanceIsEqual() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertEquals(code, code);
    }

    @Test
    void ensureIcaoCodesWithSameValueAreEqual() {
        final ICAOCode first = ICAOCode.valueOf("TAP");
        final ICAOCode second = ICAOCode.valueOf("tap");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureIcaoCodesWithDifferentValuesAreNotEqual() {
        final ICAOCode first = ICAOCode.valueOf("TAP");
        final ICAOCode second = ICAOCode.valueOf("RYR");

        assertNotEquals(first, second);
    }

    @Test
    void ensureIcaoCodeIsNotEqualToDifferentObjectType() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertNotEquals(code, "TAP");
    }

    @Test
    void ensureIcaoCodeIsNotEqualToNull() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertNotEquals(null, code);
    }
}