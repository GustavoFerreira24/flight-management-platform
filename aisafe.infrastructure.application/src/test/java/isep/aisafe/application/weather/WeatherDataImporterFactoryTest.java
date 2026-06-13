package isep.aisafe.application.weather;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the weather data importer factory.
 */
class WeatherDataImporterFactoryTest {

    /**
     * Purpose: verify CSV importer selection.
     * Scenario: request an importer for a CSV weather data file.
     * Expected output: a CSV weather data importer is returned.
     */
    @Test
    void ensureCsvWeatherDataImporterIsSelected() {
        final WeatherDataImporterFactory factory = new WeatherDataImporterFactory();

        final WeatherDataImporter importer = factory.importerFor(Path.of("weather-data.csv"));

        assertInstanceOf(CsvWeatherDataImporter.class, importer);
    }

    /**
     * Purpose: verify case-insensitive CSV importer selection.
     * Scenario: request an importer for a CSV file with uppercase extension.
     * Expected output: a CSV weather data importer is returned.
     */
    @Test
    void ensureCsvWeatherDataImporterSelectionIsCaseInsensitive() {
        final WeatherDataImporterFactory factory = new WeatherDataImporterFactory();

        final WeatherDataImporter importer = factory.importerFor(Path.of("WEATHER-DATA.CSV"));

        assertInstanceOf(CsvWeatherDataImporter.class, importer);
    }

    /**
     * Purpose: verify XLSX importer selection.
     * Scenario: request an importer for a XLSX weather data file.
     * Expected output: a XLSX weather data importer is returned.
     */
    @Test
    void ensureXlsxWeatherDataImporterIsSelected() {
        final WeatherDataImporterFactory factory = new WeatherDataImporterFactory();

        final WeatherDataImporter importer = factory.importerFor(Path.of("weather-data.xlsx"));

        assertInstanceOf(XlsxWeatherDataImporter.class, importer);
    }

    /**
     * Purpose: verify case-insensitive XLSX importer selection.
     * Scenario: request an importer for a XLSX file with uppercase extension.
     * Expected output: a XLSX weather data importer is returned.
     */
    @Test
    void ensureXlsxWeatherDataImporterSelectionIsCaseInsensitive() {
        final WeatherDataImporterFactory factory = new WeatherDataImporterFactory();

        final WeatherDataImporter importer = factory.importerFor(Path.of("WEATHER-DATA.XLSX"));

        assertInstanceOf(XlsxWeatherDataImporter.class, importer);
    }

    /**
     * Purpose: verify unsupported format handling.
     * Scenario: request an importer for a file extension that is not supported yet.
     * Expected output: an IllegalArgumentException is thrown with clear feedback.
     */
    @Test
    void ensureUnsupportedWeatherDataImportFormatIsRejected() {
        final WeatherDataImporterFactory factory = new WeatherDataImporterFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.importerFor(Path.of("weather-data.json")));
    }

    /**
     * Purpose: verify import file validation.
     * Scenario: request an importer without providing a file path.
     * Expected output: a NullPointerException is thrown before format selection.
     */
    @Test
    void ensureImportFileIsRequired() {
        final WeatherDataImporterFactory factory = new WeatherDataImporterFactory();

        assertThrows(NullPointerException.class, () -> factory.importerFor(null));
    }
}
