package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.weatherdata.domain.InMemoryWeatherDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the application flow that imports multiple weather data records from a file.
 */
class ImportBulkWeatherDataControllerTest {

    @TempDir
    private Path temporaryDirectory;

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting test doubles.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        assertNotNull(new ImportBulkWeatherDataController());
    }

    /**
     * Purpose: verify constructor dependency validation.
     * Scenario: create the controller with null injected dependencies.
     * Expected output: a NullPointerException is thrown for each missing dependency.
     */
    @Test
    void ensureConstructorRejectsNullDependencies() {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final InMemoryAirControlAreaRepository airControlAreaRepository = new InMemoryAirControlAreaRepository();

        assertThrows(NullPointerException.class, () -> new ImportBulkWeatherDataController(
                null,
                weatherDataRepository,
                airControlAreaRepository
        ));
        assertThrows(NullPointerException.class, () -> new ImportBulkWeatherDataController(
                authz,
                null,
                airControlAreaRepository
        ));
        assertThrows(NullPointerException.class, () -> new ImportBulkWeatherDataController(
                authz,
                weatherDataRepository,
                null
        ));
    }

    /**
     * Purpose: verify successful bulk import.
     * Scenario: an importer returns two valid records for the same day and air control area.
     * Expected output: both records are persisted in the same weather data aggregate.
     */
    @Test
    void ensureValidWeatherMeasurementsAreImported() throws IOException {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final ImportBulkWeatherDataController controller = controller(authz, weatherDataRepository);
        final Path file = readableFile();
        final WeatherDataImporter importer = fixedImporter(List.of(
                measurement(2, "2027-01-10", "temperature", 18.5, "C", 38.72, -9.14),
                measurement(3, "2027-01-10", "wind speed", 12.0, "M/S", 38.80, -9.10)
        ));

        final BulkWeatherImportResult result = controller.importFromFile(file, importer);

        assertTrue(result.isFullyImported());
        assertFalse(result.hasErrors());
        assertEquals(2, result.readRecords());
        assertEquals(2, result.importedRecords());
        assertArrayEquals(AisafePermissions.WEATHER_DATA_MANAGEMENT, authz.requestedRoles());
        assertEquals(2, weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2027, 1, 10),
                AirControlAreaCode.valueOf("LISACC")
        ).orElseThrow().measurements().size());
    }

    /**
     * Purpose: verify that invalid imported records are reported and not persisted.
     * Scenario: one valid record and one record outside the air control area are imported.
     * Expected output: the valid record is persisted and the invalid record is reported with its source line.
     */
    @Test
    void ensureInvalidWeatherMeasurementsAreReported() throws IOException {
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );
        final Path file = readableFile();
        final WeatherDataImporter importer = fixedImporter(List.of(
                measurement(2, "2027-01-11", "temperature", 18.5, "C", 38.72, -9.14),
                measurement(3, "2027-01-11", "humidity", 75.0, "PERCENT", 50.0, 20.0)
        ));

        final BulkWeatherImportResult result = controller.importFromFile(file, importer);

        assertFalse(result.isFullyImported());
        assertTrue(result.hasErrors());
        assertEquals(2, result.readRecords());
        assertEquals(1, result.importedRecords());
        assertEquals(1, result.errors().size());
        assertEquals(3, result.errors().getFirst().sourceLine());
        assertEquals("Weather coordinates must be inside the air control area.", result.errors().getFirst().message());
        assertEquals(1, weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2027, 1, 11),
                AirControlAreaCode.valueOf("LISACC")
        ).orElseThrow().measurements().size());
    }

    /**
     * Purpose: verify that unknown air control areas are rejected during bulk import.
     * Scenario: an imported record references an area code that does not exist.
     * Expected output: no record is persisted and the result contains a clear error.
     */
    @Test
    void ensureUnknownAirControlAreaIsRejected() throws IOException {
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );
        final Path file = readableFile();
        final WeatherDataImporter importer = fixedImporter(List.of(new ImportedWeatherMeasurement(
                2,
                "2027-01-12",
                "UNKNOWN",
                "IPMA",
                "STATION",
                "temperature",
                18.5,
                "C",
                1200,
                38.72,
                -9.14
        )));

        final BulkWeatherImportResult result = controller.importFromFile(file, importer);

        assertEquals(1, result.readRecords());
        assertEquals(0, result.importedRecords());
        assertEquals("Air control area not found.", result.errors().getFirst().message());
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2027, 1, 12),
                AirControlAreaCode.valueOf("UNKNOWN")
        ).isEmpty());
    }

    /**
     * Purpose: verify imported required weather attributes validation.
     * Scenario: imported records have blank day, provider, parameter and unit values.
     * Expected output: each invalid record is rejected with clear feedback and nothing is persisted.
     */
    @Test
    void ensureRequiredImportedWeatherAttributesAreReported() throws IOException {
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );
        final Path file = readableFile();
        final WeatherDataImporter importer = fixedImporter(List.of(
                importedMeasurement(2, " ", "LISACC", "IPMA", "STATION", "temperature", 18.5, "C", 1200, 38.72, -9.14),
                importedMeasurement(3, "2027-01-13", "LISACC", " ", "STATION", "temperature", 18.5, "C", 1200, 38.72, -9.14),
                importedMeasurement(4, "2027-01-13", "LISACC", "IPMA", "STATION", " ", 18.5, "C", 1200, 38.72, -9.14),
                importedMeasurement(5, "2027-01-13", "LISACC", "IPMA", "STATION", "temperature", 18.5, " ", 1200, 38.72, -9.14)
        ));

        final BulkWeatherImportResult result = controller.importFromFile(file, importer);

        assertEquals(4, result.readRecords());
        assertEquals(0, result.importedRecords());
        assertEquals(4, result.errors().size());
        assertEquals("Weather data day is required.", result.errors().get(0).message());
        assertEquals("Weather provider name is required.", result.errors().get(1).message());
        assertEquals("Weather parameter is required.", result.errors().get(2).message());
        assertEquals("Weather unit is required.", result.errors().get(3).message());
        assertTrue(weatherDataRepository.findByDayAndAirControlArea(
                LocalDate.of(2027, 1, 13),
                AirControlAreaCode.valueOf("LISACC")
        ).isEmpty());
    }

    /**
     * Purpose: verify imported weather date validation.
     * Scenario: an imported record has a malformed ISO date.
     * Expected output: the record is rejected with clear feedback and no data is persisted.
     */
    @Test
    void ensureInvalidImportedWeatherDateIsReported() throws IOException {
        final InMemoryWeatherDataRepository weatherDataRepository = new InMemoryWeatherDataRepository();
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                weatherDataRepository
        );
        final WeatherDataImporter importer = fixedImporter(List.of(importedMeasurement(
                2, "01-13-2027", "LISACC", "IPMA", "STATION", "temperature", 18.5, "C", 1200, 38.72, -9.14
        )));

        final BulkWeatherImportResult result = controller.importFromFile(readableFile(), importer);

        assertEquals(1, result.readRecords());
        assertEquals(0, result.importedRecords());
        assertEquals(2, result.errors().getFirst().sourceLine());
        assertEquals("Weather data day must use ISO date format YYYY-MM-DD.", result.errors().getFirst().message());
    }

    /**
     * Purpose: verify imported weather source validation.
     * Scenario: an imported record has blank provider type.
     * Expected output: the record is rejected with clear feedback.
     */
    @Test
    void ensureInvalidImportedWeatherSourceIsReported() throws IOException {
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );
        final WeatherDataImporter importer = fixedImporter(List.of(importedMeasurement(
                2, "2027-01-14", "LISACC", "IPMA", " ", "temperature", 18.5, "C", 1200, 38.72, -9.14
        )));

        final BulkWeatherImportResult result = controller.importFromFile(readableFile(), importer);

        assertEquals(0, result.importedRecords());
        assertEquals("Weather provider type is required.", result.errors().getFirst().message());
    }

    /**
     * Purpose: verify imported weather domain validation.
     * Scenario: imported records violate value range, unit compatibility and coordinate range rules.
     * Expected output: invalid records are reported individually.
     */
    @Test
    void ensureInvalidImportedWeatherDomainValuesAreReported() throws IOException {
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );
        final WeatherDataImporter importer = fixedImporter(List.of(
                importedMeasurement(2, "2027-01-15", "LISACC", "IPMA", "STATION", "humidity", 150, "PERCENT", 1200, 38.72, -9.14),
                importedMeasurement(3, "2027-01-15", "LISACC", "IPMA", "STATION", "temperature", 18.5, "M/S", 1200, 38.72, -9.14),
                importedMeasurement(4, "2027-01-15", "LISACC", "IPMA", "STATION", "temperature", 18.5, "C", 1200, 95.0, -9.14)
        ));

        final BulkWeatherImportResult result = controller.importFromFile(readableFile(), importer);

        assertEquals(3, result.readRecords());
        assertEquals(0, result.importedRecords());
        assertEquals("Humidity must be between 0 and 100 percent.", result.errors().get(0).message());
        assertEquals("Weather unit is not compatible with parameter.", result.errors().get(1).message());
        assertEquals("Weather coordinates must be inside the air control area.", result.errors().get(2).message());
    }

    /**
     * Purpose: verify import file validation.
     * Scenario: call the import flow with a missing file.
     * Expected output: an IllegalArgumentException is thrown before the importer is invoked.
     */
    @Test
    void ensureMissingFileIsRejectedBeforeImport() {
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );
        final Path missingFile = temporaryDirectory.resolve("missing.weather");

        assertThrows(IllegalArgumentException.class, () -> controller.importFromFile(
                missingFile,
                file -> {
                    throw new AssertionError("Importer should not be called.");
                }
        ));
    }

    /**
     * Purpose: verify importer validation.
     * Scenario: call the import flow with a readable file and no importer.
     * Expected output: a NullPointerException is thrown before any record is processed.
     */
    @Test
    void ensureImporterIsRequired() throws IOException {
        final ImportBulkWeatherDataController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryWeatherDataRepository()
        );

        assertThrows(NullPointerException.class, () -> controller.importFromFile(readableFile(), null));
    }

    /**
     * Purpose: ensure authorization is enforced before importing weather data.
     * Scenario: authorization service rejects the current actor.
     * Expected output: the same exception is propagated and the importer is not invoked.
     */
    @Test
    void ensureAuthorizationFailureStopsImport() throws IOException {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final ImportBulkWeatherDataController controller = controller(authz, new InMemoryWeatherDataRepository());

        assertSame(failure, assertThrows(RuntimeException.class, () -> controller.importFromFile(
                readableFile(),
                file -> {
                    throw new AssertionError("Importer should not be called.");
                }
        )));
    }

    private ImportBulkWeatherDataController controller(
            final CapturingAuthorizationService authz,
            final InMemoryWeatherDataRepository weatherDataRepository) {

        final InMemoryAirControlAreaRepository airControlAreaRepository = new InMemoryAirControlAreaRepository();
        airControlAreaRepository.save(new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        ));

        return new ImportBulkWeatherDataController(
                authz,
                weatherDataRepository,
                airControlAreaRepository
        );
    }

    private Path readableFile() throws IOException {
        final Path file = temporaryDirectory.resolve("weather.import");
        Files.writeString(file, "weather import placeholder");
        return file;
    }

    private WeatherDataImporter fixedImporter(final List<ImportedWeatherMeasurement> measurements) {
        return file -> measurements;
    }

    private ImportedWeatherMeasurement measurement(
            final int sourceLine,
            final String day,
            final String parameter,
            final double value,
            final String unit,
            final double latitude,
            final double longitude) {

        return new ImportedWeatherMeasurement(
                sourceLine,
                day,
                "LISACC",
                "IPMA",
                "STATION",
                parameter,
                value,
                unit,
                1200,
                latitude,
                longitude
        );
    }

    private ImportedWeatherMeasurement importedMeasurement(
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
