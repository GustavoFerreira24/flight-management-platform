package isep.aisafe.flightroute.domain;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteDesignatorTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final RouteDesignator designator = new RouteDesignator();

        assertNotNull(designator);
    }

    @Test
    void ensureValidRouteDesignatorIsAccepted() {
        final RouteDesignator designator = RouteDesignator.valueOf("TP123");

        assertEquals("TP123", designator.toString());
    }

    @Test
    void ensureRouteDesignatorIsNormalized() {
        final RouteDesignator designator = RouteDesignator.valueOf(" tp123 ");

        assertEquals("TP123", designator.toString());
    }

    @Test
    void ensureRouteDesignatorAcceptsOneDigit() {
        final RouteDesignator designator = RouteDesignator.valueOf("TP1");

        assertEquals("TP1", designator.toString());
    }

    @Test
    void ensureRouteDesignatorAcceptsFourDigits() {
        final RouteDesignator designator = RouteDesignator.valueOf("TP1234");

        assertEquals("TP1234", designator.toString());
    }

    @Test
    void ensureNullRouteDesignatorIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RouteDesignator.valueOf(null));
    }

    @Test
    void ensureEmptyRouteDesignatorIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RouteDesignator.valueOf("   "));
    }

    @Test
    void ensureRouteDesignatorWithoutDigitsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RouteDesignator.valueOf("TP"));
    }

    @Test
    void ensureRouteDesignatorWithTooManyDigitsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RouteDesignator.valueOf("TP12345"));
    }

    @Test
    void ensureRouteDesignatorWithInvalidCompanyPartIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RouteDesignator.valueOf("T1123"));
    }

    @Test
    void ensureRouteDesignatorBelongsToCompanyWithMatchingIataCode() {
        final RouteDesignator designator = RouteDesignator.valueOf("TP123");

        assertTrue(designator.belongsTo(company("TAP Air Portugal", "Portugal", "TP", "TAP")));
        assertFalse(designator.belongsTo(company("Ryanair", "Ireland", "FR", "RYR")));
    }

    @Test
    void ensureRouteDesignatorsWithSameValueAreEqual() {
        final RouteDesignator first = RouteDesignator.valueOf("TP123");
        final RouteDesignator second = RouteDesignator.valueOf("tp123");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureRouteDesignatorsWithDifferentValuesAreNotEqual() {
        final RouteDesignator first = RouteDesignator.valueOf("TP123");
        final RouteDesignator second = RouteDesignator.valueOf("TP124");

        assertNotEquals(first, second);
    }

    @Test
    void ensureRouteDesignatorsCanBeCompared() {
        final RouteDesignator first = RouteDesignator.valueOf("AA1");
        final RouteDesignator second = RouteDesignator.valueOf("BB1");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(RouteDesignator.valueOf("AA1")));
    }

    private AirTransportCompany company(
            final String name,
            final String country,
            final String iata,
            final String icao) {

        return new AirTransportCompany(
                name,
                Country.valueOf(country),
                IATACode.valueOf(iata),
                ICAOCode.valueOf(icao)
        );
    }
}
