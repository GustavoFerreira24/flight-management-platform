package isep.aisafe.manufacturer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerDesignatorTest {

    @Test
    void ensureValidDesignatorIsAccepted() {
        // Arrange / Act
        final ManufacturerDesignator designator = ManufacturerDesignator.valueOf("BOEING");

        // Assert
        assertNotNull(designator);
    }

    @Test
    void ensureDesignatorIsNormalized() {
        // Arrange / Act
        final ManufacturerDesignator designator = ManufacturerDesignator.valueOf("  general   electric  ");

        // Assert
        assertEquals("GENERAL ELECTRIC", designator.toString());
    }

    @Test
    void ensureNullDesignatorIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> ManufacturerDesignator.valueOf(null));
    }

    @Test
    void ensureEmptyDesignatorIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> ManufacturerDesignator.valueOf("   "));
    }

    @Test
    void ensureDesignatorsWithSameValueAreEqual() {
        // Arrange
        final ManufacturerDesignator first = ManufacturerDesignator.valueOf(" boeing ");
        final ManufacturerDesignator second = ManufacturerDesignator.valueOf("BOEING");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureDesignatorsCanBeCompared() {
        // Arrange
        final ManufacturerDesignator airbus = ManufacturerDesignator.valueOf("AIRBUS");
        final ManufacturerDesignator boeing = ManufacturerDesignator.valueOf("BOEING");

        // Act / Assert
        assertTrue(airbus.compareTo(boeing) < 0);
        assertTrue(boeing.compareTo(airbus) > 0);
    }
}
