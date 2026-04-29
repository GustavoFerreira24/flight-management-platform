package isep.aisafe.enginemodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineTypeTest {

    @Test
    void ensureValidEngineTypeIsAccepted() {
        // Arrange / Act
        final EngineType type = EngineType.valueOf("TURBOFAN");

        // Assert
        assertNotNull(type);
    }

    @Test
    void ensureNullEngineTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> EngineType.valueOf(null));
    }

    @Test
    void ensureEmptyEngineTypeIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> EngineType.valueOf("   "));
    }

    @Test
    void ensureEngineTypeIsNormalized() {
        // Arrange / Act
        final EngineType type = EngineType.valueOf(" turbofan ");

        // Assert
        assertEquals("TURBOFAN", type.toString());
    }

    @Test
    void ensureEngineTypesWithSameValueAreEqual() {
        // Arrange
        final EngineType first = EngineType.valueOf("turbofan");
        final EngineType second = EngineType.valueOf("TURBOFAN");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
