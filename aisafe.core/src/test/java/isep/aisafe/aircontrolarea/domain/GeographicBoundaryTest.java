package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicBoundaryTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final GeographicBoundary boundary = new GeographicBoundary();

        assertNotNull(boundary);
    }

    @Test
    void ensureValidBoundaryIsAccepted() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(
                30.0,
                45.0,
                -15.0,
                5.0
        );

        assertEquals(30.0, boundary.minLatitude());
        assertEquals(45.0, boundary.maxLatitude());
        assertEquals(-15.0, boundary.minLongitude());
        assertEquals(5.0, boundary.maxLongitude());
    }

    @Test
    void ensureWorldBoundaryIsAccepted() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(
                -90.0,
                90.0,
                -180.0,
                180.0
        );

        assertEquals(-90.0, boundary.minLatitude());
        assertEquals(90.0, boundary.maxLatitude());
        assertEquals(-180.0, boundary.minLongitude());
        assertEquals(180.0, boundary.maxLongitude());
    }

    @Test
    void ensureMinimumLatitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(-91.0, 45.0, -15.0, 5.0));
    }

    @Test
    void ensureMinimumLatitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(91.0, 92.0, -15.0, 5.0));
    }

    @Test
    void ensureMaximumLatitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(-80.0, -91.0, -15.0, 5.0));
    }

    @Test
    void ensureMaximumLatitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 91.0, -15.0, 5.0));
    }

    @Test
    void ensureMinimumLongitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, -181.0, 5.0));
    }

    @Test
    void ensureMinimumLongitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, 181.0, 182.0));
    }

    @Test
    void ensureMaximumLongitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, -181.0));
    }

    @Test
    void ensureMaximumLongitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 181.0));
    }

    @Test
    void ensureMinimumLatitudeEqualToMaximumLatitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 30.0, -15.0, 5.0));
    }

    @Test
    void ensureMinimumLatitudeGreaterThanMaximumLatitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(45.0, 30.0, -15.0, 5.0));
    }

    @Test
    void ensureMinimumLongitudeEqualToMaximumLongitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, 5.0, 5.0));
    }

    @Test
    void ensureMinimumLongitudeGreaterThanMaximumLongitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, 5.0, -15.0));
    }

    @Test
    void ensureSameBoundaryInstanceIsEqual() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertEquals(boundary, boundary);
    }

    @Test
    void ensureBoundariesWithSameValuesAreEqual() {
        final GeographicBoundary first = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);
        final GeographicBoundary second = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureBoundariesWithDifferentValuesAreNotEqual() {
        final GeographicBoundary first = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);
        final GeographicBoundary second = GeographicBoundary.valueOf(31.0, 45.0, -15.0, 5.0);

        assertNotEquals(first, second);
    }

    @Test
    void ensureBoundaryIsNotEqualToDifferentObjectType() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertNotEquals(boundary, "boundary");
    }

    @Test
    void ensureBoundaryIsNotEqualToNull() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertNotEquals(null, boundary);
    }

    @Test
    void ensureBoundaryCanBeRepresentedAsText() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertEquals("30.0,45.0,-15.0,5.0", boundary.toString());
    }

    @Test
    void ensureCoordinatesInsideBoundaryAreAccepted() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertTrue(boundary.contains(38.72, -9.14));
        assertTrue(boundary.contains(30.0, -15.0));
        assertTrue(boundary.contains(45.0, 5.0));
    }

    @Test
    void ensureCoordinatesOutsideBoundaryAreRejected() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertFalse(boundary.contains(29.99, -9.14));
        assertFalse(boundary.contains(45.01, -9.14));
        assertFalse(boundary.contains(38.72, -15.01));
        assertFalse(boundary.contains(38.72, 5.01));
    }
}
