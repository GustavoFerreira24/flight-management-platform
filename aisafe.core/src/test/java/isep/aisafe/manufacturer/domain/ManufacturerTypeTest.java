package isep.aisafe.manufacturer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerTypeTest {

    @Test
    void ensureAircraftManufacturerTypeIsAccepted() {
        // Arrange / Act
        final ManufacturerType type = ManufacturerType.valueOf("AIRCRAFT");

        // Assert
        assertTrue(type.supportsAircraftModels());
        assertFalse(type.supportsEngineModels());
    }

    @Test
    void ensureEngineManufacturerTypeIsAccepted() {
        // Arrange / Act
        final ManufacturerType type = ManufacturerType.valueOf("ENGINE");

        // Assert
        assertFalse(type.supportsAircraftModels());
        assertTrue(type.supportsEngineModels());
    }

    @Test
    void ensureCombinedManufacturerTypeIsAccepted() {
        // Arrange / Act
        final ManufacturerType type = ManufacturerType.valueOf("AIRCRAFT_AND_ENGINE");

        // Assert
        assertTrue(type.supportsAircraftModels());
        assertTrue(type.supportsEngineModels());
    }

    @Test
    void ensureTypeIsNormalized() {
        // Arrange / Act
        final ManufacturerType type = ManufacturerType.valueOf(" aircraft and engine ");

        // Assert
        assertEquals("AIRCRAFT_AND_ENGINE", type.toString());
    }

    @Test
    void ensureNullTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> ManufacturerType.valueOf(null));
    }

    @Test
    void ensureEmptyTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> ManufacturerType.valueOf("   "));
    }

    @Test
    void ensureUnsupportedTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> ManufacturerType.valueOf("UNKNOWN"));
    }

    @Test
    void ensureTypesWithSameValueAreEqual() {
        // Arrange
        final ManufacturerType first = ManufacturerType.valueOf("aircraft and engine");
        final ManufacturerType second = ManufacturerType.valueOf("AIRCRAFT_AND_ENGINE");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
