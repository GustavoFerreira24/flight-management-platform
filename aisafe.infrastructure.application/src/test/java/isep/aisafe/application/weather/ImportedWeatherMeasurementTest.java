package isep.aisafe.application.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the imported weather measurement data transfer object.
 */
class ImportedWeatherMeasurementTest {

    /**
     * Purpose: verify imported weather measurement creation.
     * Scenario: create a record with all fields received from an import source.
     * Expected output: every submitted field is exposed by the corresponding accessor.
     */
    @Test
    void ensureImportedWeatherMeasurementCanBeCreated() {
        final ImportedWeatherMeasurement measurement = measurement(
                2,
                "2026-06-01",
                "LISACC",
                "IPMA",
                "STATION",
                "TEMPERATURE",
                18.5,
                "C",
                1200,
                38.72,
                -9.14
        );

        assertEquals(2, measurement.sourceLine());
        assertEquals("2026-06-01", measurement.day());
        assertEquals("LISACC", measurement.areaCode());
        assertEquals("IPMA", measurement.providerName());
        assertEquals("STATION", measurement.providerType());
        assertEquals("TEMPERATURE", measurement.parameter());
        assertEquals(18.5, measurement.value());
        assertEquals("C", measurement.unit());
        assertEquals(1200, measurement.altitude());
        assertEquals(38.72, measurement.latitude());
        assertEquals(-9.14, measurement.longitude());
    }

    /**
     * Purpose: verify imported weather measurement invariants.
     * Scenario: create records with invalid source line or missing textual fields.
     * Expected output: invalid data is rejected before reaching the import flow.
     */
    @Test
    void ensureImportedWeatherMeasurementRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> measurement(
                0, "2026-06-01", "LISACC", "IPMA", "STATION", "TEMPERATURE", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(NullPointerException.class, () -> measurement(
                1, null, "LISACC", "IPMA", "STATION", "TEMPERATURE", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(NullPointerException.class, () -> measurement(
                1, "2026-06-01", null, "IPMA", "STATION", "TEMPERATURE", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(NullPointerException.class, () -> measurement(
                1, "2026-06-01", "LISACC", null, "STATION", "TEMPERATURE", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(NullPointerException.class, () -> measurement(
                1, "2026-06-01", "LISACC", "IPMA", null, "TEMPERATURE", 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(NullPointerException.class, () -> measurement(
                1, "2026-06-01", "LISACC", "IPMA", "STATION", null, 18.5, "C", 1200, 38.72, -9.14
        ));
        assertThrows(NullPointerException.class, () -> measurement(
                1, "2026-06-01", "LISACC", "IPMA", "STATION", "TEMPERATURE", 18.5, null, 1200, 38.72, -9.14
        ));
    }

    private ImportedWeatherMeasurement measurement(
            final int sourceLine,
            final String day,
            final String areaCode,
            final String providerName,
            final String providerType,
            final String parameter,
            final double value,
            final String unit,
            final double altitude,
            final double latitude,
            final double longitude) {

        return new ImportedWeatherMeasurement(
                sourceLine,
                day,
                areaCode,
                providerName,
                providerType,
                parameter,
                value,
                unit,
                altitude,
                latitude,
                longitude
        );
    }
}
