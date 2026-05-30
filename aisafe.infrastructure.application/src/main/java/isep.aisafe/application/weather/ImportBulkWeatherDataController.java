package isep.aisafe.application.weather;

import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;
import isep.aisafe.weatherdata.domain.WeatherDataRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates the bulk import of weather data records from an external file.
 */
public class ImportBulkWeatherDataController {

    private final AisafeAuthorizationService authz;
    private final WeatherDataRepository weatherDataRepository;
    private final AirControlAreaRepository airControlAreaRepository;
    private final WeatherDataImporterFactory importerFactory;

    /**
     * Creates the controller with production authorization and persistence services.
     */
    public ImportBulkWeatherDataController() {
        authz = new AisafeAuthorizationService();
        weatherDataRepository = null;
        airControlAreaRepository = null;
        importerFactory = new WeatherDataImporterFactory();
    }

    ImportBulkWeatherDataController(
            final AisafeAuthorizationService authz,
            final WeatherDataRepository weatherDataRepository,
            final AirControlAreaRepository airControlAreaRepository) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.weatherDataRepository = Objects.requireNonNull(weatherDataRepository, "weatherDataRepository cannot be null");
        this.airControlAreaRepository = Objects.requireNonNull(airControlAreaRepository, "airControlAreaRepository cannot be null");
        importerFactory = new WeatherDataImporterFactory();
    }

    ImportBulkWeatherDataController(
            final AisafeAuthorizationService authz,
            final WeatherDataRepository weatherDataRepository,
            final AirControlAreaRepository airControlAreaRepository,
            final WeatherDataImporterFactory importerFactory) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.weatherDataRepository = Objects.requireNonNull(weatherDataRepository, "weatherDataRepository cannot be null");
        this.airControlAreaRepository = Objects.requireNonNull(airControlAreaRepository, "airControlAreaRepository cannot be null");
        this.importerFactory = Objects.requireNonNull(importerFactory, "importerFactory cannot be null");
    }

    /**
     * Imports weather data records from a readable file using the importer selected for its format.
     *
     * @param file import file to read
     * @return import result with persisted and rejected record counts
     * @throws IOException when the import file cannot be read by the selected importer
     */
    public BulkWeatherImportResult importFromFile(final Path file) throws IOException {
        return importFromFile(file, importerFactory.importerFor(file));
    }

    /**
     * Imports weather data records from a readable file through the provided importer.
     *
     * @param file import file to read
     * @param importer importer capable of converting the file into weather measurements
     * @return import result with persisted and rejected record counts
     * @throws IOException when the import file cannot be read by the importer
     */
    public BulkWeatherImportResult importFromFile(final Path file, final WeatherDataImporter importer) throws IOException {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        ensureReadableFile(file);
        Objects.requireNonNull(importer, "Weather data importer is required.");

        final List<ImportedWeatherMeasurement> importedMeasurements = importer.importFrom(file);
        final List<BulkWeatherImportError> errors = new ArrayList<>();
        int persistedRecords = 0;

        for (final ImportedWeatherMeasurement importedMeasurement : importedMeasurements) {
            try {
                persist(importedMeasurement);
                persistedRecords++;
            } catch (IllegalArgumentException ex) {
                errors.add(BulkWeatherImportError.valueOf(importedMeasurement.sourceLine(), ex.getMessage()));
            }
        }

        return BulkWeatherImportResult.valueOf(importedMeasurements.size(), persistedRecords, errors);
    }

    private void persist(final ImportedWeatherMeasurement importedMeasurement) {
        final RegisterWeatherDataController registerController = new RegisterWeatherDataController(
                authz,
                weatherDataRepository(),
                airControlAreaRepository()
        );

        registerController.registerWeatherMeasurement(
                importedMeasurement.day(),
                importedMeasurement.areaCode(),
                importedMeasurement.providerName(),
                importedMeasurement.providerType(),
                importedMeasurement.parameter(),
                importedMeasurement.value(),
                importedMeasurement.unit(),
                importedMeasurement.altitude(),
                importedMeasurement.latitude(),
                importedMeasurement.longitude()
        );
    }

    private void ensureReadableFile(final Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Weather data import file is required.");
        }

        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new IllegalArgumentException("Weather data import file must exist and be readable.");
        }
    }

    private WeatherDataRepository weatherDataRepository() {
        if (weatherDataRepository != null) {
            return weatherDataRepository;
        }

        return PersistenceContext.repositories().weatherData();
    }

    private AirControlAreaRepository airControlAreaRepository() {
        if (airControlAreaRepository != null) {
            return airControlAreaRepository;
        }

        return PersistenceContext.repositories().airControlAreas();
    }
}
