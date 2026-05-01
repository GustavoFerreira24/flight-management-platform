package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final Country country = new Country();

        assertNotNull(country);
    }

    @Test
    void ensureValidCountryIsAccepted() {
        final Country country = Country.valueOf("Portugal");

        assertEquals("Portugal", country.toString());
    }

    @Test
    void ensureCountryNormalizesExtraSpaces() {
        final Country country = Country.valueOf("  United   States  ");

        assertEquals("United States", country.toString());
    }

    @Test
    void ensureNullCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Country.valueOf(null));
    }

    @Test
    void ensureEmptyCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Country.valueOf("   "));
    }

    @Test
    void ensureSameCountryInstanceIsEqual() {
        final Country country = Country.valueOf("Portugal");

        assertEquals(country, country);
    }

    @Test
    void ensureCountriesWithSameValueAreEqual() {
        final Country first = Country.valueOf("Portugal");
        final Country second = Country.valueOf("Portugal");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureCountriesWithDifferentValuesAreNotEqual() {
        final Country first = Country.valueOf("Portugal");
        final Country second = Country.valueOf("Spain");

        assertNotEquals(first, second);
    }

    @Test
    void ensureCountryIsNotEqualToDifferentObjectType() {
        final Country country = Country.valueOf("Portugal");

        assertNotEquals(country, "Portugal");
    }

    @Test
    void ensureCountryIsNotEqualToNull() {
        final Country country = Country.valueOf("Portugal");

        assertNotEquals(null, country);
    }
}