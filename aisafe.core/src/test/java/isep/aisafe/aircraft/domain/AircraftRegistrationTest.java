package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftRegistrationTest {

    /**
     * Verifica que o construtor protegido existe para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AircraftRegistration registration = new AircraftRegistration();

        assertNotNull(registration);
    }

    /**
     * Verifica que uma matricula valida e aceite e preservada em texto.
     */
    @Test
    void ensureValidRegistrationIsAccepted() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertEquals("CS-TVA", registration.toString());
    }

    /**
     * Verifica que uma matricula em minusculas e normalizada para maiusculas.
     */
    @Test
    void ensureRegistrationIsNormalizedToUppercase() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("cs-tva");

        assertEquals("CS-TVA", registration.toString());
    }

    /**
     * Verifica que os espacos em redor da matricula sao removidos.
     */
    @Test
    void ensureRegistrationTrimsSpaces() {
        final AircraftRegistration registration = AircraftRegistration.valueOf(" cs-tva ");

        assertEquals("CS-TVA", registration.toString());
    }

    /**
     * Verifica que uma matricula nula e rejeitada.
     */
    @Test
    void ensureNullRegistrationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf(null));
    }

    /**
     * Verifica que uma matricula vazia e rejeitada.
     */
    @Test
    void ensureEmptyRegistrationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("   "));
    }

    /**
     * Verifica que uma matricula com apenas um caracter e rejeitada.
     */
    @Test
    void ensureRegistrationWithOneCharacterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("A"));
    }

    /**
     * Verifica que uma matricula com mais de dez caracteres e rejeitada.
     */
    @Test
    void ensureRegistrationWithMoreThanTenCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("ABCDEFGHIJK"));
    }

    /**
     * Verifica que uma matricula com simbolo invalido e rejeitada.
     */
    @Test
    void ensureRegistrationWithInvalidSymbolIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftRegistration.valueOf("CS/TAB"));
    }

    /**
     * Verifica que a mesma instancia de matricula e igual a si propria.
     */
    @Test
    void ensureSameRegistrationInstanceIsEqual() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertEquals(registration, registration);
    }

    /**
     * Verifica que matriculas com o mesmo valor normalizado sao iguais.
     */
    @Test
    void ensureRegistrationsWithSameValueAreEqual() {
        final AircraftRegistration first = AircraftRegistration.valueOf("CS-TVA");
        final AircraftRegistration second = AircraftRegistration.valueOf("cs-tva");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que matriculas com valores diferentes nao sao iguais.
     */
    @Test
    void ensureRegistrationsWithDifferentValuesAreNotEqual() {
        final AircraftRegistration first = AircraftRegistration.valueOf("CS-TVA");
        final AircraftRegistration second = AircraftRegistration.valueOf("CS-TVB");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que uma matricula nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureRegistrationIsNotEqualToDifferentObjectType() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertNotEquals(registration, "CS-TVA");
    }

    /**
     * Verifica que uma matricula nao e igual a nulo.
     */
    @Test
    void ensureRegistrationIsNotEqualToNull() {
        final AircraftRegistration registration = AircraftRegistration.valueOf("CS-TVA");

        assertNotEquals(null, registration);
    }

    /**
     * Verifica que matriculas podem ser comparadas pela sua ordem natural.
     */
    @Test
    void ensureRegistrationsCanBeCompared() {
        final AircraftRegistration first = AircraftRegistration.valueOf("AA100");
        final AircraftRegistration second = AircraftRegistration.valueOf("BB100");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(AircraftRegistration.valueOf("AA100")));
    }
}
