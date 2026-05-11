package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CabinConfigurationTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final CabinConfiguration configuration = new CabinConfiguration();

        assertNotNull(configuration);
    }

    @Test
    void ensureValidCabinConfigurationIsAccepted() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals(150, configuration.economySeats());
        assertEquals(12, configuration.businessSeats());
        assertEquals(4, configuration.firstClassSeats());
        assertEquals(166, configuration.totalSeats());
    }

    @Test
    void ensureCabinConfigurationWithOnlyEconomySeatsIsAccepted() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(100, 0, 0);

        assertEquals(100, configuration.totalSeats());
    }

    @Test
    void ensureNegativeEconomySeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(-1, 10, 0));
    }

    @Test
    void ensureNegativeBusinessSeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(100, -1, 0));
    }

    @Test
    void ensureNegativeFirstClassSeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(100, 10, -1));
    }

    @Test
    void ensureCabinConfigurationWithoutSeatsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(0, 0, 0));
    }

    @Test
    void ensureSameCabinConfigurationInstanceIsEqual() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals(configuration, configuration);
    }

    @Test
    void ensureCabinConfigurationsWithSameValuesAreEqual() {
        final CabinConfiguration first = CabinConfiguration.valueOf(150, 12, 4);
        final CabinConfiguration second = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureCabinConfigurationsWithDifferentValuesAreNotEqual() {
        final CabinConfiguration first = CabinConfiguration.valueOf(150, 12, 4);
        final CabinConfiguration second = CabinConfiguration.valueOf(151, 12, 4);

        assertNotEquals(first, second);
    }

    @Test
    void ensureCabinConfigurationIsNotEqualToDifferentObjectType() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertNotEquals(configuration, "150/12/4");
    }

    @Test
    void ensureCabinConfigurationIsNotEqualToNull() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertNotEquals(null, configuration);
    }

    @Test
    void ensureCabinConfigurationCanBeRepresentedAsText() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals("Economy: 150, Business: 12, First Class: 4", configuration.toString());
    }
}