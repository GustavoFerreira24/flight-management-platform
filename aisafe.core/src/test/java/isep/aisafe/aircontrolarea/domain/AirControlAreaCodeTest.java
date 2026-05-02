package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirControlAreaCodeTest {

    /**
     * Purpose: verify successful air control area code creation.
     * Scenario: provide a valid code with lowercase letters and surrounding spaces.
     * Expected output: the code is normalized to uppercase text.
     */
    @Test
    void ensureValidCodeIsCreatedAndNormalized() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf(" lisacc ");

        assertEquals("LISACC", code.toString());
    }

    /**
     * Purpose: verify that an air control area code is mandatory.
     * Scenario: create a code with a blank value.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf(" "));
    }

    /**
     * Purpose: verify that short codes are rejected.
     * Scenario: create a code with only one character.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureTooShortCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("A"));
    }

    /**
     * Purpose: verify that long codes are rejected.
     * Scenario: create a code with more than ten characters.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureTooLongCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("ABCDEFGHIJK"));
    }

    /**
     * Purpose: verify that invalid characters are rejected.
     * Scenario: create a code containing a hyphen.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureInvalidCharactersAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("LIS-ACC"));
    }

    /**
     * Purpose: verify value equality.
     * Scenario: compare two equivalent codes created with different casing.
     * Expected output: both codes are equal and have the same hash code.
     */
    @Test
    void ensureEqualityUsesNormalizedValue() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("lisacc");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("LISACC");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Purpose: verify natural ordering.
     * Scenario: compare two different area codes.
     * Expected output: comparison follows normalized text ordering.
     */
    @Test
    void ensureCompareToUsesCodeValue() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("AAA");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("BBB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
    }
}