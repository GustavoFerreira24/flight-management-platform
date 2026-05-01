package isep.aisafe.aircraftmodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftModelTypeTest {

    @Test
    void ensureValidAircraftModelTypeIsAccepted() {
        // Arrange / Act
        final AircraftModelType type = AircraftModelType.valueOf("PASSENGER");

        // Assert
        assertEquals("PASSENGER", type.toString());
    }

    @Test
    void ensureNullAircraftModelTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelType.valueOf(null));
    }

    @Test
    void ensureEmptyAircraftModelTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelType.valueOf("   "));
    }

    @Test
    void ensureAircraftModelTypeIsNormalized() {
        // Arrange / Act
        final AircraftModelType type = AircraftModelType.valueOf(" passenger ");

        // Assert
        assertEquals("PASSENGER", type.toString());
    }

    @Test
    void ensureAircraftModelTypesWithSameValueAreEqual() {
        // Arrange
        final AircraftModelType first = AircraftModelType.valueOf("passenger");
        final AircraftModelType second = AircraftModelType.valueOf("PASSENGER");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
