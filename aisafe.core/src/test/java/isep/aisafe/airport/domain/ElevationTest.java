package isep.aisafe.airport.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Elevation value object.
 */
class ElevationTest {

    @Test
    void ensureElevationIsCreatedWithPositiveValue() {
        Elevation elevation = new Elevation(114.5);
        assertNotNull(elevation);
    }

    @Test
    void ensureElevationCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Elevation(-3.0);
        });
    }

    @Test
    void ensureToStringFormatsCorrectly() {
        Elevation elevation = new Elevation(100);
        assertEquals("100.0 m", elevation.toString());
    }

    @Test
    void ensureIdenticalElevationsAreEqual() {
        Elevation e1 = new Elevation(50.0);
        Elevation e2 = new Elevation(50.0);
        assertEquals(e1, e1);
    }
}