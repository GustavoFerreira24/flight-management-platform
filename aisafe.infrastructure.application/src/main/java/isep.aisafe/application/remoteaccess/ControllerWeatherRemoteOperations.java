package isep.aisafe.application.remoteaccess;

import isep.aisafe.application.weather.BulkWeatherImportError;
import isep.aisafe.application.weather.BulkWeatherImportResult;
import isep.aisafe.application.weather.ConsultWeatherDataController;
import isep.aisafe.application.weather.ImportBulkWeatherDataController;
import isep.aisafe.application.weather.RegisterWeatherDataController;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherMeasurement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Production adapter that delegates Weather Person remote commands to existing EAPLI controllers.
 */
public class ControllerWeatherRemoteOperations implements WeatherRemoteOperations {

    private final RegisterWeatherDataController registerWeatherDataController;
    private final ImportBulkWeatherDataController importBulkWeatherDataController;
    private final ConsultWeatherDataController consultWeatherDataController;

    public ControllerWeatherRemoteOperations() {
        this(
                new RegisterWeatherDataController(),
                new ImportBulkWeatherDataController(),
                new ConsultWeatherDataController()
        );
    }

    ControllerWeatherRemoteOperations(
            final RegisterWeatherDataController registerWeatherDataController,
            final ImportBulkWeatherDataController importBulkWeatherDataController,
            final ConsultWeatherDataController consultWeatherDataController) {

        if (registerWeatherDataController == null) {
            throw new IllegalArgumentException("Register weather data controller cannot be null.");
        }
        if (importBulkWeatherDataController == null) {
            throw new IllegalArgumentException("Import bulk weather data controller cannot be null.");
        }
        if (consultWeatherDataController == null) {
            throw new IllegalArgumentException("Consult weather data controller cannot be null.");
        }

        this.registerWeatherDataController = registerWeatherDataController;
        this.importBulkWeatherDataController = importBulkWeatherDataController;
        this.consultWeatherDataController = consultWeatherDataController;
    }

    @Override
    public String registerWeatherData(
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

        final WeatherData weatherData = registerWeatherDataController.registerWeatherMeasurement(
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

        return "Weather data registered: " + weatherData.identity()
                + "; measurements=" + weatherData.measurements().size();
    }

    @Override
    public String importBulkWeatherData(final String serverFilePath) {
        try {
            return formatImportResult(importBulkWeatherDataController.importFromFile(Path.of(serverFilePath)));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not import weather data file: " + e.getMessage(), e);
        }
    }

    @Override
    public String consultWeatherData(final String day, final String airControlAreaCode) {
        final Optional<WeatherData> weatherData = consultWeatherDataController.consultWeatherData(
                day,
                airControlAreaCode
        );

        return weatherData
                .map(this::formatWeatherData)
                .orElse("No weather data found.");
    }

    private String formatImportResult(final BulkWeatherImportResult result) {
        final String summary = "Weather data import finished: read=" + result.readRecords()
                + "; imported=" + result.importedRecords()
                + "; errors=" + result.errors().size();

        if (!result.hasErrors()) {
            return summary;
        }

        return summary + "; " + result.errors().stream()
                .map(this::formatImportError)
                .collect(Collectors.joining("; "));
    }

    private String formatImportError(final BulkWeatherImportError error) {
        return "line " + error.sourceLine() + ": " + error.message();
    }

    private String formatWeatherData(final WeatherData weatherData) {
        final String measurements = weatherData.measurements().stream()
                .map(this::formatMeasurement)
                .collect(Collectors.joining("; "));

        return "Weather data: " + weatherData.identity()
                + "; day=" + weatherData.day()
                + "; area=" + weatherData.airControlAreaCode()
                + "; measurements=" + weatherData.measurements().size()
                + (measurements.isBlank() ? "" : "; " + measurements);
    }

    private String formatMeasurement(final WeatherMeasurement measurement) {
        return measurement.parameter()
                + "=" + measurement.value()
                + " " + measurement.unit()
                + " at " + measurement.altitude()
                + "m"
                + " from " + measurement.provider().name()
                + " (types=" + measurement.provider().supportedTypes() + ")";
    }
}
