package isep.aisafe.airport.domain;

import isep.aisafe.ICAOCode;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AirportBuilder.
 * Ensures the factory correctly assembles the Aggregate.
 */
class AirportBuilderTest {

    @Test
    void ensureBuilderCreatesAirportWithValidData() {
        AirportBuilder builder = new AirportBuilder();
        // Código válido (sem símbolos)
        AirControlArea area = new AirControlArea(AirControlAreaCode.valueOf("LPPTFIR"), "Lisbon");

        Airport airport = builder
                .withData("LPPT", "LIS", "Lisbon Airport", "Lisbon")
                .withLocation(38.7, -9.1, 114.0)
                .inArea(area)
                .build();

        assertNotNull(airport);
        assertEquals("LPPT", airport.identity().toString());
    }

    @Test
    void ensureBuilderFailsWhenMandatoryDataIsMissing() {
        AirportBuilder builder = new AirportBuilder();

        // Act & Assert
        // Should fail because withData/withLocation were not called,
        // leaving fields null or invalid for the Airport constructor
        assertThrows(Exception.class, () -> {
            builder.build();
        });
    }

    @Test
    void ensureBuilderPropagatesValidationErrors() {
        AirportBuilder builder = new AirportBuilder();

        // Act & Assert
        // The builder uses the ICAOCode constructor, so it should throw
        // if we pass a code that fails the regex (e.g., too long)
        assertThrows(IllegalArgumentException.class, () -> {
            builder.withData("INVALIDICAO", "LIS", "Name", "Town");
        });
    }
}