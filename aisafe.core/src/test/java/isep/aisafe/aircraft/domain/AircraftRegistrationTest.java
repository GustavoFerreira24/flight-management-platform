package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AircraftRegistration registration = new AircraftRegistration();

        assertNotNull(registration);
    }

    @Test
    void ensureValidRegistrationIsAccepted() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertEquals("CS-TVA", registration.toString());
    }

    @Test
    void ensureRegistrationIsNormalizedToUppercase() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("cs-tva");

        assertEquals("CS-TVA", registration.toString());
    }

    @Test
    void ensureRegistrationTrimsSpaces() {
        final AircraftRegistration registration = AircraftRegistration.valueOf(" cs-tva ");

        assertEquals("CS-TVA", registration.toString());
    }

    @Test
    void ensureNullRegistrationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf(null));
    }

    @Test
    void ensureEmptyRegistrationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("   "));
    }

    @Test
    void ensureRegistrationWithOneCharacterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("A"));
    }

    @Test
    void ensureRegistrationWithMoreThanTenCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("ABCDEFGHIJK"));
    }

    @Test
    void ensureRegistrationWithInvalidSymbolIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("CS/TAB"));
    }

    @Test
    void ensureSameRegistrationInstanceIsEqual() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertEquals(registration, registration);
    }

    @Test
    void ensureRegistrationsWithSameValueAreEqual() {
        final AircraftRegistration first = AircraftRegistration.valueOf("CS-TVA");
        final AircraftRegistration second = AircraftRegistration.valueOf("cs-tva");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureRegistrationsWithDifferentValuesAreNotEqual() {
        final AircraftRegistration first = AircraftRegistration.valueOf("CS-TVA");
        final AircraftRegistration second = AircraftRegistration.valueOf("CS-TVB");

        assertNotEquals(first, second);
    }

    @Test
    void ensureRegistrationIsNotEqualToDifferentObjectType() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertNotEquals(registration, "CS-TVA");
    }

    @Test
    void ensureRegistrationIsNotEqualToNull() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertNotEquals(null, registration);
    }

    @Test
    void ensureRegistrationsCanBeCompared() {
        final AircraftRegistration first = AircraftRegistration.valueOf("AA100");
        final AircraftRegistration second = AircraftRegistration.valueOf("BB100");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(AircraftRegistration.valueOf("AA100")));
    }
}