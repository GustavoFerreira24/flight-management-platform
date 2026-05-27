package isep.aisafe.application.weather;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Contract for importers that convert external weather data files into system records.
 */
public interface WeatherDataImporter {

    /**
     * Reads an import file and returns the weather measurements found in it.
     *
     * @param file import file to read
     * @return imported weather measurement records
     * @throws IOException when the import file cannot be read
     */
    List<ImportedWeatherMeasurement> importFrom(Path file) throws IOException;
}
