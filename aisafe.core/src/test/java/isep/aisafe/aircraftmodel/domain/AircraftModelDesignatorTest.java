package isep.aisafe.aircraftmodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftModelDesignatorTest {

    @Test
    void ensureValidDesignatorIsAccepted() {
        // Arrange / Act
        final AircraftModelDesignator designator = AircraftModelDesignator.valueOf("A320neo");

        // Assert
        assertEquals("A320NEO", designator.toString());
    }

    @Test
    void ensureModelNameAndManufacturerBuildDesignator() {
        // Arrange / Act
        final AircraftModelDesignator designator = AircraftModelDesignator.valueOf("A320neo", "Airbus");

        // Assert
        assertEquals("A320NEO | AIRBUS", designator.toString());
    }

    @Test
    void ensureNullDesignatorIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelDesignator.valueOf(null));
    }

    @Test
    void ensureEmptyDesignatorIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelDesignator.valueOf("   "));
    }

    @Test
    void ensureNullModelNameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelDesignator.valueOf(null, "Airbus"));
    }

    @Test
    void ensureNullManufacturerIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftModelDesignator.valueOf("A320neo", null));
    }

    @Test
    void ensureDesignatorsWithSameValueAreEqual() {
        // Arrange
        final AircraftModelDesignator first = AircraftModelDesignator.valueOf(" a320neo ");
        final AircraftModelDesignator second = AircraftModelDesignator.valueOf("A320neo");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
