package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftOperationalStatusTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AircraftOperationalStatus status = new AircraftOperationalStatus();

        assertNotNull(status);
    }

    @Test
    void ensureActiveStatusIsAccepted() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("ACTIVE");

        assertEquals("ACTIVE", status.toString());
    }

    @Test
    void ensureInMaintenanceStatusIsAccepted() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("IN_MAINTENANCE");

        assertEquals("IN_MAINTENANCE", status.toString());
    }

    @Test
    void ensureDecommissionedStatusIsAccepted() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("DECOMMISSIONED");

        assertEquals("DECOMMISSIONED", status.toString());
    }

    @Test
    void ensureStatusIsNormalizedToUppercase() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf("active");

        assertEquals("ACTIVE", status.toString());
    }

    @Test
    void ensureStatusTrimsSpaces() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.valueOf(" active ");

        assertEquals("ACTIVE", status.toString());
    }

    @Test
    void ensureDefaultStatusIsActive() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertEquals("ACTIVE", status.toString());
    }

    @Test
    void ensureDecommissionedFactoryCreatesDecommissionedStatus() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.decommissioned();

        assertEquals("DECOMMISSIONED", status.toString());
        assertTrue(status.isDecommissioned());
    }

    @Test
    void ensureActiveStatusIsNotDecommissioned() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertFalse(status.isDecommissioned());
    }

    @Test
    void ensureNullStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftOperationalStatus.valueOf(null));
    }

    @Test
    void ensureEmptyStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftOperationalStatus.valueOf("   "));
    }

    @Test
    void ensureInvalidStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AircraftOperationalStatus.valueOf("BROKEN"));
    }

    @Test
    void ensureSameStatusInstanceIsEqual() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertEquals(status, status);
    }

    @Test
    void ensureStatusesWithSameValueAreEqual() {
        final AircraftOperationalStatus first = AircraftOperationalStatus.valueOf("ACTIVE");
        final AircraftOperationalStatus second = AircraftOperationalStatus.valueOf("active");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureStatusesWithDifferentValuesAreNotEqual() {
        final AircraftOperationalStatus first = AircraftOperationalStatus.valueOf("ACTIVE");
        final AircraftOperationalStatus second = AircraftOperationalStatus.valueOf("DECOMMISSIONED");

        assertNotEquals(first, second);
    }

    @Test
    void ensureStatusIsNotEqualToDifferentObjectType() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertNotEquals(status, "ACTIVE");
    }

    @Test
    void ensureStatusIsNotEqualToNull() {
        final AircraftOperationalStatus status = AircraftOperationalStatus.active();

        assertNotEquals(null, status);
    }
}
