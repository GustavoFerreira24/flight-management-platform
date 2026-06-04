package isep.aisafe.flightplan.domain;

import isep.aisafe.aircontrolarea.domain.FuelUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuelQuantityTest {

    @Test
    void ensureFuelQuantityCanBeCreated() {
        // Arrange
        final double amount = 5000.0;
        final FuelUnit unit = FuelUnit.valueOf("KG");

        // Act
        final FuelQuantity fuelQuantity = FuelQuantity.valueOf(amount, unit);

        // Assert
        assertEquals(amount, fuelQuantity.amount());
        assertEquals(unit, fuelQuantity.unit());
    }

    @Test
    void ensureInvalidFuelQuantityDataIsRejected() {
        // Arrange
        final FuelUnit unit = FuelUnit.valueOf("KG");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> FuelQuantity.valueOf(0, unit));
        assertThrows(IllegalArgumentException.class, () -> FuelQuantity.valueOf(-1, unit));
        assertThrows(IllegalArgumentException.class, () -> FuelQuantity.valueOf(1, null));
    }

    @Test
    void ensureEqualsAcceptsSameInstance() {
        // Arrange
        final FuelQuantity fuelQuantity = FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));

        // Act
        final boolean result = fuelQuantity.equals(fuelQuantity);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensureEqualsRejectsDifferentType() {
        // Arrange
        final FuelQuantity fuelQuantity = FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));

        // Act
        final boolean result = fuelQuantity.equals("5000 KG");

        // Assert
        assertFalse(result);
    }

    @Test
    void ensureEqualityAndHashCodeUseAmountAndUnit() {
        // Arrange
        final FuelQuantity first = FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));
        final FuelQuantity second = FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));
        final FuelQuantity third = FuelQuantity.valueOf(6000, FuelUnit.valueOf("KG"));

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
    }

    @Test
    void ensureToStringIncludesAmountAndUnit() {
        // Arrange
        final FuelQuantity fuelQuantity = FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));

        // Act
        final String result = fuelQuantity.toString();

        // Assert
        assertEquals("5000.0 KG", result);
    }
}
