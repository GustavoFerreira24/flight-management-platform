package isep.aisafe.flight.domain;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightDesignatorTest {

    @Test
    void ensureFlightDesignatorCanBeCreated() {
        // Arrange
        final String value = "TP123";

        // Act
        final FlightDesignator designator = FlightDesignator.valueOf(value);

        // Assert
        assertEquals("TP123", designator.toString());
    }

    @Test
    void ensureFlightDesignatorIsNormalized() {
        // Arrange
        final String value = " tp123a ";

        // Act
        final FlightDesignator designator = FlightDesignator.valueOf(value);

        // Assert
        assertEquals("TP123A", designator.toString());
    }

    @Test
    void ensureFlightDesignatorAcceptsOneToFourDigitsAndOptionalSuffix() {
        // Arrange / Act
        final FlightDesignator oneDigit = FlightDesignator.valueOf("TP1");
        final FlightDesignator twoDigits = FlightDesignator.valueOf("TP12");
        final FlightDesignator threeDigits = FlightDesignator.valueOf("TP123");
        final FlightDesignator fourDigits = FlightDesignator.valueOf("TP1234");
        final FlightDesignator suffix = FlightDesignator.valueOf("TP1234A");

        // Assert
        assertEquals("TP1", oneDigit.toString());
        assertEquals("TP12", twoDigits.toString());
        assertEquals("TP123", threeDigits.toString());
        assertEquals("TP1234", fourDigits.toString());
        assertEquals("TP1234A", suffix.toString());
    }

    @Test
    void ensureInvalidFlightDesignatorsAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf(""));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf(" "));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf("T123"));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf("TP"));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf("TP12345"));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf("TP123AB"));
        assertThrows(IllegalArgumentException.class, () -> FlightDesignator.valueOf("TAP123"));
    }

    @Test
    void ensureBelongsToChecksCompanyIataPrefix() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");
        final AirTransportCompany tap = company("TP");
        final AirTransportCompany ryanair = company("FR");

        // Act
        final boolean belongsToTap = designator.belongsTo(tap);
        final boolean belongsToRyanair = designator.belongsTo(ryanair);

        // Assert
        assertTrue(belongsToTap);
        assertFalse(belongsToRyanair);
    }

    @Test
    void ensureBelongsToRejectsNullCompany() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> designator.belongsTo(null));
    }

    @Test
    void ensureEqualsAcceptsSameInstance() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");

        // Act
        final boolean result = designator.equals(designator);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensureEqualsRejectsDifferentType() {
        // Arrange
        final FlightDesignator designator = FlightDesignator.valueOf("TP123");

        // Act
        final boolean result = designator.equals("TP123");

        // Assert
        assertFalse(result);
    }

    @Test
    void ensureEqualityAndHashCodeUseValue() {
        // Arrange
        final FlightDesignator first = FlightDesignator.valueOf("TP123");
        final FlightDesignator second = FlightDesignator.valueOf("tp123");
        final FlightDesignator third = FlightDesignator.valueOf("TP124");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
    }

    @Test
    void ensureComparisonUsesValue() {
        // Arrange
        final FlightDesignator first = FlightDesignator.valueOf("TP123");
        final FlightDesignator second = FlightDesignator.valueOf("TP124");

        // Act
        final int result = first.compareTo(second);

        // Assert
        assertTrue(result < 0);
    }

    private AirTransportCompany company(final String iata) {
        return new AirTransportCompany(
                iata + " Company",
                Country.valueOf("Portugal"),
                IATACode.valueOf(iata),
                ICAOCode.valueOf(iata + "A")
        );
    }
}
