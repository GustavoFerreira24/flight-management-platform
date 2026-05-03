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
    void ensureElevationCanBeNegative() {
        // Some airports are below sea level (e.g., -3 meters)
        Elevation elevation = new Elevation(-3.0);
        assertNotNull(elevation);
    }

    @Test
    void ensureToStringFormatsCorrectly() {
        Elevation elevation = new Elevation(100);
        // Checking if it follows the "100.0 m" format we defined
        assertEquals("100.0 m", elevation.toString());
    }

    @Test
    void ensureIdenticalElevationsAreEqual() {
        Elevation e1 = new Elevation(50.0);
        Elevation e2 = new Elevation(50.0);
        assertEquals(e1, e1); // Identity
        // If you implemented equals/hashCode (recommended for Value Objects)
        // assertEquals(e1, e2);
    }
}