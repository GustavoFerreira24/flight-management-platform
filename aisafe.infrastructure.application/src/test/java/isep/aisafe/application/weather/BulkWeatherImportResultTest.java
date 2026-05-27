package isep.aisafe.application.weather;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the value objects used to report bulk weather import results.
 */
class BulkWeatherImportResultTest {

    /**
     * Purpose: verify successful import result creation.
     * Scenario: all read records were imported and no errors exist.
     * Expected output: the result is marked as fully imported.
     */
    @Test
    void ensureSuccessfulResultCanBeCreated() {
        final BulkWeatherImportResult result = BulkWeatherImportResult.valueOf(2, 2, List.of());

        assertEquals(2, result.readRecords());
        assertEquals(2, result.importedRecords());
        assertTrue(result.errors().isEmpty());
        assertTrue(result.isFullyImported());
        assertFalse(result.hasErrors());
    }

    /**
     * Purpose: verify partial import result creation.
     * Scenario: one read record was rejected.
     * Expected output: the result exposes the rejection and is not marked as fully imported.
     */
    @Test
    void ensurePartialResultCanBeCreated() {
        final BulkWeatherImportError error = BulkWeatherImportError.valueOf(4, "Invalid weather data.");
        final BulkWeatherImportResult result = BulkWeatherImportResult.valueOf(2, 1, List.of(error));

        assertEquals(List.of(error), result.errors());
        assertFalse(result.isFullyImported());
        assertTrue(result.hasErrors());
        assertThrows(UnsupportedOperationException.class, () -> result.errors().add(error));
    }

    /**
     * Purpose: verify import result invariants.
     * Scenario: create results with negative counts, inconsistent counts or null errors.
     * Expected output: invalid data is rejected.
     */
    @Test
    void ensureResultRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> BulkWeatherImportResult.valueOf(-1, 0, List.of()));
        assertThrows(IllegalArgumentException.class, () -> BulkWeatherImportResult.valueOf(1, -1, List.of()));
        assertThrows(IllegalArgumentException.class, () -> BulkWeatherImportResult.valueOf(1, 2, List.of()));
        assertThrows(NullPointerException.class, () -> BulkWeatherImportResult.valueOf(1, 0, null));
    }

    /**
     * Purpose: verify import error creation.
     * Scenario: create an error with source line and message.
     * Expected output: the error exposes the normalized message and supports equality.
     */
    @Test
    void ensureImportErrorCanBeCreated() {
        final BulkWeatherImportError error = BulkWeatherImportError.valueOf(2, " Invalid area. ");
        final BulkWeatherImportError sameError = BulkWeatherImportError.valueOf(2, "Invalid area.");
        final BulkWeatherImportError otherError = BulkWeatherImportError.valueOf(3, "Invalid area.");

        assertEquals(2, error.sourceLine());
        assertEquals("Invalid area.", error.message());
        assertEquals(error, sameError);
        assertEquals(error.hashCode(), sameError.hashCode());
        assertNotEquals(error, otherError);
        assertNotEquals(error, "Invalid area.");
    }

    /**
     * Purpose: verify import error invariants.
     * Scenario: create errors with invalid source line or message.
     * Expected output: invalid data is rejected.
     */
    @Test
    void ensureImportErrorRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> BulkWeatherImportError.valueOf(0, "Invalid area."));
        assertThrows(IllegalArgumentException.class, () -> BulkWeatherImportError.valueOf(1, null));
        assertThrows(IllegalArgumentException.class, () -> BulkWeatherImportError.valueOf(1, " "));
    }
}
