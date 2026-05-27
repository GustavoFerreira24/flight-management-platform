package isep.aisafe.application.weather;

import java.util.List;
import java.util.Objects;

/**
 * Result of a bulk weather data import execution.
 */
public final class BulkWeatherImportResult {

    private final int readRecords;
    private final int importedRecords;
    private final List<BulkWeatherImportError> errors;

    private BulkWeatherImportResult(
            final int readRecords,
            final int importedRecords,
            final List<BulkWeatherImportError> errors) {

        if (readRecords < 0) {
            throw new IllegalArgumentException("Read records cannot be negative.");
        }

        if (importedRecords < 0) {
            throw new IllegalArgumentException("Imported records cannot be negative.");
        }

        if (importedRecords > readRecords) {
            throw new IllegalArgumentException("Imported records cannot exceed read records.");
        }

        this.readRecords = readRecords;
        this.importedRecords = importedRecords;
        this.errors = List.copyOf(Objects.requireNonNull(errors, "errors cannot be null"));
    }

    /**
     * Creates a bulk weather import result.
     *
     * @param readRecords number of records read from the source
     * @param importedRecords number of records persisted successfully
     * @param errors rejected record errors
     * @return bulk weather import result
     */
    public static BulkWeatherImportResult valueOf(
            final int readRecords,
            final int importedRecords,
            final List<BulkWeatherImportError> errors) {

        return new BulkWeatherImportResult(readRecords, importedRecords, errors);
    }

    /**
     * @return number of records read from the source
     */
    public int readRecords() {
        return readRecords;
    }

    /**
     * @return number of records persisted successfully
     */
    public int importedRecords() {
        return importedRecords;
    }

    /**
     * @return rejected record errors
     */
    public List<BulkWeatherImportError> errors() {
        return errors;
    }

    /**
     * @return true when all read records were imported
     */
    public boolean isFullyImported() {
        return readRecords == importedRecords && errors.isEmpty();
    }

    /**
     * @return true when at least one record was rejected
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
