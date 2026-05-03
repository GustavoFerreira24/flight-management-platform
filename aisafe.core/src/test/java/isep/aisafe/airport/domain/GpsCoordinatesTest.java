package isep.aisafe.airport.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GpsCoordinatesTest {
    @Test
    void ensureValidCoordinatesAreAccepted() {
        assertNotNull(new GpsCoordinates(38.0, -9.0));
    }

    @Test
    void ensureInvalidLatitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GpsCoordinates(91.0, 0.0));
    }
}