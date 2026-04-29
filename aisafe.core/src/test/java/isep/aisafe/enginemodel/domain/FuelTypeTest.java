package isep.aisafe.enginemodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuelTypeTest {

    @Test
    void ensureValidFuelTypeIsAccepted() {
        // Arrange / Act
        final FuelType fuelType = FuelType.valueOf("JET A-1");

        // Assert
        assertNotNull(fuelType);
    }

    @Test
    void ensureNullFuelTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> FuelType.valueOf(null));
    }

    @Test
    void ensureEmptyFuelTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> FuelType.valueOf("   "));
    }

    @Test
    void ensureFuelTypeIsNormalized() {
        // Arrange / Act
        final FuelType fuelType = FuelType.valueOf(" jet a-1 ");

        // Assert
        assertEquals("JET A-1", fuelType.toString());
    }

    @Test
    void ensureFuelTypesWithSameValueAreEqual() {
        // Arrange
        final FuelType first = FuelType.valueOf("jet a-1");
        final FuelType second = FuelType.valueOf("JET A-1");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
