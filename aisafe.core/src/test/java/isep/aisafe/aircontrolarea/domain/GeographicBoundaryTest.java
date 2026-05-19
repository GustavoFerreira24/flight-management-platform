package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicBoundaryTest {

    /**
     * Verifica que o construtor protegido existe para uso pelo ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final GeographicBoundary boundary = new GeographicBoundary();

        assertNotNull(boundary);
    }

    /**
     * Verifica que um limite geografico valido e aceite.
     */
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

    /**
     * Verifica que o limite geografico global e aceite.
     */
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

    /**
     * Verifica que a latitude minima abaixo do limite permitido e rejeitada.
     */
    @Test
    void ensureMinimumLatitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(-91.0, 45.0, -15.0, 5.0));
    }

    /**
     * Verifica que a latitude minima acima do limite permitido e rejeitada.
     */
    @Test
    void ensureMinimumLatitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(91.0, 92.0, -15.0, 5.0));
    }

    /**
     * Verifica que a latitude maxima abaixo do limite permitido e rejeitada.
     */
    @Test
    void ensureMaximumLatitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(-80.0, -91.0, -15.0, 5.0));
    }

    /**
     * Verifica que a latitude maxima acima do limite permitido e rejeitada.
     */
    @Test
    void ensureMaximumLatitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 91.0, -15.0, 5.0));
    }

    /**
     * Verifica que a longitude minima abaixo do limite permitido e rejeitada.
     */
    @Test
    void ensureMinimumLongitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, -181.0, 5.0));
    }

    /**
     * Verifica que a longitude minima acima do limite permitido e rejeitada.
     */
    @Test
    void ensureMinimumLongitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, 181.0, 182.0));
    }

    /**
     * Verifica que a longitude maxima abaixo do limite permitido e rejeitada.
     */
    @Test
    void ensureMaximumLongitudeBelowLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, -181.0));
    }

    /**
     * Verifica que a longitude maxima acima do limite permitido e rejeitada.
     */
    @Test
    void ensureMaximumLongitudeAboveLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 181.0));
    }

    /**
     * Verifica que latitude minima igual a latitude maxima e rejeitada.
     */
    @Test
    void ensureMinimumLatitudeEqualToMaximumLatitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 30.0, -15.0, 5.0));
    }

    /**
     * Verifica que latitude minima maior que latitude maxima e rejeitada.
     */
    @Test
    void ensureMinimumLatitudeGreaterThanMaximumLatitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(45.0, 30.0, -15.0, 5.0));
    }

    /**
     * Verifica que longitude minima igual a longitude maxima e rejeitada.
     */
    @Test
    void ensureMinimumLongitudeEqualToMaximumLongitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, 5.0, 5.0));
    }

    /**
     * Verifica que longitude minima maior que longitude maxima e rejeitada.
     */
    @Test
    void ensureMinimumLongitudeGreaterThanMaximumLongitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                GeographicBoundary.valueOf(30.0, 45.0, 5.0, -15.0));
    }

    /**
     * Verifica que a mesma instancia de limite geografico e igual a si propria.
     */
    @Test
    void ensureSameBoundaryInstanceIsEqual() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertEquals(boundary, boundary);
    }

    /**
     * Verifica que limites geograficos com os mesmos valores sao iguais.
     */
    @Test
    void ensureBoundariesWithSameValuesAreEqual() {
        final GeographicBoundary first = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);
        final GeographicBoundary second = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que limites geograficos com valores diferentes nao sao iguais.
     */
    @Test
    void ensureBoundariesWithDifferentValuesAreNotEqual() {
        final GeographicBoundary first = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);
        final GeographicBoundary second = GeographicBoundary.valueOf(31.0, 45.0, -15.0, 5.0);

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um limite geografico nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureBoundaryIsNotEqualToDifferentObjectType() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertNotEquals(boundary, "boundary");
    }

    /**
     * Verifica que um limite geografico nao e igual a nulo.
     */
    @Test
    void ensureBoundaryIsNotEqualToNull() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertNotEquals(null, boundary);
    }

    /**
     * Verifica que um limite geografico pode ser representado como texto.
     */
    @Test
    void ensureBoundaryCanBeRepresentedAsText() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertEquals("30.0,45.0,-15.0,5.0", boundary.toString());
    }

    /**
     * Verifica que coordenadas dentro do limite geografico sao aceites.
     */
    @Test
    void ensureCoordinatesInsideBoundaryAreAccepted() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertTrue(boundary.contains(38.72, -9.14));
        assertTrue(boundary.contains(30.0, -15.0));
        assertTrue(boundary.contains(45.0, 5.0));
    }

    /**
     * Verifica que coordenadas fora do limite geografico sao rejeitadas.
     */
    @Test
    void ensureCoordinatesOutsideBoundaryAreRejected() {
        final GeographicBoundary boundary = GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0);

        assertFalse(boundary.contains(29.99, -9.14));
        assertFalse(boundary.contains(45.01, -9.14));
        assertFalse(boundary.contains(38.72, -15.01));
        assertFalse(boundary.contains(38.72, 5.01));
    }
}
