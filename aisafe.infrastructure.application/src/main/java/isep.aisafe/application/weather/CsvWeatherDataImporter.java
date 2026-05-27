package isep.aisafe.application.weather;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Imports weather measurements from the initial simple CSV weather data format.
 */
public class CsvWeatherDataImporter implements WeatherDataImporter {

    private static final int EXPECTED_FIELD_COUNT = 10;
    private static final String[] EXPECTED_HEADER = {
            "day",
            "areaCode",
            "providerName",
            "providerType",
            "parameter",
            "value",
            "unit",
            "altitude",
            "latitude",
            "longitude"
    };

    /**
     * Reads a CSV file and maps each data line into an imported weather measurement.
     *
     * @param file CSV file to read
     * @return imported weather measurement records
     * @throws IOException when the CSV file cannot be read
     */
    @Override
    public List<ImportedWeatherMeasurement> importFrom(final Path file) throws IOException {
        Objects.requireNonNull(file, "CSV weather data file is required.");

        final List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV weather data file must contain a header.");
        }

        ensureExpectedHeader(lines.getFirst());

        final List<ImportedWeatherMeasurement> importedMeasurements = new ArrayList<>();
        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            final String line = lines.get(lineIndex);
            if (line.isBlank()) {
                continue;
            }

            importedMeasurements.add(parseMeasurement(line, lineIndex + 1));
        }

        return importedMeasurements;
    }

    private void ensureExpectedHeader(final String headerLine) {
        final String[] header = split(headerLine);
        if (header.length != EXPECTED_HEADER.length) {
            throw new IllegalArgumentException("CSV weather data header must contain 10 fields.");
        }

        for (int i = 0; i < EXPECTED_HEADER.length; i++) {
            if (!EXPECTED_HEADER[i].equals(header[i])) {
                throw new IllegalArgumentException("CSV weather data header is invalid.");
            }
        }
    }

    private ImportedWeatherMeasurement parseMeasurement(final String line, final int sourceLine) {
        final String[] fields = split(line);
        if (fields.length != EXPECTED_FIELD_COUNT) {
            throw new IllegalArgumentException("Line " + sourceLine + " must contain 10 fields.");
        }

        return new ImportedWeatherMeasurement(
                sourceLine,
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                fields[4],
                parseDouble(fields[5], sourceLine, "value"),
                fields[6],
                parseDouble(fields[7], sourceLine, "altitude"),
                parseDouble(fields[8], sourceLine, "latitude"),
                parseDouble(fields[9], sourceLine, "longitude")
        );
    }

    private double parseDouble(final String value, final int sourceLine, final String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Line " + sourceLine + " has invalid numeric value for " + fieldName + ".",
                    ex
            );
        }
    }

    private String[] split(final String line) {
        final String[] fields = line.split(",", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }
}
