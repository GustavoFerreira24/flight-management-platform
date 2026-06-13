package isep.aisafe.application.weather;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the XLSX weather data importer for the supplied US042 weather files.
 */
class XlsxWeatherDataImporterTest {

    /**
     * Purpose: verify successful parsing of the CW professor weather data file.
     * Scenario: import the supplied CW XLSX file with Wind sheet records.
     * Expected output: wind speed and wind direction measurements are created for each data row.
     */
    @Test
    void ensureCwXlsxWeatherDataIsImported() throws IOException {
        final XlsxWeatherDataImporter importer = new XlsxWeatherDataImporter();

        final List<ImportedWeatherMeasurement> measurements = importer.importFrom(resourcePath(
                "weather/us42/WeatherData_v0/CW_WeatherData_v0.xlsx"
        ));

        assertEquals(90, measurements.size());
        assertEquals(2, measurements.getFirst().sourceLine());
        assertEquals("2026-06-22", measurements.getFirst().day());
        assertEquals("121", measurements.getFirst().areaCode());
        assertEquals("IPMA", measurements.getFirst().providerName());
        assertEquals("SATELLITE", measurements.getFirst().providerType());
        assertEquals("WIND_SPEED", measurements.getFirst().parameter());
        assertEquals(28.75, measurements.getFirst().value(), 0.000001);
        assertEquals("KT", measurements.getFirst().unit());
        assertEquals(500.0, measurements.getFirst().altitude(), 0.000001);
        assertEquals(43.840454, measurements.getFirst().latitude(), 0.000001);
        assertEquals(-9.795711, measurements.getFirst().longitude(), 0.000001);
        assertEquals("WIND_DIRECTION", measurements.get(1).parameter());
        assertEquals(90.0, measurements.get(1).value(), 0.000001);
        assertEquals("DEG", measurements.get(1).unit());
        assertEquals(3, measurements.get(2).sourceLine());
    }

    /**
     * Purpose: verify successful parsing of the HP professor weather data file.
     * Scenario: import the supplied HP XLSX file with Wind sheet records.
     * Expected output: wind speed and wind direction measurements are created for each data row.
     */
    @Test
    void ensureHpXlsxWeatherDataIsImported() throws IOException {
        final XlsxWeatherDataImporter importer = new XlsxWeatherDataImporter();

        final List<ImportedWeatherMeasurement> measurements = importer.importFrom(resourcePath(
                "weather/us42/WeatherData_v0/HP_WeatherData_v0.xlsx"
        ));

        assertEquals(90, measurements.size());
        assertEquals("2026-06-22", measurements.getFirst().day());
        assertEquals("121", measurements.getFirst().areaCode());
        assertEquals("WIND_SPEED", measurements.getFirst().parameter());
        assertEquals(25.0, measurements.getFirst().value(), 0.000001);
        assertEquals("WIND_DIRECTION", measurements.get(1).parameter());
        assertEquals(90.0, measurements.get(1).value(), 0.000001);
    }

    private Path resourcePath(final String resourceName) {
        try {
            return Path.of(ClassLoader.getSystemResource(resourceName).toURI());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid test resource path: " + resourceName, ex);
        }
    }
}
