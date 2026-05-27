package isep.aisafe.flightroute.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightRouteStatusTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final FlightRouteStatus status = new FlightRouteStatus();

        assertNotNull(status);
    }

    @Test
    void ensureActiveStatusIsAccepted() {
        final FlightRouteStatus status = FlightRouteStatus.valueOf("ACTIVE");

        assertEquals("ACTIVE", status.toString());
        assertTrue(status.isActive());
    }

    @Test
    void ensureInactiveStatusIsAccepted() {
        final FlightRouteStatus status = FlightRouteStatus.valueOf("INACTIVE");

        assertEquals("INACTIVE", status.toString());
        assertFalse(status.isActive());
    }

    @Test
    void ensureStatusIsNormalized() {
        final FlightRouteStatus status = FlightRouteStatus.valueOf(" active ");

        assertEquals("ACTIVE", status.toString());
    }

    @Test
    void ensureFactoriesCreateExpectedStatuses() {
        assertEquals("ACTIVE", FlightRouteStatus.active().toString());
        assertEquals("INACTIVE", FlightRouteStatus.inactive().toString());
    }

    @Test
    void ensureInvalidStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FlightRouteStatus.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> FlightRouteStatus.valueOf("   "));
        assertThrows(IllegalArgumentException.class, () -> FlightRouteStatus.valueOf("SUSPENDED"));
    }

    @Test
    void ensureStatusesWithSameValueAreEqual() {
        final FlightRouteStatus first = FlightRouteStatus.valueOf("ACTIVE");
        final FlightRouteStatus second = FlightRouteStatus.valueOf("active");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
