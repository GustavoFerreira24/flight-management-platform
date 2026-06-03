package isep.aisafe.flight.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DepartureTest {

    @Test
    void ensureDepartureCanBeCreatedFromDayAndTime() {
        // Arrange
        final LocalDate day = LocalDate.of(2027, 1, 10);
        final LocalTime time = LocalTime.of(10, 30);

        // Act
        final Departure departure = Departure.valueOf(day, time);

        // Assert
        assertEquals(day, departure.day());
        assertEquals(time, departure.time());
        assertEquals(LocalDateTime.of(2027, 1, 10, 10, 30), departure.dateTime());
    }

    @Test
    void ensureDepartureCanBeCreatedFromDateTime() {
        // Arrange
        final LocalDateTime dateTime = LocalDateTime.of(2027, 1, 10, 10, 30);

        // Act
        final Departure departure = Departure.valueOf(dateTime);

        // Assert
        assertEquals(LocalDate.of(2027, 1, 10), departure.day());
        assertEquals(LocalTime.of(10, 30), departure.time());
        assertEquals(dateTime, departure.dateTime());
    }

    @Test
    void ensureInvalidDepartureDataIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> Departure.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Departure.valueOf(null, LocalTime.of(10, 30)));
        assertThrows(IllegalArgumentException.class, () -> Departure.valueOf(LocalDate.of(2027, 1, 10), null));
    }

    @Test
    void ensureEqualsAcceptsSameInstance() {
        // Arrange
        final Departure departure = Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 30));

        // Act
        final boolean result = departure.equals(departure);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensureEqualsRejectsDifferentType() {
        // Arrange
        final Departure departure = Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 30));

        // Act
        final boolean result = departure.equals("2027-01-10T10:30");

        // Assert
        assertFalse(result);
    }

    @Test
    void ensureEqualityAndHashCodeUseDayAndTime() {
        // Arrange
        final Departure first = Departure.valueOf(LocalDate.of(2027, 1, 10), LocalTime.of(10, 30));
        final Departure second = Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 30));
        final Departure third = Departure.valueOf(LocalDate.of(2027, 1, 10), LocalTime.of(11, 0));

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
    }

    @Test
    void ensureToStringUsesIsoDateTime() {
        // Arrange
        final Departure departure = Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 30));

        // Act
        final String result = departure.toString();

        // Assert
        assertEquals("2027-01-10T10:30", result);
    }
}
