package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationCountryTest {

    /**
     * Verifica que o construtor protegido existe para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AircraftRegistrationCountry country = new AircraftRegistrationCountry();

        assertNotNull(country);
    }

    /**
     * Verifica que um pais de registo valido e aceite e preservado em texto.
     */
    @Test
    void ensureValidCountryIsAccepted() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertEquals("Portugal", country.toString());
    }

    /**
     * Verifica que espacos extra no pais de registo sao normalizados.
     */
    @Test
    void ensureCountryNormalizesExtraSpaces() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("  United   States  ");

        assertEquals("United States", country.toString());
    }

    /**
     * Verifica que um pais de registo nulo e rejeitado.
     */
    @Test
    void ensureNullCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistrationCountry.valueOf(null));
    }

    /**
     * Verifica que um pais de registo vazio e rejeitado.
     */
    @Test
    void ensureEmptyCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistrationCountry.valueOf("   "));
    }

    /**
     * Verifica que a mesma instancia de pais e igual a si propria.
     */
    @Test
    void ensureSameCountryInstanceIsEqual() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertEquals(country, country);
    }

    /**
     * Verifica que paises de registo com o mesmo valor sao iguais.
     */
    @Test
    void ensureCountriesWithSameValueAreEqual() {
        final AircraftRegistrationCountry first = AircraftRegistrationCountry.valueOf("Portugal");
        final AircraftRegistrationCountry second = AircraftRegistrationCountry.valueOf("Portugal");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que paises de registo com valores diferentes nao sao iguais.
     */
    @Test
    void ensureCountriesWithDifferentValuesAreNotEqual() {
        final AircraftRegistrationCountry first = AircraftRegistrationCountry.valueOf("Portugal");
        final AircraftRegistrationCountry second = AircraftRegistrationCountry.valueOf("Spain");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um pais de registo nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureCountryIsNotEqualToDifferentObjectType() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertNotEquals(country, "Portugal");
    }

    /**
     * Verifica que um pais de registo nao e igual a nulo.
     */
    @Test
    void ensureCountryIsNotEqualToNull() {
        final AircraftRegistrationCountry country = AircraftRegistrationCountry.valueOf("Portugal");

        assertNotEquals(null, country);
    }
}
