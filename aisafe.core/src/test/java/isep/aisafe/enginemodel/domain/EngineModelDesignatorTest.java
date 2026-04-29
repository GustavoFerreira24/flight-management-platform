package isep.aisafe.enginemodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineModelDesignatorTest {

    @Test
    void ensureValidDesignatorIsAccepted() {
        // Arrange / Act
        final EngineModelDesignator designator = EngineModelDesignator.valueOf("LEAP-1A");

        // Assert
        assertNotNull(designator);
    }

    @Test
    void ensureModelNameAndManufacturerBuildDesignator() {
        // Arrange / Act
        final EngineModelDesignator designator =
                EngineModelDesignator.valueOf("LEAP-1A", "CFM International");

        // Assert
        assertEquals("LEAP-1A | CFM INTERNATIONAL", designator.toString());
    }

    @Test
    void ensureNullDesignatorIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> EngineModelDesignator.valueOf(null));
    }

    @Test
    void ensureEmptyDesignatorIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> EngineModelDesignator.valueOf("   "));
    }

    @Test
    void ensureNullModelNameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                EngineModelDesignator.valueOf(null, "CFM International"));
    }

    @Test
    void ensureNullManufacturerIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                EngineModelDesignator.valueOf("LEAP-1A", null));
    }

    @Test
    void ensureDesignatorsWithSameValueAreEqual() {
        // Arrange
        final EngineModelDesignator first = EngineModelDesignator.valueOf(" leap-1a ");
        final EngineModelDesignator second = EngineModelDesignator.valueOf("LEAP-1A");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
