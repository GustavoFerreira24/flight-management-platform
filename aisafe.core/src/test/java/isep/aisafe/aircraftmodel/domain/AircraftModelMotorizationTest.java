package isep.aisafe.aircraftmodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftModelMotorizationTest {

    @Test
    void ensureValidAircraftModelMotorizationIsAccepted() {
        // Arrange / Act
        final AircraftModelMotorization motorization = AircraftModelMotorization.valueOf("TURBOFAN");

        // Assert
        assertEquals("TURBOFAN", motorization.toString());
    }

    @Test
    void ensureNullAircraftModelMotorizationIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelMotorization.valueOf(null));
    }

    @Test
    void ensureEmptyAircraftModelMotorizationIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelMotorization.valueOf("   "));
    }

    @Test
    void ensureAircraftModelMotorizationIsNormalized() {
        // Arrange / Act
        final AircraftModelMotorization motorization = AircraftModelMotorization.valueOf(" turbofan ");

        // Assert
        assertEquals("TURBOFAN", motorization.toString());
    }

    @Test
    void ensureAircraftModelMotorizationsWithSameValueAreEqual() {
        // Arrange
        final AircraftModelMotorization first = AircraftModelMotorization.valueOf("turbofan");
        final AircraftModelMotorization second = AircraftModelMotorization.valueOf("TURBOFAN");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureCompatibleEngineTypeIsAccepted() {
        // Arrange
        final AircraftModelMotorization motorization = AircraftModelMotorization.valueOf("TURBOFAN");

        // Act / Assert
        assertTrue(motorization.isCompatibleWith(" turbofan "));
    }

    @Test
    void ensureDifferentEngineTypeIsRejectedAsIncompatible() {
        // Arrange
        final AircraftModelMotorization motorization = AircraftModelMotorization.valueOf("TURBOFAN");

        // Act / Assert
        assertFalse(motorization.isCompatibleWith("TURBOJET"));
    }
}
