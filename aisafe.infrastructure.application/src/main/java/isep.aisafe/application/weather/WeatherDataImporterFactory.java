package isep.aisafe.application.weather;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Selects the weather data importer that supports a given import file format.
 */
public class WeatherDataImporterFactory {

    /**
     * Returns an importer for the provided weather data import file.
     *
     * @param file import file whose format must be supported
     * @return weather data importer for the file format
     */
    public WeatherDataImporter importerFor(final Path file) {
        Objects.requireNonNull(file, "Weather data import file is required.");

        final String fileName = Objects.requireNonNull(file.getFileName(), "Weather data import file name is required.")
                .toString()
                .toLowerCase(Locale.ROOT);

        if (fileName.endsWith(".csv")) {
            return new CsvWeatherDataImporter();
        }

        throw new IllegalArgumentException("Unsupported weather data import file format.");
    }
}
