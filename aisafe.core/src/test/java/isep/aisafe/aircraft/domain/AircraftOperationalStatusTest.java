package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftOperationalStatusTest {

    /**
     * Verifica que o construtor protegido existe para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AircraftOperationalStatus status = new AircraftOperationalStatus();

        assertNotNull(status);
    }

    /**
     * Verifica que o estado operacional ativo e aceite.
     */
    @Test
    void ensureActiveStatusIsAccepted() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("ACTIVE");

        assertEquals("ACTIVE", status.toString());
    }

    /**
     * Verifica que o estado operacional em manutencao e aceite.
     */
    @Test
    void ensureInMaintenanceStatusIsAccepted() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("IN_MAINTENANCE");

        assertEquals("IN_MAINTENANCE", status.toString());
    }

    /**
     * Verifica que o estado operacional descomissionado e aceite.
     */
    @Test
    void ensureDecommissionedStatusIsAccepted() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("DECOMMISSIONED");

        assertEquals("DECOMMISSIONED", status.toString());
    }

    /**
     * Verifica que o estado operacional e normalizado para maiusculas.
     */
    @Test
    void ensureStatusIsNormalizedToUppercase() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("active");

        assertEquals("ACTIVE", status.toString());
    }

    /**
     * Verifica que os espacos em redor do estado operacional sao removidos.
     */
    @Test
    void ensureStatusTrimsSpaces() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf(" active ");

        assertEquals("ACTIVE", status.toString());
    }

    /**
     * Verifica que o estado operacional por omissao e ativo.
     */
    @Test
    void ensureDefaultStatusIsActive() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertEquals("ACTIVE", status.toString());
    }

    /**
     * Verifica que a fabrica de descomissionado cria esse estado operacional.
     */
    @Test
    void ensureDecommissionedFactoryCreatesDecommissionedStatus() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.decommissioned();

        assertEquals("DECOMMISSIONED", status.toString());
        assertTrue(status.isDecommissioned());
    }

    /**
     * Verifica que o estado ativo nao e considerado descomissionado.
     */
    @Test
    void ensureActiveStatusIsNotDecommissioned() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertFalse(status.isDecommissioned());
    }

    /**
     * Verifica que um estado operacional nulo e rejeitado.
     */
    @Test
    void ensureNullStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftOperationalStatus.valueOf(null));
    }

    /**
     * Verifica que um estado operacional vazio e rejeitado.
     */
    @Test
    void ensureEmptyStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftOperationalStatus.valueOf("   "));
    }

    /**
     * Verifica que um estado operacional desconhecido e rejeitado.
     */
    @Test
    void ensureInvalidStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftOperationalStatus.valueOf("BROKEN"));
    }

    /**
     * Verifica que a mesma instancia de estado operacional e igual a si propria.
     */
    @Test
    void ensureSameStatusInstanceIsEqual() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertEquals(status, status);
    }

    /**
     * Verifica que estados operacionais com o mesmo valor sao iguais.
     */
    @Test
    void ensureStatusesWithSameValueAreEqual() {
        final AircraftOperationalStatus first = AircraftOperationalStatus.valueOf("ACTIVE");
        final AircraftOperationalStatus second = AircraftOperationalStatus.valueOf("active");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que estados operacionais com valores diferentes nao sao iguais.
     */
    @Test
    void ensureStatusesWithDifferentValuesAreNotEqual() {
        final AircraftOperationalStatus first = AircraftOperationalStatus.valueOf("ACTIVE");
        final AircraftOperationalStatus second = AircraftOperationalStatus.valueOf("DECOMMISSIONED");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um estado operacional nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureStatusIsNotEqualToDifferentObjectType() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertNotEquals(status, "ACTIVE");
    }

    /**
     * Verifica que um estado operacional nao e igual a nulo.
     */
    @Test
    void ensureStatusIsNotEqualToNull() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertNotEquals(null, status);
    }
}
