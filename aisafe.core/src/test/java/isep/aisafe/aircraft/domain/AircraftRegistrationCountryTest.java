package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationCountryTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AircraftRegistrationCountry country = new AircraftRegistrationCountry();

        assertNotNull(country);
    }

    @Test
    void ensureValidCountryIsAccepted() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertEquals("Portugal", country.toString());
    }

    @Test
    void ensureCountryNormalizesExtraSpaces() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("  United   States  ");

        assertEquals("United States", country.toString());
    }

    @Test
    void ensureNullCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistrationCountry.valueOf(null));
    }

    @Test
    void ensureEmptyCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistrationCountry.valueOf("   "));
    }

    @Test
    void ensureSameCountryInstanceIsEqual() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertEquals(country, country);
    }

    @Test
    void ensureCountriesWithSameValueAreEqual() {
        final AircraftRegistrationCountry first = AircraftRegistrationCountry.valueOf("Portugal");
        final AircraftRegistrationCountry second = AircraftRegistrationCountry.valueOf("Portugal");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureCountriesWithDifferentValuesAreNotEqual() {
        final AircraftRegistrationCountry first = AircraftRegistrationCountry.valueOf("Portugal");
        final AircraftRegistrationCountry second = AircraftRegistrationCountry.valueOf("Spain");

        assertNotEquals(first, second);
    }

    @Test
    void ensureCountryIsNotEqualToDifferentObjectType() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertNotEquals(country, "Portugal");
    }

    @Test
    void ensureCountryIsNotEqualToNull() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertNotEquals(null, country);
    }
}