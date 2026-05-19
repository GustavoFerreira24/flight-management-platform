package isep.aisafe.aircraft.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CabinConfigurationTest {

    /**
     * Verifica que o construtor protegido existe para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final CabinConfiguration configuration = new CabinConfiguration();

        assertNotNull(configuration);
    }

    /**
     * Verifica que uma configuracao de cabine valida e aceite com o total correto.
     */
    @Test
    void ensureValidCabinConfigurationIsAccepted() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals(150, configuration.economySeats());
        assertEquals(12, configuration.businessSeats());
        assertEquals(4, configuration.firstClassSeats());
        assertEquals(166, configuration.totalSeats());
    }

    /**
     * Verifica que uma configuracao com apenas lugares economicos e aceite.
     */
    @Test
    void ensureCabinConfigurationWithOnlyEconomySeatsIsAccepted() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(100, 0, 0);

        assertEquals(100, configuration.totalSeats());
    }

    /**
     * Verifica que um numero negativo de lugares economicos e rejeitado.
     */
    @Test
    void ensureNegativeEconomySeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(-1, 10, 0));
    }

    /**
     * Verifica que um numero negativo de lugares executivos e rejeitado.
     */
    @Test
    void ensureNegativeBusinessSeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(100, -1, 0));
    }

    /**
     * Verifica que um numero negativo de lugares de primeira classe e rejeitado.
     */
    @Test
    void ensureNegativeFirstClassSeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(100, 10, -1));
    }

    /**
     * Verifica que uma configuracao de cabine sem lugares e rejeitada.
     */
    @Test
    void ensureCabinConfigurationWithoutSeatsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CabinConfiguration.valueOf(0, 0, 0));
    }

    /**
     * Verifica que a mesma instancia de configuracao de cabine e igual a si propria.
     */
    @Test
    void ensureSameCabinConfigurationInstanceIsEqual() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals(configuration, configuration);
    }

    /**
     * Verifica que configuracoes de cabine com os mesmos valores sao iguais.
     */
    @Test
    void ensureCabinConfigurationsWithSameValuesAreEqual() {
        final CabinConfiguration first = CabinConfiguration.valueOf(150, 12, 4);
        final CabinConfiguration second = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que configuracoes de cabine com valores diferentes nao sao iguais.
     */
    @Test
    void ensureCabinConfigurationsWithDifferentValuesAreNotEqual() {
        final CabinConfiguration first = CabinConfiguration.valueOf(150, 12, 4);
        final CabinConfiguration second = CabinConfiguration.valueOf(151, 12, 4);

        assertNotEquals(first, second);
    }

    /**
     * Verifica que uma configuracao de cabine nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureCabinConfigurationIsNotEqualToDifferentObjectType() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertNotEquals(configuration, "150/12/4");
    }

    /**
     * Verifica que uma configuracao de cabine nao e igual a nulo.
     */
    @Test
    void ensureCabinConfigurationIsNotEqualToNull() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertNotEquals(null, configuration);
    }

    /**
     * Verifica que a configuracao de cabine pode ser representada em texto.
     */
    @Test
    void ensureCabinConfigurationCanBeRepresentedAsText() {
        final CabinConfiguration configuration = CabinConfiguration.valueOf(150, 12, 4);

        assertEquals("Economy: 150, Business: 12, First Class: 4", configuration.toString());
    }
}
