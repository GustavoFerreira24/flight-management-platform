package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirControlAreaCodeTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirControlAreaCode code = new AirControlAreaCode();

        assertNotNull(code);
    }

    @Test
    void ensureValidCodeIsAccepted() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertEquals("LPPTFIR", code.toString());
    }

    @Test
    void ensureCodeIsNormalizedToUppercase() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("lpptfir");

        assertEquals("LPPTFIR", code.toString());
    }

    @Test
    void ensureCodeTrimsSpaces() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf(" lpptfir ");

        assertEquals("LPPTFIR", code.toString());
    }

    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf(null));
    }

    @Test
    void ensureEmptyCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("   "));
    }

    @Test
    void ensureCodeWithOneCharacterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("A"));
    }

    @Test
    void ensureCodeWithMoreThanTenCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("ABCDEFGHIJK"));
    }

    @Test
    void ensureCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("LPPT-FIR"));
    }

    @Test
    void ensureSameCodeInstanceIsEqual() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertEquals(code, code);
    }

    @Test
    void ensureCodesWithSameValueAreEqual() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("LPPTFIR");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("lpptfir");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureCodesWithDifferentValuesAreNotEqual() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("LPPTFIR");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("LPPRFIR");

        assertNotEquals(first, second);
    }

    @Test
    void ensureCodeIsNotEqualToDifferentObjectType() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertNotEquals(code, "LPPTFIR");
    }

    @Test
    void ensureCodeIsNotEqualToNull() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertNotEquals(null, code);
    }

    @Test
    void ensureCodesCanBeCompared() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("AAAA");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("BBBB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(AirControlAreaCode.valueOf("AAAA")));
    }
}