package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.weatherdata.domain.InMemoryWeatherDataRepository;
import isep.aisafe.weatherdata.domain.InMemoryWeatherProviderRepository;
import isep.aisafe.weatherdata.domain.WeatherProvider;
import isep.aisafe.weatherdata.domain.WeatherProviderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the CSV weather data importer.
 */
class CsvWeatherDataImporterTest {

    private static final String HEADER = "areaCode,latitude,longitude,providerName,providerType,parameter,value,unit,altitude,day";

    @TempDir
    private Path temporaryDirectory;

    /**
     * Purpose: verify successful CSV weather data parsing.
     * Scenario: import a CSV file with the expected header and two valid data lines.
     * Expected output: two imported weather measurement records are returned with their source lines.
     */
    @Test
    void ensureValidCsvWeatherDataIsImported() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = resourcePath("weather/us42/valid/ipma-weather-data.csv");

        final List<ImportedWeatherMeasurement> measurements = importer.importFrom(file);

        assertEquals(3, measurements.size());
        assertEquals(2, measurements.getFirst().sourceLine());
        assertEquals("2027-03-01", measurements.getFirst().day());
        assertEquals("LISACC", measurements.getFirst().areaCode());
        assertEquals("IPMA", measurements.getFirst().providerName());
        assertEquals("STATION", measurements.getFirst().providerType());
        assertEquals("TEMPERATURE", measurements.getFirst().parameter());
        assertEquals(18.5, measurements.getFirst().value());
        assertEquals("C", measurements.getFirst().unit());
        assertEquals(1200, measurements.getFirst().altitude());
        assertEquals(38.72, measurements.getFirst().latitude());
        assertEquals(-9.14, measurements.getFirst().longitude());
        assertEquals(3, measurements.get(1).sourceLine());
        assertEquals("GROUND_SENSOR", measurements.get(1).providerType());
        assertEquals("SATELLITE", measurements.get(2).providerType());
    }

    /**
     * Purpose: verify that surrounding spaces are accepted in CSV fields.
     * Scenario: import a CSV line with spaces around each comma-separated value.
     * Expected output: values are trimmed before creating the imported weather measurement.
     */
    @Test
    void ensureCsvFieldsAreTrimmed() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = writeCsv("trimmed-weather.csv", String.join(System.lineSeparator(),
                HEADER,
                " LISACC , 38.72 , -9.14 , IPMA , STATION , HUMIDITY , 75.0 , PERCENT , 900 , 2027-02-02 "
        ));

        final ImportedWeatherMeasurement measurement = importer.importFrom(file).getFirst();

        assertEquals("2027-02-02", measurement.day());
        assertEquals("LISACC", measurement.areaCode());
        assertEquals("IPMA", measurement.providerName());
        assertEquals("STATION", measurement.providerType());
        assertEquals("HUMIDITY", measurement.parameter());
        assertEquals(75.0, measurement.value());
    }

    /**
     * Purpose: verify that blank CSV data lines are ignored.
     * Scenario: import a CSV file with blank lines between valid records.
     * Expected output: only non-blank data lines are returned.
     */
    @Test
    void ensureBlankCsvLinesAreIgnored() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = resourcePath("weather/us42/valid/ipma-weather-data-with-blank-lines.csv");

        final List<ImportedWeatherMeasurement> measurements = importer.importFrom(file);

        assertEquals(2, measurements.size());
        assertEquals(3, measurements.getFirst().sourceLine());
        assertEquals(5, measurements.get(1).sourceLine());
    }

    /**
     * Purpose: verify empty CSV file handling.
     * Scenario: import a file without any line.
     * Expected output: an IllegalArgumentException explains that the header is required.
     */
    @Test
    void ensureEmptyCsvFileIsRejected() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = writeCsv("empty-weather.csv", "");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importFrom(file)
        );

        assertEquals("CSV weather data file must contain a header.", exception.getMessage());
    }

    /**
     * Purpose: verify CSV header validation.
     * Scenario: import a CSV file with an unexpected header field.
     * Expected output: an IllegalArgumentException explains that the header is invalid.
     */
    @Test
    void ensureInvalidCsvHeaderIsRejected() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = resourcePath("weather/us42/invalid/invalid-header.csv");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importFrom(file)
        );

        assertEquals("CSV weather data header is invalid.", exception.getMessage());
    }

    /**
     * Purpose: verify CSV header field count validation.
     * Scenario: import a CSV file with an incomplete header.
     * Expected output: an IllegalArgumentException explains the expected field count.
     */
    @Test
    void ensureCsvHeaderWithWrongFieldCountIsRejected() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = writeCsv("short-header-weather.csv", "day,areaCode");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importFrom(file)
        );

        assertEquals("CSV weather data header must contain 10 fields.", exception.getMessage());
    }

    /**
     * Purpose: verify malformed CSV record handling.
     * Scenario: import a CSV file with a data line missing fields.
     * Expected output: an IllegalArgumentException identifies the malformed line.
     */
    @Test
    void ensureCsvRecordWithWrongFieldCountIsRejected() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = resourcePath("weather/us42/invalid/missing-fields.csv");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importFrom(file)
        );

        assertEquals("Line 2 must contain 10 fields.", exception.getMessage());
    }

    /**
     * Purpose: verify numeric field validation.
     * Scenario: import a CSV file with an invalid measurement value.
     * Expected output: an IllegalArgumentException identifies the line and numeric field.
     */
    @Test
    void ensureCsvRecordWithInvalidValueIsRejected() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final Path file = resourcePath("weather/us42/invalid/invalid-numeric-value.csv");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importFrom(file)
        );

        assertEquals("Line 2 has invalid numeric value for value.", exception.getMessage());
    }

    /**
     * Purpose: verify numeric field validation for location fields.
     * Scenario: import CSV records with invalid altitude, latitude and longitude values.
     * Expected output: each invalid numeric field is rejected with a clear message.
     */
    @Test
    void ensureCsvRecordWithInvalidLocationNumbersIsRejected() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();

        assertCsvNumericFailure(importer, "altitude", "wrong", "Line 2 has invalid numeric value for altitude.");
        assertCsvNumericFailure(importer, "latitude", "wrong", "Line 2 has invalid numeric value for latitude.");
        assertCsvNumericFailure(importer, "longitude", "wrong", "Line 2 has invalid numeric value for longitude.");
    }

    /**
     * Purpose: verify required file validation.
     * Scenario: invoke the importer without a file path.
     * Expected output: a NullPointerException is thrown before reading.
     */
    @Test
    void ensureCsvFileIsRequired() {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();

        assertThrows(NullPointerException.class, () -> importer.importFrom(null));
    }

    /**
     * Purpose: verify integration between CSV importer and bulk import controller.
     * Scenario: the controller imports a valid CSV file using the CSV importer.
     * Expected output: all CSV records are persisted by the bulk import flow.
     */
    @Test
    void ensureCsvImporterCanBeUsedByBulkImportController() throws IOException {
        final CsvWeatherDataImporter importer = new CsvWeatherDataImporter();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final InMemoryWeatherProviderRepository weatherProviderRepository = new InMemoryWeatherProviderRepository();
        weatherProviderRepository.save(new WeatherProvider(
                "IPMA",
                List.of(WeatherProviderType.valueOf("STATION"))
        ));
        final InMemoryAirControlAreaRepository airControlAreaRepository = new InMemoryAirControlAreaRepository();
        airControlAreaRepository.save(new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        ));
        final ImportBulkWeatherDataController controller = new ImportBulkWeatherDataController(
                new CapturingAuthorizationService(),
                weatherDataRepository,
                airControlAreaRepository,
                weatherProviderRepository
        );
        final Path file = writeCsv("controller-weather.csv", String.join(System.lineSeparator(),
                HEADER,
                "LISACC,38.72,-9.14,IPMA,STATION,TEMPERATURE,18.5,C,1200,2027-02-04"
        ));

        final BulkWeatherImportResult result = controller.importFromFile(file, importer);

        assertTrue(result.isFullyImported());
        assertEquals(1, result.importedRecords());
        assertEquals(1, weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2027, 2, 4),
                AirControlAreaCode.valueOf("LISACC")
        ).orElseThrow().measurements().size());
    }

    private void assertCsvNumericFailure(
            final CsvWeatherDataImporter importer,
            final String fieldName,
            final String invalidValue,
            final String expectedMessage) throws IOException {

        final String value = "altitude".equals(fieldName) ? invalidValue : "1200";
        final String latitude = "latitude".equals(fieldName) ? invalidValue : "38.72";
        final String longitude = "longitude".equals(fieldName) ? invalidValue : "-9.14";
        final Path file = writeCsv("invalid-" + fieldName + "-weather.csv", String.join(System.lineSeparator(),
                HEADER,
                "LISACC," + latitude + "," + longitude + ",IPMA,STATION,TEMPERATURE,18.5,C," + value + ",2027-02-01"
        ));

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importFrom(file)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    private Path writeCsv(final String fileName, final String source) throws IOException {
        final Path file = temporaryDirectory.resolve(fileName);
        Files.writeString(file, source);
        return file;
    }

    private Path resourcePath(final String resourceName) {
        try {
            return Path.of(ClassLoader.getSystemResource(resourceName).toURI());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid test resource path: " + resourceName, ex);
        }
    }
}
