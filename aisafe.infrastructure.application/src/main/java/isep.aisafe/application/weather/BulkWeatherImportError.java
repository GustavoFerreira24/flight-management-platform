package isep.aisafe.application.weather;

import java.util.Objects;

/**
 * Describes why one weather import record could not be persisted.
 */
public final class BulkWeatherImportError {

    private final int sourceLine;
    private final String message;

    private BulkWeatherImportError(final int sourceLine, final String message) {
        if (sourceLine < 1) {
            throw new IllegalArgumentException("Source line must be positive.");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Import error message is required.");
        }

        this.sourceLine = sourceLine;
        this.message = message.trim();
    }

    /**
     * Creates an import error for a source record.
     *
     * @param sourceLine original line or record number in the import source
     * @param message clear error message
     * @return import error
     */
    public static BulkWeatherImportError valueOf(final int sourceLine, final String message) {
        return new BulkWeatherImportError(sourceLine, message);
    }

    /**
     * @return original line or record number in the import source
     */
    public int sourceLine() {
        return sourceLine;
    }

    /**
     * @return clear error message
     */
    public String message() {
        return message;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BulkWeatherImportError)) {
            return false;
        }
        final BulkWeatherImportError that = (BulkWeatherImportError) other;
        return sourceLine == that.sourceLine && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceLine, message);
    }
}
