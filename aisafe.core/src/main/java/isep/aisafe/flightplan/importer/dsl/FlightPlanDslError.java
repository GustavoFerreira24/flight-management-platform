package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;

/**
 * Error reported while validating a Core Flight DSL file.
 *
 * @param type the error type
 * @param line the source line where the error was found
 * @param column the source column where the error was found
 * @param message the user-facing error message
 */
public record FlightPlanDslError(FlightPlanDslErrorType type, int line, int column, String message) {

    /**
     * Creates a DSL validation error.
     *
     * @param type the error type
     * @param line the source line
     * @param column the source column
     * @param message the error message
     */
    public FlightPlanDslError {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(message, "message cannot be null");

        if (line < 1) {
            throw new IllegalArgumentException("line must be greater than or equal to 1");
        }

        if (column < 0) {
            throw new IllegalArgumentException("column must be greater than or equal to 0");
        }
    }
}
