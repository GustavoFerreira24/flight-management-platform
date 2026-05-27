package isep.aisafe.flightroute.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightRouteTypeTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final FlightRouteType type = new FlightRouteType();

        assertNotNull(type);
    }

    @Test
    void ensureValidRouteTypeIsAccepted() {
        final FlightRouteType type = FlightRouteType.valueOf("seasonal route");

        assertEquals("SEASONAL_ROUTE", type.toString());
    }

    @Test
    void ensureDefaultRouteTypeIsRegular() {
        final FlightRouteType type = FlightRouteType.regular();

        assertEquals("REGULAR", type.toString());
    }

    @Test
    void ensureInvalidRouteTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FlightRouteType.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> FlightRouteType.valueOf("   "));
        assertThrows(IllegalArgumentException.class, () -> FlightRouteType.valueOf("ROUTE-1"));
    }

    @Test
    void ensureRouteTypesWithSameValueAreEqual() {
        final FlightRouteType first = FlightRouteType.valueOf("regular");
        final FlightRouteType second = FlightRouteType.valueOf("REGULAR");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
