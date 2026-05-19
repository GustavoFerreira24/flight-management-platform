package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    /**
     * Verifica que existe um construtor protegido para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final Country country = new Country();

        assertNotNull(country);
    }

    /**
     * Verifica que um pais valido e aceite e mantem o seu valor.
     */
    @Test
    void ensureValidCountryIsAccepted() {
        final Country country = Country.valueOf("Portugal");

        assertEquals("Portugal", country.toString());
    }

    /**
     * Verifica que o pais e normalizado ao remover espacos extra.
     */
    @Test
    void ensureCountryNormalizesExtraSpaces() {
        final Country country = Country.valueOf("  United   States  ");

        assertEquals("United States", country.toString());
    }

    /**
     * Verifica que um pais nulo e rejeitado.
     */
    @Test
    void ensureNullCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Country.valueOf(null));
    }

    /**
     * Verifica que um pais vazio e rejeitado.
     */
    @Test
    void ensureEmptyCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Country.valueOf("   "));
    }

    /**
     * Verifica que a mesma instancia de pais e igual a si propria.
     */
    @Test
    void ensureSameCountryInstanceIsEqual() {
        final Country country = Country.valueOf("Portugal");

        assertEquals(country, country);
    }

    /**
     * Verifica que paises com o mesmo valor sao iguais e tem o mesmo hash.
     */
    @Test
    void ensureCountriesWithSameValueAreEqual() {
        final Country first = Country.valueOf("Portugal");
        final Country second = Country.valueOf("Portugal");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que paises com valores diferentes nao sao iguais.
     */
    @Test
    void ensureCountriesWithDifferentValuesAreNotEqual() {
        final Country first = Country.valueOf("Portugal");
        final Country second = Country.valueOf("Spain");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um pais nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureCountryIsNotEqualToDifferentObjectType() {
        final Country country = Country.valueOf("Portugal");

        assertNotEquals(country, "Portugal");
    }

    /**
     * Verifica que um pais nao e igual a nulo.
     */
    @Test
    void ensureCountryIsNotEqualToNull() {
        final Country country = Country.valueOf("Portugal");

        assertNotEquals(null, country);
    }
}
